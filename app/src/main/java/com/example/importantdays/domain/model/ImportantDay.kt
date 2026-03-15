package com.example.importantdays.domain.model

import com.example.importantdays.data.model.DayType
import com.example.importantdays.data.model.DateType
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

data class ImportantDay(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: LocalDate,
    val dayType: DayType,
    val dateType: DateType = DateType.SOLAR,
    val lunarMonth: Int? = null,
    val lunarDay: Int? = null,
    val isLunarLeapMonth: Boolean = false,
    val isFavorite: Boolean = false,
    val reminderEnabled: Boolean = false,
    val reminderDaysBefore: Int = 0,
    val notificationChannels: List<NotificationChannel> = listOf(
        NotificationChannel.APP,
        NotificationChannel.SYSTEM
    ),
    val timeEnabled: Boolean = false,
    val time: LocalTime? = null,
    val personId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val nextOccurrence: LocalDate
        get() = calculateNextOccurrence()

    val daysUntil: Long
        get() = ChronoUnit.DAYS.between(LocalDate.now(), nextOccurrence)

    val isUpcoming: Boolean
        get() = daysUntil >= 0

    private fun calculateNextOccurrence(): LocalDate {
        val today = LocalDate.now()
        return when (dayType) {
            DayType.ONE_TIME -> date
            DayType.YEARLY_REPEAT -> {
                val thisYear = date.withYear(today.year)
                if (thisYear.isBefore(today)) {
                    date.withYear(today.year + 1)
                } else {
                    thisYear
                }
            }
            DayType.LUNAR_YEARLY_REPEAT -> {
                // 农历年度重复 - 需要用 lunar4j 计算
                calculateNextLunarDate(today)
            }
        }
    }

    private fun calculateNextLunarDate(today: LocalDate): LocalDate {
        // 使用 lunar4j 计算下一个农历日期
        return try {
            val lunarModule = com.github.szhangb.lunar.LunarModule.getInstance()
            val currentYear = today.year

            // 尝试在当前年份查找对应的农历日期
            for (yearOffset in 0..1) {
                val targetYear = currentYear + yearOffset
                val lunarDate = lunarModule.lunarFromYMD(
                    targetYear,
                    lunarMonth ?: 1,
                    lunarDay ?: 1,
                    isLunarLeapMonth
                )
                
                if (lunarDate != null) {
                    val solarDate = lunarDate.toSolar()
                    if (solarDate.isAfter(today) || solarDate == today) {
                        return solarDate
                    }
                }
            }
            // 默认返回今天（不应该发生）
            today
        } catch (e: Exception) {
            // 如果计算失败，返回公历日期
            date.withYear(today.year).let {
                if (it.isBefore(today)) it.plusYears(1) else it
            }
        }
    }

    /**
     * 获取农历日期的显示文本
     */
    fun getLunarDateText(): String {
        if (dateType != DateType.LUNAR || lunarMonth == null || lunarDay == null) {
            return ""
        }
        
        val monthNames = arrayOf("", "正月", "二月", "三月", "四月", "五月", "六月", 
                                  "七月", "八月", "九月", "十月", "冬月", "腊月")
        val dayNames = arrayOf("", "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
                               "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
                               "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十")
        
        val monthStr = monthNames.getOrElse(lunarMonth) { "${lunarMonth}月" }
        val dayStr = dayNames.getOrElse(lunarDay) { "${lunarDay}日" }
        val leapStr = if (isLunarLeapMonth) "闰" else ""
        
        return "农历$leapStr$monthStr$dayStr"
    }
}
