package com.example.importantdays.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.importantdays.data.local.Converters
import java.time.LocalDate

@Entity(
    tableName = "activity_records",
    foreignKeys = [
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ImportantDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["importantDayId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["personId"]), Index(value = ["importantDayId"])]
)
@TypeConverters(Converters::class)
data class ActivityRecordEntity(
    @PrimaryKey(autoGenerate = true)
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
