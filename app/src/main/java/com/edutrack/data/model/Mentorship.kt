package com.edutrack.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

data class Mentor @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val department: String,
    val designation: String,
    val employeeId: String,
    val maxMentees: Int = 10,
    val currentMentees: Int = 0,
    val isActive: Boolean = true,
    val specialization: List<String>? = null,
    val experience: Int? = null, // in years
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class Mentorship(
    val id: String,
    val mentorId: String,
    val menteeId: String,
    val assignedDate: LocalDateTime,
    val status: MentorshipStatus,
    val notes: String? = null,
    val lastMeetingDate: LocalDateTime? = null,
    val nextMeetingDate: LocalDateTime? = null,
    val isActive: Boolean = true
)

data class MentorshipMeeting(
    val id: String,
    val mentorshipId: String,
    val meetingDate: LocalDateTime,
    val duration: Int, // in minutes
    val agenda: String? = null,
    val discussion: String? = null,
    val actionItems: List<String>? = null,
    val nextSteps: String? = null,
    val menteeFeedback: String? = null,
    val mentorNotes: String? = null
)

enum class MentorshipStatus {
    ACTIVE, INACTIVE, COMPLETED, SUSPENDED
}
