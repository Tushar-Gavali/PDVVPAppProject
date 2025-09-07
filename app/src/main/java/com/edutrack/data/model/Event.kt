package com.edutrack.data.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val eventType: EventType,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val startTime: String? = null, // HH:MM format
    val endTime: String? = null, // HH:MM format
    val venue: String? = null,
    val organizer: String,
    val maxParticipants: Int? = null,
    val registrationDeadline: LocalDate? = null,
    val isActive: Boolean = true,
    val isRegistrationRequired: Boolean = false,
    val createdBy: String, // Staff/Admin ID
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class EventAttendance(
    val id: String,
    val eventId: String,
    val studentId: String,
    val attendanceStatus: EventAttendanceStatus,
    val registeredAt: LocalDateTime? = null,
    val attendedAt: LocalDateTime? = null,
    val remarks: String? = null
)

data class EventNotice(
    val id: String,
    val eventId: String,
    val title: String,
    val message: String,
    val priority: NoticePriority,
    val targetAudience: List<String>? = null, // Student IDs or groups
    val isActive: Boolean = true,
    val publishedAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime? = null,
    val createdBy: String
)

enum class EventType {
    ACADEMIC, EXTRACURRICULAR, SPORTS, CULTURAL, WORKSHOP, SEMINAR, CONFERENCE, COMPETITION
}

enum class EventAttendanceStatus {
    REGISTERED, ATTENDED, ABSENT, CANCELLED
}

enum class NoticePriority {
    LOW, MEDIUM, HIGH, URGENT
}
