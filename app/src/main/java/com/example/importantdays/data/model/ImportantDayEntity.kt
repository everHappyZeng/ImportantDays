package com.example.importantdays.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.importantdays.data.local.Converters
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "important_days",
    foreignKeys = [
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["personId"])]
)
@TypeConverters(Converters::class)
data class ImportantDayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: LocalDate,
    val dayType: DayType,
    val dateType: DateType = DateType.SOLAR,    // 公历/农历
    val lunarMonth: Int? = null,                // 农历月份 (1-12)，农历日期时使用
    val lunarDay: Int? = null,                  // 农历日期 (1-30)，农历日期时使用
    val isLunarLeapMonth: Boolean = false,       // 是否闰月
    val isFavorite: Boolean = false,
    val reminderEnabled: Boolean = false,
    val reminderDaysBefore: Int = 0,
    val notificationChannels: List<String> = listOf("APP", "SYSTEM"),
    val timeEnabled: Boolean = false,
    val time: LocalTime? = null,
    val personId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
