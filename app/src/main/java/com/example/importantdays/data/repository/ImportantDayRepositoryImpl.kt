package com.example.importantdays.data.repository

import com.example.importantdays.data.local.ImportantDayDao
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.repository.ImportantDayRepository
import com.example.importantdays.util.toDomain
import com.example.importantdays.util.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ImportantDayRepositoryImpl(
    private val dao: ImportantDayDao
) : ImportantDayRepository {

    override fun getAllDays(): Flow<List<ImportantDay>> {
        return dao.getAllDays().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDayById(id: Long): ImportantDay? {
        return dao.getDayById(id)?.toDomain()
    }

    override fun getFavoriteDays(): Flow<List<ImportantDay>> {
        return dao.getFavoriteDays().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDaysNeedingReminders(): List<ImportantDay> {
        return dao.getDaysWithReminders().map { it.toDomain() }
    }

    override suspend fun insertDay(day: ImportantDay): Long {
        return dao.insertDay(day.toEntity())
    }

    override suspend fun updateDay(day: ImportantDay) {
        dao.updateDay(day.toEntity())
    }

    override suspend fun deleteDay(day: ImportantDay) {
        dao.deleteDay(day.toEntity())
    }

    override suspend fun deleteDayById(id: Long) {
        dao.deleteDayById(id)
    }

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        dao.updateFavoriteStatus(id, isFavorite)
    }
}