package com.example.importantdays.util

import com.example.importantdays.data.model.ImportantDayEntity
import com.example.importantdays.data.model.PersonEntity
import com.example.importantdays.data.model.PersonWithNextDaySummary
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.model.NotificationChannel
import com.example.importantdays.domain.model.Person
import com.example.importantdays.domain.model.PersonWithNextDay

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
        personId = personId,
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
        personId = personId,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )
}

fun PersonEntity.toDomain(): Person {
    return Person(
        id = id,
        name = name,
        avatar = avatar,
        notes = notes,
        createdAt = createdAt
    )
}

fun Person.toEntity(): PersonEntity {
    return PersonEntity(
        id = id,
        name = name,
        avatar = avatar,
        notes = notes,
        createdAt = createdAt
    )
}

fun PersonWithNextDaySummary.toDomain(): PersonWithNextDay {
    return PersonWithNextDay(
        person = person.toDomain(),
        nextDayId = nextDayId,
        nextDayTitle = nextDayTitle,
        nextDayDate = nextDayDate
    )
}
