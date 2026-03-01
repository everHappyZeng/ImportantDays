package com.example.importantdays.domain.usecase

import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.repository.ImportantDayRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteDaysUseCase(
    private val repository: ImportantDayRepository
) {
    operator fun invoke(): Flow<List<ImportantDay>> {
        return repository.getFavoriteDays()
    }
}
