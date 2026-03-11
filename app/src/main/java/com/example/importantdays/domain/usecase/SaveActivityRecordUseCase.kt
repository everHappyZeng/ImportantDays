package com.example.importantdays.domain.usecase

import com.example.importantdays.domain.model.ActivityRecord
import com.example.importantdays.domain.repository.ActivityRecordRepository

class SaveActivityRecordUseCase(
    private val repository: ActivityRecordRepository
) {
    suspend operator fun invoke(record: ActivityRecord): Long {
        return if (record.id == 0L) {
            repository.insertRecord(record)
        } else {
            repository.updateRecord(record)
            record.id
        }
    }
}
