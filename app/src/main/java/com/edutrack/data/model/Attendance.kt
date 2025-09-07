package com.edutrack.data.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Attendance(
    val id: String,
    val studentId: String,
    val subjectId: String,
    val date: LocalDate,
    val status: AttendanceStatus,
    val remarks: String? = null,
    val markedBy: String, // Teacher/Staff ID
    val markedAt: LocalDateTime = LocalDateTime.now(),
    val isLate: Boolean = false,
    val lateMinutes: Int? = null
)

data class AttendanceSummary(
    val studentId: String,
    val subjectId: String,
    val totalClasses: Int,
    val presentClasses: Int,
    val absentClasses: Int,
    val lateClasses: Int,
    val attendancePercentage: Double,
    val lastUpdated: LocalDateTime
)

data class Subject(
    val id: String,
    val name: String,
    val code: String,
    val credits: Int,
    val semester: Int,
    val academicYear: String,
    val teacherId: String,
    val description: String? = null,
    val isActive: Boolean = true
)

enum class AttendanceStatus {
    PRESENT, ABSENT, LATE, EXCUSED, HOLIDAY
}
