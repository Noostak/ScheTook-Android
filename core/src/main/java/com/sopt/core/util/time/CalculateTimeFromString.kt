package com.sopt.core.util.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class CalculateTimeFromString {
    // ex) 09/07
    fun extractFullDateWithSlash(dateTime: String): String {
        return parseDateTime(dateTime).toLocalDate().format(DateTimeFormatter.ofPattern("MM/dd"))
    }

    // ex) 9/7
    fun extractDateWithSlash(dateTime: String): String {
        return parseDateTime(dateTime).toLocalDate().format(DateTimeFormatter.ofPattern("M/d"))
    }

    // ex) 09월 07일
    fun extractFullDateWithKorean(dateTime: String): String {
        return parseDateTime(dateTime).toLocalDate().format(DateTimeFormatter.ofPattern("MM월 dd일"))
    }

    // ex) 9월 7일
    fun extractDateWithKorean(dateTime: String): String {
        return parseDateTime(dateTime).toLocalDate().format(DateTimeFormatter.ofPattern("M월 d일"))
    }

    // ex) 목
    fun extractDayOfWeek(dateTime: String): String {
        return parseDateTime(dateTime).dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
    }

    // ex) (목)
    fun extractDayOfWeekWithBraces(dateTime: String): String {
        return "(${parseDateTime(dateTime).dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)})"
    }

    // ex) 11시
    fun extractHourWithKorean(dateTime: String): String {
        return "${parseDateTime(dateTime).hour}시"
    }

    // ex) 11:00
    fun extractHourWithZero(dateTime: String): String {
        return "${parseDateTime(dateTime).hour}:00"
    }

    private fun parseDateTime(dateTime: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        return LocalDateTime.parse(dateTime, formatter)
    }
}
