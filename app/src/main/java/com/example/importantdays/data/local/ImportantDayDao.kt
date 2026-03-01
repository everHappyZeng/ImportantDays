package com.example.importantdays.data.local

import androidx.room.*
import com.example.importantdays.data.model.ImportantDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImportantDayDao {
    @Query("SELECT * FROM important_days ORDER BY date ASC")
    fun getAllDays(): Flow<List<ImportantDayEntity>>

    @Query("SELECT * FROM important_days WHERE id = :id")
    suspend fun getDayById(id: Long): ImportantDayEntity?

    @Query("SELECT * FROM important_days WHERE isFavorite = 1 ORDER BY date ASC")
    fun getFavoriteDays(): Flow<List<ImportantDayEntity>>

    @Query("SELECT * FROM important_days WHERE reminderEnabled = 1")
    suspend fun getDaysWithReminders(): List<ImportantDayEntity>

    @Query("SELECT * FROM important_days WHERE personId = :personId ORDER BY date ASC")
    fun getDaysByPersonId(personId: Long): Flow<List<ImportantDayEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day: ImportantDayEntity): Long

    @Update
    suspend fun updateDay(day: ImportantDayEntity)

    @Delete
    suspend fun deleteDay(day: ImportantDayEntity)

    @Query("DELETE FROM important_days WHERE id = :id")
    suspend fun deleteDayById(id: Long)

    @Query("UPDATE important_days SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
}
