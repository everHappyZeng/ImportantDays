package com.example.importantdays.data.local

import androidx.room.TypeConverter
import com.example.importantdays.data.model.DayType
import com.example.importantdays.data.model.DateType
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromDayType(dayType: DayType): String {
        return dayType.name
    }

    @TypeConverter
    fun toDayType(dayTypeString: String): DayType {
        return DayType.valueOf(dayTypeString)
    }

    @TypeConverter
    fun fromDateType(dateType: DateType): String {
        return dateType.name
    }

    @TypeConverter
    fun toDateType(dateTypeString: String): DateType {
        return DateType.valueOf(dateTypeString)
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toStringList(string: String): List<String> {
        return if (string.isEmpty()) emptyList() else string.split(",")
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it) }
    }
}
