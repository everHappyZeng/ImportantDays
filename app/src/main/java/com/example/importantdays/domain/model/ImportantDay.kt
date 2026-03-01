package com.example.importantdays.domain.model

import com.example.importantdays.data.model.DayType
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

data class ImportantDay(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: LocalDate,
    val dayType: DayType,
    val isFavorite: Boolean = false,
    val reminderEnabled: Boolean = false,
    val reminderDaysBefore: Int = 0,
    val notificationChannels: List<NotificationChannel> = listOf(
        NotificationChannel.APP,
        NotificationChannel.SYSTEM
    ),
    val timeEnabled: Boolean = false,
    val time: LocalTime? = null,
    val personId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val nextOccurrence: LocalDate
        get() = calculateNextOccurrence()

    val daysUntil: Long
        get() = ChronoUnit.DAYS.between(LocalDate.now(), nextOccurrence)

    val isUpcoming: Boolean
        get() = daysUntil >= 0

    private fun calculateNextOccurrence(): LocalDate {
        val today = LocalDate.now()
        return when (dayType) {
            DayType.ONE_TIME -> date
            DayType.YEARLY_REPEAT -> {
                val thisYear = date.withYear(today.year)
                if (thisYear.isBefore(today)) {
                    date.withYear(today.year + 1)
                } else {
                    thisYear
                }
            }
        }
    }
}
