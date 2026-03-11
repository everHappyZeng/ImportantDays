package com.example.importantdays.domain.model

import com.example.importantdays.data.model.ActivityType
import java.time.LocalDate

data class ActivityRecord(
    val id: Long = 0,
    val personId: Long,
    val importantDayId: Long? = null,
    val activityType: ActivityType,
    val title: String,
    val description: String = "",
    val date: LocalDate,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
