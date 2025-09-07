package com.edutrack.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.edutrack.data.model.AuthState
import com.edutrack.data.model.LoginRequest
import com.edutrack.data.model.LoginResponse
import com.edutrack.data.model.User
import com.edutrack.data.model.UserRole
import java.time.LocalDateTime

class AuthViewModel {
    var authState by mutableStateOf(AuthState())
        private set

    // Mock users for demo
    private val mockUsers = listOf(
        User(
            id = "U001",
            email = "student@demo.com",
            password = "password",
            role = UserRole.STUDENT,
            profileId = "S001",
            lastLogin = LocalDateTime.now()
        ),
        User(
            id = "U002",
            email = "teacher@demo.com",
            password = "password",
            role = UserRole.TEACHER,
            profileId = "T001",
            lastLogin = LocalDateTime.now()
        ),
        User(
            id = "U003",
            email = "admin@demo.com",
            password = "password",
            role = UserRole.ADMIN,
            profileId = "A001",
            lastLogin = LocalDateTime.now()
        )
    )

    fun login(request: LoginRequest) {
        authState = authState.copy(isLoading = true, error = null)

        // Simulate network delay
        val user = when (request.role) {
            UserRole.STUDENT -> {
                // For students, authenticate using PRN and DOB
                if (request.prn != null && request.dateOfBirth != null) {
                    mockUsers.find { 
                        it.role == UserRole.STUDENT && 
                        it.profileId == request.prn // Using profileId as PRN for demo
                    }
                } else {
                    null
                }
            }
            else -> {
                // For teachers and admins, use email and password
                mockUsers.find { 
                    it.email == request.email && 
                    it.password == request.password && 
                    it.role == request.role 
                }
            }
        }

        authState = if (user != null) {
            AuthState(
                isLoggedIn = true,
                user = user,
                isLoading = false
            )
        } else {
            AuthState(
                isLoggedIn = false,
                user = null,
                isLoading = false,
                error = when (request.role) {
                    UserRole.STUDENT -> "Invalid PRN or Date of Birth"
                    else -> "Invalid email or password"
                }
            )
        }
    }

    fun logout() {
        authState = AuthState()
    }

    fun clearError() {
        authState = authState.copy(error = null)
    }
}
