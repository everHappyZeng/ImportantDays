package com.example.importantdays.util

import com.example.importantdays.data.model.DateType
import com.example.importantdays.data.model.DayType
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

    /**
     * 获取公历日期对应的农历信息
     * 返回 Pair(月份, 日期)
     */
    fun getLunarInfo(solarDate: LocalDate): Pair<Int, Int>? {
        return try {
            val lunarModule = com.github.szhangb.lunar.LunarModule.getInstance()
            val lunar = lunarModule.lunarFromSolar(solarDate.year, solarDate.monthValue, solarDate.dayOfMonth)
            if (lunar != null) {
                Pair(lunar.month, lunar.day)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 将农历日期转换为公历日期
     */
    fun solarFromLunar(year: Int, month: Int, day: Int, isLeapMonth: Boolean = false): LocalDate? {
        return try {
            val lunarModule = com.github.szhangb.lunar.LunarModule.getInstance()
            val lunar = lunarModule.lunarFromYMD(year, month, day, isLeapMonth)
            if (lunar != null) {
                val solar = lunar.toSolar()
                LocalDate.of(solar.year, solar.month, solar.day)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 格式化农历日期显示
     */
    fun formatLunarDate(month: Int, day: Int, isLeapMonth: Boolean = false): String {
        val monthNames = arrayOf("", "正月", "二月", "三月", "四月", "五月", "六月", 
                                  "七月", "八月", "九月", "十月", "冬月", "腊月")
        val dayNames = arrayOf("", "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
                               "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
                               "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十")
        
        val monthStr = monthNames.getOrElse(month) { "${month}月" }
        val dayStr = dayNames.getOrElse(day) { "${day}日" }
        val leapStr = if (isLeapMonth) "闰" else ""
        
        return "$leapStr$monthStr$dayStr"
    }

    /**
     * 判断是否为农历重复类型
     */
    fun isLunarRepeat(dayType: DayType): Boolean {
        return dayType == DayType.LUNAR_YEARLY_REPEAT
    }
}
