package ru.navigator.abiturient.domain.model

data class FavoriteItem(
    val entityId: Int,
    val entityType: FavoriteEntityType,
    val title: String,
    val subtitle: String?,
    val collegeId: Int?,
)
