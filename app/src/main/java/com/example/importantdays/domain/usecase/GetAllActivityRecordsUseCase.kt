package com.example.importantdays.domain.usecase

import com.example.importantdays.domain.model.ActivityRecord
import com.example.importantdays.domain.repository.ActivityRecordRepository
import kotlinx.coroutines.flow.Flow

class GetAllActivityRecordsUseCase(
    private val repository: ActivityRecordRepository
) {
    operator fun invoke(): Flow<List<ActivityRecord>> {
        return repository.getAllRecords()
    }
}
