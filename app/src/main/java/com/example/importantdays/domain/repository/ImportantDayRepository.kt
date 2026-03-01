package com.example.importantdays.domain.repository

import com.example.importantdays.domain.model.ImportantDay
import kotlinx.coroutines.flow.Flow

interface ImportantDayRepository {
    fun getAllDays(): Flow<List<ImportantDay>>

    suspend fun getDayById(id: Long): ImportantDay?

    fun getFavoriteDays(): Flow<List<ImportantDay>>

    suspend fun getDaysNeedingReminders(): List<ImportantDay>

    suspend fun insertDay(day: ImportantDay): Long

    suspend fun updateDay(day: ImportantDay)

    suspend fun deleteDay(day: ImportantDay)

    suspend fun deleteDayById(id: Long)

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
}
