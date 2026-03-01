package com.example.importantdays.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {
    private val displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val readableFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    private val timeFormatterWithSeconds = DateTimeFormatter.ofPattern("HH:mm:ss")
    private val timeFormatterWithoutSeconds = DateTimeFormatter.ofPattern("HH:mm")

    fun formatDate(date: LocalDate): String {
        return date.format(displayFormatter)
    }

    fun formatDateReadable(date: LocalDate): String {
        return date.format(readableFormatter)
    }

    fun parseDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString, displayFormatter)
    }

    fun daysBetween(start: LocalDate, end: LocalDate): Long {
        return ChronoUnit.DAYS.between(start, end)
    }

    fun formatDaysUntil(days: Long): String {
        return when {
            days < 0 -> "${-days} days ago"
            days == 0L -> "Today"
            days == 1L -> "Tomorrow"
            days < 7 -> "In $days days"
            days < 30 -> "In ${days / 7} weeks"
            days < 365 -> "In ${days / 30} months"
            else -> "In ${days / 365} years"
        }
    }

    fun formatTime(time: LocalTime, includeSeconds: Boolean = true): String {
        return if (includeSeconds) {
            time.format(timeFormatterWithSeconds)
        } else {
            time.format(timeFormatterWithoutSeconds)
        }
    }

    fun formatDateTime(date: LocalDate, time: LocalTime?): String {
        return if (time != null) {
            "${formatDateReadable(date)} ${formatTime(time, includeSeconds = true)}"
        } else {
            formatDateReadable(date)
        }
    }
}
