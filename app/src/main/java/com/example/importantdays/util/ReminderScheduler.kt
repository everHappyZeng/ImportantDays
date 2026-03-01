package com.example.importantdays.util

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.worker.ReminderWorker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleReminder(context: Context, day: ImportantDay) {
        if (!day.reminderEnabled) {
            cancelReminder(context, day.id)
            return
        }

        val reminderDate = calculateReminderDate(day)
        val now = LocalDateTime.now()

        if (reminderDate.isBefore(now)) {
            // Reminder time has passed, don't schedule
            return
        }

        val delayMillis = calculateDelay(reminderDate)

        val inputData = Data.Builder()
            .putLong("day_id", day.id)
            .putString("title", day.title)
            .putString("date", DateUtils.formatDateReadable(day.date))
            .putStringArray("notification_channels", day.notificationChannels.map { it.name }.toTypedArray())
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("reminder_${day.id}")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "reminder_${day.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelReminder(context: Context, dayId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("reminder_$dayId")
    }

    private fun calculateReminderDate(day: ImportantDay): LocalDateTime {
        val targetDate = day.nextOccurrence
        val reminderDate = targetDate.minusDays(day.reminderDaysBefore.toLong())
        
        // Use the specific time if enabled, otherwise use 9:00 AM as default
        val reminderTime = if (day.timeEnabled && day.time != null) {
            day.time
        } else {
            LocalTime.of(9, 0)
        }
        
        return LocalDateTime.of(reminderDate, reminderTime)
    }

    private fun calculateDelay(reminderDateTime: LocalDateTime): Long {
        val now = LocalDateTime.now()
        val reminderMillis = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val nowMillis = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return (reminderMillis - nowMillis).coerceAtLeast(0)
    }
}
