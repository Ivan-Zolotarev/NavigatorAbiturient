package ru.navigator.abiturient.data.repository

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.navigator.abiturient.BuildConfig
import ru.navigator.abiturient.data.api.CollegeApi
import ru.navigator.abiturient.data.dto.CollegeDto
import ru.navigator.abiturient.data.dto.CollegesResponseDto
import ru.navigator.abiturient.data.local.CollegeCacheDao
import ru.navigator.abiturient.data.local.CollegeCacheEntity
import ru.navigator.abiturient.data.mapper.toDomain
import ru.navigator.abiturient.domain.model.College
import ru.navigator.abiturient.domain.model.CollegeType
import java.io.IOException

data class CollegeSearchFilters(
    val budgetOnly: Boolean = false,
    val paidOnly: Boolean = false,
    val minPassingScore: Int? = null,
    val district: String? = null,
)

class CollegeRepository(
    private val context: Context,
    private val collegeCacheDao: CollegeCacheDao,
    private val collegeApi: CollegeApi,
    private val gson: Gson = Gson(),
) {
    private var memoryCache: List<College>? = null

    suspend fun hasCachedData(): Boolean = withContext(Dispatchers.IO) {
        memoryCache != null || collegeCacheDao.count() > 0
    }

    suspend fun warmUp(): Result<LoadResult<Unit>> = withContext(Dispatchers.IO) {
        runCatching {
            val result = refreshFromSource()
            LoadResult(Unit, isFromCache = result.isFromCache)
        }
    }

    suspend fun clearCache() = withContext(Dispatchers.IO) {
        memoryCache = null
        collegeCacheDao.clear()
    }

    suspend fun getCollegesByType(type: CollegeType): Result<LoadResult<List<College>>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val colleges = loadColleges()
                LoadResult(
                    data = colleges.filter { it.type == type },
                    isFromCache = collegesLoadWasFromCache,
                )
            }
        }

    suspend fun getCollegeById(id: Int): Result<LoadResult<College>> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (!BuildConfig.USE_MOCK) {
                    try {
                        val college = collegeApi.getCollege(id).toDomain()
                        mergeIntoCache(college)
                        return@runCatching LoadResult(college)
                    } catch (_: IOException) {
                        // fallback to cached list below
                    }
                }
                val college = loadColleges().first { it.id == id }
                LoadResult(college, isFromCache = collegesLoadWasFromCache)
            }
        }

    suspend fun searchColleges(
        query: String,
        filters: CollegeSearchFilters = CollegeSearchFilters(),
    ): Result<LoadResult<List<College>>> = withContext(Dispatchers.IO) {
        runCatching {
            if (!BuildConfig.USE_MOCK) {
                try {
                    val response = collegeApi.searchColleges(
                        query = query,
                        budget = filters.budgetOnly.takeIf { it },
                        paid = filters.paidOnly.takeIf { it },
                        minScore = filters.minPassingScore,
                        district = filters.district,
                    )
                    val colleges = response.colleges.map { it.toDomain() }
                    return@runCatching LoadResult(colleges)
                } catch (_: IOException) {
                    // local filter fallback
                }
            }
            val normalizedQuery = query.trim().lowercase()
            val filtered = loadColleges().filter { college ->
                val matchesQuery = normalizedQuery.isEmpty() ||
                    college.name.lowercase().contains(normalizedQuery) ||
                    college.specialties.any { it.name.lowercase().contains(normalizedQuery) }
                val matchesDistrict = filters.district.isNullOrBlank() ||
                    college.district.equals(filters.district, ignoreCase = true)
                val matchesBudget = !filters.budgetOnly ||
                    college.specialties.any { (it.budgetPlaces ?: 0) > 0 }
                val matchesPaid = !filters.paidOnly ||
                    college.specialties.any { (it.paidPlaces ?: 0) > 0 }
                val matchesScore = filters.minPassingScore == null ||
                    college.specialties.any {
                        (it.passingScore ?: 0) >= filters.minPassingScore
                    }
                matchesQuery && matchesDistrict && matchesBudget && matchesPaid && matchesScore
            }
            LoadResult(filtered, isFromCache = collegesLoadWasFromCache)
        }
    }

    suspend fun getDistricts(): List<String> = withContext(Dispatchers.IO) {
        loadColleges()
            .mapNotNull { it.district }
            .distinct()
            .sorted()
    }

    @Volatile
    private var collegesLoadWasFromCache: Boolean = false

    private suspend fun loadColleges(): List<College> {
        memoryCache?.let { return it }
        val cached = loadFromRoom()
        if (cached.isNotEmpty()) {
            memoryCache = cached
            collegesLoadWasFromCache = true
            return cached
        }
        val result = refreshFromSource()
        return result.data
    }

    private suspend fun refreshFromSource(): LoadResult<List<College>> {
        if (BuildConfig.USE_MOCK) {
            val colleges = loadFromAssets()
            saveToCache(colleges)
            memoryCache = colleges
            collegesLoadWasFromCache = false
            return LoadResult(colleges)
        }
        return try {
            val response = collegeApi.getColleges(type = null)
            val colleges = response.colleges.map { it.toDomain() }
            saveToCache(colleges)
            memoryCache = colleges
            collegesLoadWasFromCache = false
            LoadResult(colleges)
        } catch (e: IOException) {
            val cached = loadFromRoom()
            if (cached.isNotEmpty()) {
                memoryCache = cached
                collegesLoadWasFromCache = true
                LoadResult(cached, isFromCache = true)
            } else {
                throw e
            }
        }
    }

    private suspend fun loadFromRoom(): List<College> {
        return collegeCacheDao.getAll().map { entity ->
            gson.fromJson(entity.json, CollegeDto::class.java).toDomain()
        }
    }

    private suspend fun mergeIntoCache(college: College) {
        val current = memoryCache ?: loadFromRoom()
        val updated = current.filter { it.id != college.id } + college
        saveToCache(updated)
        memoryCache = updated
    }

    private suspend fun saveToCache(colleges: List<College>) {
        collegeCacheDao.clear()
        collegeCacheDao.insertAll(
            colleges.map { college -> college.toCacheEntity() },
        )
    }

    private fun College.toCacheEntity(): CollegeCacheEntity = CollegeCacheEntity(
        id = id,
        type = CollegeType.toApiValue(type),
        json = gson.toJson(
            CollegeDto(
                id = id,
                name = name,
                type = CollegeType.toApiValue(type),
                shortDescription = shortDescription,
                district = district,
                address = address,
                websiteUrl = websiteUrl,
                specialties = specialties.map { spec ->
                    ru.navigator.abiturient.data.dto.SpecialtyDto(
                        id = spec.id,
                        name = spec.name,
                        budgetPlaces = spec.budgetPlaces,
                        paidPlaces = spec.paidPlaces,
                        passingScore = spec.passingScore,
                        admissionInfo = spec.admissionInfo,
                    )
                },
                tuitionCost = tuitionCost,
                installmentAvailable = installmentAvailable,
                specialAdmissionConditions = specialAdmissionConditions,
                hasDormitory = hasDormitory,
            ),
        ),
    )

    private fun loadFromAssets(): List<College> {
        val json = context.assets.open("mock_colleges.json")
            .bufferedReader()
            .use { it.readText() }
        val response = gson.fromJson(json, CollegesResponseDto::class.java)
        return response.colleges.map { it.toDomain() }
    }
}
