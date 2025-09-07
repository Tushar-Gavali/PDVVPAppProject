package com.edutrack.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime

data class Student @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String,
    val name: String,
    val rollNumber: String,
    val prn: String, // Permanent Registration Number
    val email: String,
    val phone: String,
    val dateOfBirth: LocalDate,
    val admissionDate: LocalDate,
    val course: String,
    val semester: Int,
    val academicYear: String,
    val mentorId: String? = null,
    val profileImage: String? = null,
    val address: String? = null,
    val parentName: String? = null,
    val parentPhone: String? = null,
    val emergencyContact: String? = null,
    val bloodGroup: String? = null,
    val medicalInfo: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class AcademicHistory @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String,
    val studentId: String,
    val semester: Int,
    val academicYear: String,
    val cgpa: Double,
    val sgpa: Double,
    val totalCredits: Int,
    val earnedCredits: Int,
    val subjects: List<String>,
    val grades: Map<String, String>,
    val attendance: Double,
    val status: AcademicStatus,
    val remarks: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class AcademicStatus {
    ACTIVE, INACTIVE, GRADUATED, SUSPENDED, DROPPED
}
