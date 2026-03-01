package com.example.importantdays.domain.usecase

import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.repository.ImportantDayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetUpcomingDaysUseCase(
    private val repository: ImportantDayRepository
) {
    operator fun invoke(): Flow<List<ImportantDay>> {
        return repository.getAllDays().map { days ->
            days.filter { it.isUpcoming }
                .sortedBy { it.daysUntil }
        }
    }
}
