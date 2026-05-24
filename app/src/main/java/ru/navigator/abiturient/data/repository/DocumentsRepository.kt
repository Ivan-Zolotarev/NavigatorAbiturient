package ru.navigator.abiturient.data.repository

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.navigator.abiturient.BuildConfig
import ru.navigator.abiturient.data.api.CollegeApi
import ru.navigator.abiturient.data.dto.DocumentsResponseDto
import ru.navigator.abiturient.data.dto.FaqResponseDto
import ru.navigator.abiturient.data.mapper.toDomain
import ru.navigator.abiturient.domain.model.DocumentItem
import ru.navigator.abiturient.domain.model.FaqItem
import java.io.IOException

class DocumentsRepository(
    private val context: Context,
    private val collegeApi: CollegeApi,
    private val gson: Gson = Gson(),
) {
    suspend fun getDocuments(): Result<LoadResult<List<DocumentItem>>> = withContext(Dispatchers.IO) {
        runCatching {
            if (!BuildConfig.USE_MOCK) {
                try {
                    val docs = collegeApi.getDocuments().documents.map { it.toDomain() }
                    return@runCatching LoadResult(docs)
                } catch (_: IOException) {
                    // fallback to assets
                }
            }
            LoadResult(loadDocumentsFromAssets(), isFromCache = !BuildConfig.USE_MOCK)
        }
    }

    suspend fun getFaq(): Result<LoadResult<List<FaqItem>>> = withContext(Dispatchers.IO) {
        runCatching {
            if (!BuildConfig.USE_MOCK) {
                try {
                    val items = collegeApi.getFaq().items.map { it.toDomain() }
                    return@runCatching LoadResult(items)
                } catch (_: IOException) {
                    // fallback to assets
                }
            }
            LoadResult(loadFaqFromAssets(), isFromCache = !BuildConfig.USE_MOCK)
        }
    }

    private fun loadDocumentsFromAssets(): List<DocumentItem> {
        val json = context.assets.open("mock_documents.json")
            .bufferedReader()
            .use { it.readText() }
        return gson.fromJson(json, DocumentsResponseDto::class.java)
            .documents
            .map { it.toDomain() }
    }

    private fun loadFaqFromAssets(): List<FaqItem> {
        val json = context.assets.open("mock_faq.json")
            .bufferedReader()
            .use { it.readText() }
        return gson.fromJson(json, FaqResponseDto::class.java)
            .items
            .map { it.toDomain() }
    }
}
