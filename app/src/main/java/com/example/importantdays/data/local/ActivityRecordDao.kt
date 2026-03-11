package com.example.importantdays.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.importantdays.data.model.ActivityRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityRecordDao {
    @Query("SELECT * FROM activity_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<ActivityRecordEntity>>

    @Query("SELECT * FROM activity_records WHERE personId = :personId ORDER BY date DESC")
    fun getRecordsByPerson(personId: Long): Flow<List<ActivityRecordEntity>>

    @Query("SELECT * FROM activity_records WHERE importantDayId = :importantDayId ORDER BY date DESC")
    fun getRecordsByImportantDay(importantDayId: Long): Flow<List<ActivityRecordEntity>>

    @Query("SELECT * FROM activity_records WHERE personId = :personId AND importantDayId = :importantDayId ORDER BY date DESC")
    fun getRecordsByPersonAndImportantDay(personId: Long, importantDayId: Long): Flow<List<ActivityRecordEntity>>

    @Query("SELECT * FROM activity_records WHERE id = :id")
    suspend fun getRecordById(id: Long): ActivityRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ActivityRecordEntity): Long

    @Update
    suspend fun updateRecord(record: ActivityRecordEntity)

    @Delete
    suspend fun deleteRecord(record: ActivityRecordEntity)

    @Query("DELETE FROM activity_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)
}
