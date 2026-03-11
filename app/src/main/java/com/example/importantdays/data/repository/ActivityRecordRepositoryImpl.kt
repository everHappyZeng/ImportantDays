package com.example.importantdays.data.repository

import com.example.importantdays.data.local.ActivityRecordDao
import com.example.importantdays.domain.model.ActivityRecord
import com.example.importantdays.domain.repository.ActivityRecordRepository
import com.example.importantdays.util.toDomain
import com.example.importantdays.util.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ActivityRecordRepositoryImpl(
    private val activityRecordDao: ActivityRecordDao
) : ActivityRecordRepository {

    override fun getAllRecords(): Flow<List<ActivityRecord>> {
        return activityRecordDao.getAllRecords().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getRecordsByPerson(personId: Long): Flow<List<ActivityRecord>> {
        return activityRecordDao.getRecordsByPerson(personId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getRecordsByImportantDay(importantDayId: Long): Flow<List<ActivityRecord>> {
        return activityRecordDao.getRecordsByImportantDay(importantDayId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getRecordsByPersonAndImportantDay(
        personId: Long,
        importantDayId: Long
    ): Flow<List<ActivityRecord>> {
        return activityRecordDao.getRecordsByPersonAndImportantDay(personId, importantDayId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getRecordById(id: Long): ActivityRecord? {
        return activityRecordDao.getRecordById(id)?.toDomain()
    }

    override suspend fun insertRecord(record: ActivityRecord): Long {
        return activityRecordDao.insertRecord(record.toEntity())
    }

    override suspend fun updateRecord(record: ActivityRecord) {
        activityRecordDao.updateRecord(record.toEntity())
    }

    override suspend fun deleteRecord(record: ActivityRecord) {
        activityRecordDao.deleteRecord(record.toEntity())
    }

    override suspend fun deleteRecordById(id: Long) {
        activityRecordDao.deleteRecordById(id)
    }
}
