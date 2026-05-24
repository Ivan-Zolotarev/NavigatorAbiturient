package ru.navigator.abiturient.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CollegeCacheDao {
    @Query("SELECT COUNT(*) FROM college_cache")
    suspend fun count(): Int

    @Query("SELECT * FROM college_cache")
    suspend fun getAll(): List<CollegeCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CollegeCacheEntity>)

    @Query("DELETE FROM college_cache")
    suspend fun clear()
}
