package ru.navigator.abiturient.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val key: String,
    val entityId: Int,
    val entityType: String,
    val title: String,
    val subtitle: String?,
    val collegeId: Int?,
)
