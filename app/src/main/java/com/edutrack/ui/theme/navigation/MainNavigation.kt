    package com.edutrack.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.edutrack.data.model.UserRole
import com.edutrack.ui.academic.AcademicModule
import com.edutrack.ui.academic.profile.StudentProfileScreen
import com.edutrack.ui.academic.teacher.TeacherProfileScreen
import com.edutrack.ui.academic.teacher.TeacherDashboard
import com.edutrack.ui.academic.teacher.TeacherClassesScreen
import com.edutrack.ui.academic.teacher.TeacherStudentsScreen
import com.edutrack.ui.academic.teacher.TeacherAttendanceScreen
import com.edutrack.ui.academic.teacher.TeacherAssignmentsScreen
import com.edutrack.ui.academic.teacher.TeacherExamsScreen
import com.edutrack.ui.academic.teacher.TeacherTimetableScreen
import com.edutrack.ui.academic.teacher.TeacherNoticesScreen
import com.edutrack.ui.academic.teacher.TeacherQuestionPaperScreen
import com.edutrack.ui.auth.AuthViewModel
import com.edutrack.ui.auth.LoginScreen
import com.edutrack.ui.auth.TeacherRegistrationScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val authViewModel = remember { AuthViewModel() }
    val authState = authViewModel.authState
    var showRegistration by remember { mutableStateOf(false) }

    when {
        !authState.isLoggedIn -> {
            if (showRegistration) {
                TeacherRegistrationScreen(
                    onBack = { showRegistration = false },
                    onRegistrationSuccess = { 
                        showRegistration = false
                        // TODO: Show success message or auto-login
                    }
                )
            } else {
                LoginScreen(
                    onLoginSuccess = { role ->
                        authViewModel.login(
                            com.edutrack.data.model.LoginRequest(
                                email = when (role) {
                                    UserRole.STUDENT -> null
                                    UserRole.TEACHER -> "teacher@demo.com"
                                    UserRole.ADMIN -> "admin@demo.com"
                                },
                                password = when (role) {
                                    UserRole.STUDENT -> null
                                    else -> "password"
                                },
                                prn = when (role) {
                                    UserRole.STUDENT -> "S001"
                                    else -> null
                                },
                                dateOfBirth = when (role) {
                                    UserRole.STUDENT -> "15/03/2000"
                                    else -> null
                                },
                                role = role
                            )
                        )
                    },
                    onRegisterClick = { showRegistration = true }
                )
            }
        }
        authState.user?.role == UserRole.STUDENT -> {
            StudentApp(
                onLogout = { authViewModel.logout() }
            )
        }
        authState.user?.role == UserRole.TEACHER -> {
            TeacherApp(
                onLogout = { authViewModel.logout() }
            )
        }
        authState.user?.role == UserRole.ADMIN -> {
            AdminApp(
                onLogout = { authViewModel.logout() }
            )
        }
    }
}

@Composable
fun StudentApp(onLogout: () -> Unit) {
    var selectedScreen by remember { mutableStateOf<StudentScreen?>(null) }
    
    when (selectedScreen) {
        null -> AcademicModule(
            navController = rememberNavController(),
            onLogout = onLogout,
            onProfileClick = { selectedScreen = StudentScreen.PROFILE }
        )
        StudentScreen.PROFILE -> StudentProfileScreen(
            onBack = { selectedScreen = null }
        )
    }
}

@Composable
fun TeacherApp(onLogout: () -> Unit) {
    var selectedScreen by remember { mutableStateOf<TeacherScreen?>(null) }
    
    when (selectedScreen) {
        null -> TeacherDashboard(
            onLogout = onLogout,
            onProfileClick = { selectedScreen = TeacherScreen.PROFILE },
            onFeatureClick = { feature -> 
                selectedScreen = when (feature) {
                    com.edutrack.ui.academic.teacher.TeacherFeature.CLASSES -> TeacherScreen.CLASSES
                    com.edutrack.ui.academic.teacher.TeacherFeature.STUDENTS -> TeacherScreen.STUDENTS
                    com.edutrack.ui.academic.teacher.TeacherFeature.ATTENDANCE -> TeacherScreen.ATTENDANCE
                    com.edutrack.ui.academic.teacher.TeacherFeature.ASSIGNMENTS -> TeacherScreen.ASSIGNMENTS
                    com.edutrack.ui.academic.teacher.TeacherFeature.EXAMS -> TeacherScreen.EXAMS
                    com.edutrack.ui.academic.teacher.TeacherFeature.TIMETABLE -> TeacherScreen.TIMETABLE
                    com.edutrack.ui.academic.teacher.TeacherFeature.NOTICES -> TeacherScreen.NOTICES
                    com.edutrack.ui.academic.teacher.TeacherFeature.QUESTION_PAPER -> TeacherScreen.QUESTION_PAPER
                }
            }
        )
        TeacherScreen.CLASSES -> TeacherClassesScreen(
            onBack = { selectedScreen = null }
        )
        TeacherScreen.STUDENTS -> TeacherStudentsScreen(
            onBack = { selectedScreen = null }
        )
        TeacherScreen.ATTENDANCE -> TeacherAttendanceScreen(
            onBack = { selectedScreen = null }
        )
        TeacherScreen.ASSIGNMENTS -> TeacherAssignmentsScreen(
            onBack = { selectedScreen = null }
        )
        TeacherScreen.EXAMS -> TeacherExamsScreen(
            onBack = { selectedScreen = null }
        )
        TeacherScreen.TIMETABLE -> TeacherTimetableScreen(
            onBack = { selectedScreen = null }
        )
        TeacherScreen.NOTICES -> TeacherNoticesScreen(
            onBack = { selectedScreen = null }
        )
        TeacherScreen.QUESTION_PAPER -> TeacherQuestionPaperScreen(
            onBack = { selectedScreen = null }
        )
        TeacherScreen.PROFILE -> TeacherProfileScreen(
            onBack = { selectedScreen = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApp(onLogout: () -> Unit) {
    // TODO: Implement admin dashboard
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Admin Dashboard",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Admin features coming soon...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

enum class StudentScreen {
    PROFILE
}

enum class TeacherScreen {
    CLASSES,
    STUDENTS,
    ATTENDANCE,
    ASSIGNMENTS,
    EXAMS,
    TIMETABLE,
    NOTICES,
    QUESTION_PAPER,
    PROFILE
}
