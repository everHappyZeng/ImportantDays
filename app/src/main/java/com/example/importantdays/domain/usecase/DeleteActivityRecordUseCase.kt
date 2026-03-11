package com.example.importantdays.domain.usecase

import com.example.importantdays.domain.repository.ActivityRecordRepository

class DeleteActivityRecordUseCase(
    private val repository: ActivityRecordRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteRecordById(id)
    }
}
