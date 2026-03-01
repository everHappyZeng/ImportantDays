package com.example.importantdays.util

import java.time.LocalDate

fun LocalDate.isToday(): Boolean {
    return this == LocalDate.now()
}

fun LocalDate.isTomorrow(): Boolean {
    return this == LocalDate.now().plusDays(1)
}

fun LocalDate.isPast(): Boolean {
    return this.isBefore(LocalDate.now())
}

fun LocalDate.isFuture(): Boolean {
    return this.isAfter(LocalDate.now())
}
