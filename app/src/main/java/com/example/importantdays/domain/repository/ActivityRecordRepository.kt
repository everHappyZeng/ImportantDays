package com.example.importantdays.domain.repository

import com.example.importantdays.domain.model.ActivityRecord
import kotlinx.coroutines.flow.Flow

interface ActivityRecordRepository {
    fun getAllRecords(): Flow<List<ActivityRecord>>
    fun getRecordsByPerson(personId: Long): Flow<List<ActivityRecord>>
    fun getRecordsByImportantDay(importantDayId: Long): Flow<List<ActivityRecord>>
    fun getRecordsByPersonAndImportantDay(personId: Long, importantDayId: Long): Flow<List<ActivityRecord>>
    suspend fun getRecordById(id: Long): ActivityRecord?
    suspend fun insertRecord(record: ActivityRecord): Long
    suspend fun updateRecord(record: ActivityRecord)
    suspend fun deleteRecord(record: ActivityRecord)
    suspend fun deleteRecordById(id: Long)
}
