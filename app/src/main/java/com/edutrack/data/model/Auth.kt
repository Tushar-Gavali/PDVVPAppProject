package com.edutrack.data.model

/**
 * Firestore-compatible user profile stored in the "users" collection.
 * Uses only primitive types safe for Firestore serialization.
 * No-arg constructor required by Firestore's toObject().
 */
data class UserProfile(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    /** Computed full name — firstName + lastName (or stored directly for teachers) */
    val name: String = "",
    val email: String = "",
    val role: String = "student",       // "student" | "teacher"
    val userClass: String = "",         // FE, SE, TE, BE
    val division: String = "",          // A, B, C, D, E, F
    val branch: String = "",            // IT, CS, Mech, Civil, ETC, CSD …
    val address: String = "",
    val prn: String = "",               // Students only — used for login
    val dateOfBirth: String = "",       // Students only — "dd/MM/yyyy"
    
    // Teacher specific fields
    val phone: String = "",
    val qualification: String = "",
    val experience: Int = 0,
    val subjects: String = "",          // Comma separated subjects
    val department: String = "",
    val designation: String = "",
    val studentCount: Int = 0,
    
    val createdAt: Long = System.currentTimeMillis()
)

/** Lightweight auth state held in memory during the session. */
data class AuthState(
    val isLoggedIn: Boolean = false,
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/** Roles used across the app. */
enum class UserRole(val firestoreValue: String) {
    STUDENT("student"),
    TEACHER("teacher");

    companion object {
        fun fromString(value: String): UserRole =
            entries.firstOrNull { it.firestoreValue == value } ?: STUDENT
    }
}

/** Login request model — unified for student and teacher. */
data class LoginRequest(
    val email: String? = null,
    val password: String? = null,
    val prn: String? = null,
    val dateOfBirth: String? = null,   // "dd/MM/yyyy"
    val role: UserRole = UserRole.STUDENT
)

/** Sealed result type for auth operations. */
sealed class AuthResult {
    data class Success(val profile: UserProfile) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}
