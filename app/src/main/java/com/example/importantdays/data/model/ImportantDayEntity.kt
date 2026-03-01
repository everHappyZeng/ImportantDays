package com.example.importantdays.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.importantdays.data.local.Converters
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "important_days")
@TypeConverters(Converters::class)
data class ImportantDayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: LocalDate,
    val dayType: DayType,
    val isFavorite: Boolean = false,
    val reminderEnabled: Boolean = false,
    val reminderDaysBefore: Int = 0,
    val notificationChannels: List<String> = listOf("APP", "SYSTEM"),
    val timeEnabled: Boolean = false,
    val time: LocalTime? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
