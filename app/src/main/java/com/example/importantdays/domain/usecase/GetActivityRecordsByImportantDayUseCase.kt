package com.example.importantdays.domain.usecase

import com.example.importantdays.domain.model.ActivityRecord
import com.example.importantdays.domain.repository.ActivityRecordRepository
import kotlinx.coroutines.flow.Flow

class GetActivityRecordsByImportantDayUseCase(
    private val repository: ActivityRecordRepository
) {
    operator fun invoke(importantDayId: Long): Flow<List<ActivityRecord>> {
        return repository.getRecordsByImportantDay(importantDayId)
    }
}
