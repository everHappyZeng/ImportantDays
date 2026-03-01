package com.example.importantdays.data.model

import androidx.room.Embedded
import java.time.LocalDate

data class PersonWithNextDaySummary(
    @Embedded(prefix = "person_")
    val person: PersonEntity,
    val nextDayId: Long?,
    val nextDayTitle: String?,
    val nextDayDate: LocalDate?
)
