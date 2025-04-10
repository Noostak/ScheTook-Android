package com.sopt.core.util.time

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class CalculateTimeFromLocalDate {
    // ex) 1월 1일 (월)
    fun formatLocalDateWithDay(localDate: LocalDate): String {
        val month = localDate.monthValue
        val day = localDate.dayOfMonth
        val dayOfWeek = localDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)

        return "${month}월 ${day}일 ($dayOfWeek)"
    }
}
