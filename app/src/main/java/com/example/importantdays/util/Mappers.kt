package com.example.importantdays.util

import com.example.importantdays.data.model.ImportantDayEntity
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.model.NotificationChannel

fun ImportantDayEntity.toDomain(): ImportantDay {
    return ImportantDay(
        id = id,
        title = title,
        description = description,
        date = date,
        dayType = dayType,
        isFavorite = isFavorite,
        reminderEnabled = reminderEnabled,
        reminderDaysBefore = reminderDaysBefore,
        notificationChannels = notificationChannels.map {
            NotificationChannel.valueOf(it)
        },
        timeEnabled = timeEnabled,
        time = time,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ImportantDay.toEntity(): ImportantDayEntity {
    return ImportantDayEntity(
        id = id,
        title = title,
        description = description,
        date = date,
        dayType = dayType,
        isFavorite = isFavorite,
        reminderEnabled = reminderEnabled,
        reminderDaysBefore = reminderDaysBefore,
        notificationChannels = notificationChannels.map { it.name },
        timeEnabled = timeEnabled,
        time = time,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )
}
