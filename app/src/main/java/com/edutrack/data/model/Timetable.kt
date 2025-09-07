package com.edutrack.data.model

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

data class Timetable(
    val id: String,
    val subjectId: String,
    val teacherId: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val venue: String? = null,
    val semester: Int,
    val academicYear: String,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class TimetableSlot(
    val id: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val subject: Subject? = null,
    val teacher: Teacher? = null,
    val venue: String? = null,
    val isBreak: Boolean = false,
    val breakType: BreakType? = null
)


enum class BreakType {
    LUNCH, SHORT_BREAK, LONG_BREAK, PRAYER_TIME
}
