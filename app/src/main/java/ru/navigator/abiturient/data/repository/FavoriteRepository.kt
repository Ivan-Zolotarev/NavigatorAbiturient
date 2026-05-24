package ru.navigator.abiturient.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.navigator.abiturient.data.local.FavoriteDao
import ru.navigator.abiturient.data.local.FavoriteEntity
import ru.navigator.abiturient.domain.model.College
import ru.navigator.abiturient.domain.model.FavoriteEntityType
import ru.navigator.abiturient.domain.model.FavoriteItem
import ru.navigator.abiturient.domain.model.Specialty

class FavoriteRepository(
    private val favoriteDao: FavoriteDao,
) {
    fun observeFavorites(): Flow<List<FavoriteItem>> =
        favoriteDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    fun observeFavoriteKeys(): Flow<Set<String>> =
        favoriteDao.observeFavoriteKeys().map { it.toSet() }

    fun observeIsFavorite(entityType: FavoriteEntityType, entityId: Int): Flow<Boolean> =
        favoriteDao.observeIsFavorite(key(entityType, entityId))

    suspend fun addCollege(college: College) {
        favoriteDao.insert(
            FavoriteEntity(
                key = key(FavoriteEntityType.COLLEGE, college.id),
                entityId = college.id,
                entityType = FavoriteEntityType.COLLEGE.name,
                title = college.name,
                subtitle = college.shortDescription,
                collegeId = null,
            ),
        )
    }

    suspend fun remove(entityType: FavoriteEntityType, entityId: Int) {
        favoriteDao.delete(key(entityType, entityId))
    }

    suspend fun toggleCollegeFavorite(college: College, isCurrentlyFavorite: Boolean) {
        if (isCurrentlyFavorite) {
            remove(FavoriteEntityType.COLLEGE, college.id)
        } else {
            addCollege(college)
        }
    }

    suspend fun toggleSpecialtyFavorite(
        college: College,
        specialty: Specialty,
        isCurrentlyFavorite: Boolean,
    ) {
        if (isCurrentlyFavorite) {
            remove(FavoriteEntityType.SPECIALTY, specialty.id)
        } else {
            favoriteDao.insert(
                FavoriteEntity(
                    key = key(FavoriteEntityType.SPECIALTY, specialty.id),
                    entityId = specialty.id,
                    entityType = FavoriteEntityType.SPECIALTY.name,
                    title = specialty.name,
                    subtitle = college.name,
                    collegeId = college.id,
                ),
            )
        }
    }

    private fun key(entityType: FavoriteEntityType, entityId: Int): String =
        "${entityType.name}_$entityId"

    private fun FavoriteEntity.toDomain(): FavoriteItem = FavoriteItem(
        entityId = entityId,
        entityType = FavoriteEntityType.fromStorage(entityType),
        title = title,
        subtitle = subtitle,
        collegeId = collegeId,
    )
}
