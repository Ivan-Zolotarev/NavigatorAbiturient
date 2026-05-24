package ru.navigator.abiturient.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "college_cache")
data class CollegeCacheEntity(
    @PrimaryKey val id: Int,
    val type: String,
    val json: String,
)
