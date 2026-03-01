package com.example.importantdays.domain.usecase

import android.content.Context
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.repository.ImportantDayRepository
import com.example.importantdays.util.ReminderScheduler

class SaveImportantDayUseCase(
    private val repository: ImportantDayRepository,
    private val context: Context
) {
    suspend operator fun invoke(day: ImportantDay): Long {
        val savedId = if (day.id == 0L) {
            repository.insertDay(day)
        } else {
            repository.updateDay(day)
            day.id
        }

        // Schedule or cancel reminder based on settings
        val savedDay = day.copy(id = savedId)
        ReminderScheduler.scheduleReminder(context, savedDay)

        return savedId
    }
}
