package com.example.importantdays.domain.usecase

import com.example.importantdays.domain.repository.ImportantDayRepository

class ToggleFavoriteUseCase(
    private val repository: ImportantDayRepository
) {
    suspend operator fun invoke(id: Long, isFavorite: Boolean) {
        repository.toggleFavorite(id, isFavorite)
    }
}
