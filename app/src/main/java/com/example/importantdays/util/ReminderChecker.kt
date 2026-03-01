package com.example.importantdays.util

import android.content.Context
import android.util.Log
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.repository.ImportantDayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime

object ReminderChecker {

    private val firedReminders = mutableSetOf<String>()

    fun start(context: Context, repository: ImportantDayRepository, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            while (true) {
                checkReminders(context, repository)
                delay(60_000)
            }
        }
    }

    suspend fun checkReminders(context: Context, repository: ImportantDayRepository) {
        try {
            val now = LocalDateTime.now()
            val days = repository.getDaysNeedingReminders()
            days.forEach { day ->
                val reminderTime = calculateReminderDateTime(day)
                // 提醒时间在过去60秒内，且还没发过
                val key = "${day.id}_${reminderTime}"
                if (!reminderTime.isAfter(now) && reminderTime.isAfter(now.minusSeconds(60)) && key !in firedReminders) {
                    firedReminders.add(key)
                    NotificationHelper.sendReminderNotification(
                        context,
                        day.title,
                        "提醒：${day.title} 即将到来"
                    )
                    Log.d("ReminderChecker", "发送提醒：${day.title} at $reminderTime")
                }
            }
        } catch (e: Exception) {
            Log.e("ReminderChecker", "检查提醒时出错", e)
        }
    }

    private fun calculateReminderDateTime(day: ImportantDay): LocalDateTime {
        val targetDate = day.nextOccurrence.minusDays(day.reminderDaysBefore.toLong())
        val time = if (day.timeEnabled && day.time != null) day.time else LocalTime.of(9, 0)
        return LocalDateTime.of(targetDate, time)
    }
}
