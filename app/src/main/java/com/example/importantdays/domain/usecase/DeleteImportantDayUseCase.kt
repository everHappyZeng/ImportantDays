package com.example.importantdays.domain.usecase

import android.content.Context
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.repository.ImportantDayRepository
import com.example.importantdays.util.ReminderScheduler

class DeleteImportantDayUseCase(
    private val repository: ImportantDayRepository,
    private val context: Context
) {
    suspend operator fun invoke(day: ImportantDay) {
        repository.deleteDay(day)
        ReminderScheduler.cancelReminder(context, day.id)
    }

    suspend operator fun invoke(id: Long) {
        repository.deleteDayById(id)
        ReminderScheduler.cancelReminder(context, id)
    }
}
