package com.example.importantdays.domain.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class PersonWithNextDay(
    val person: Person,
    val nextDayId: Long?,
    val nextDayTitle: String?,
    val nextDayDate: LocalDate?
) {
    val daysUntil: Long?
        get() = nextDayDate?.let { ChronoUnit.DAYS.between(LocalDate.now(), it) }
}
