package com.edutrack.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

data class User @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String,
    val email: String,
    val password: String,
    val role: UserRole,
    val profileId: String, // Student ID or Teacher ID
    val isActive: Boolean = true,
    val lastLogin: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class LoginRequest(
    val email: String? = null,
    val password: String? = null,
    val prn: String? = null,
    val dateOfBirth: String? = null, // Format: "dd/MM/yyyy"
    val role: UserRole
)

data class LoginResponse(
    val success: Boolean,
    val user: User? = null,
    val message: String = ""
)

enum class UserRole {
    STUDENT, TEACHER, ADMIN
}

data class AuthState(
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
