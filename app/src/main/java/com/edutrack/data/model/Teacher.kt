package com.edutrack.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime

data class Teacher @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val employeeId: String,
    val department: String,
    val designation: String,
    val qualification: String,
    val experience: Int, // in years
    val specialization: List<String>,
    val subjects: List<String>,
    val dateOfBirth: LocalDate,
    val joiningDate: LocalDate,
    val address: String? = null,
    val profileImage: String? = null,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class TeacherProfile(
    val teacher: Teacher,
    val totalStudents: Int,
    val currentSemester: String,
    val subjectsTeaching: List<String>,
    val recentActivities: List<TeacherActivity>
)

data class TeacherActivity(
    val id: String,
    val type: ActivityType,
    val title: String,
    val description: String,
    val timestamp: LocalDateTime,
    val relatedId: String? = null
)

enum class ActivityType {
    CLASS_TAKEN, ASSIGNMENT_CREATED, EXAM_CONDUCTED, ATTENDANCE_MARKED, 
    STUDENT_EVALUATED, MEETING_SCHEDULED, NOTICE_PUBLISHED
}
