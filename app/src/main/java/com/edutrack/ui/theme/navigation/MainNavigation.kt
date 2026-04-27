package com.edutrack.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.edutrack.data.model.UserProfile
import com.edutrack.ui.academic.AcademicModule
import com.edutrack.ui.academic.profile.StudentProfileScreen
import com.edutrack.ui.academic.teacher.TeacherProfileScreen
import com.edutrack.ui.academic.teacher.TeacherDashboard
import com.edutrack.ui.academic.teacher.TeacherClassesScreen
import com.edutrack.ui.academic.teacher.TeacherStudentsScreen
import com.edutrack.ui.academic.teacher.TeacherAttendanceScreen
import com.edutrack.ui.academic.teacher.TeacherAssignmentsScreen
import com.edutrack.ui.academic.teacher.TeacherExamsScreen
import com.edutrack.ui.academic.teacher.TeacherOrPrExamsScreen
import com.edutrack.ui.academic.teacher.TeacherTimetableScreen
import com.edutrack.ui.academic.teacher.TeacherNoticesScreen
import com.edutrack.ui.academic.teacher.TeacherQuestionPaperScreen
import com.edutrack.ui.academic.teacher.TeacherMarksScreen
import com.edutrack.ui.viewmodel.AuthViewModel
import com.edutrack.ui.auth.LoginScreen
import com.edutrack.ui.auth.TeacherRegistrationScreen

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(authViewModel: AuthViewModel = viewModel()) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    var showRegistration by remember { mutableStateOf(false) }

    when {
        !authState.isLoggedIn -> {
            if (showRegistration) {
                TeacherRegistrationScreen(
                    isLoading = authState.isLoading,
                    onBack = { showRegistration = false },
                    onRegister = { email, password, profile ->
                        authViewModel.registerTeacher(email, password, profile,
                            onSuccess = { showRegistration = false },
                            onError = { /* error shown via authState */ }
                        )
                    }
                )
            } else {
                LoginScreen(
                    authState = authState,
                    onLoginClick = { request -> authViewModel.login(request) },
                    onRegisterClick = { showRegistration = true }
                )
            }
        }
        authState.userProfile?.role == "student" -> {
            StudentApp(
                userProfile = authState.userProfile!!,
                onLogout = { authViewModel.logout() }
            )
        }
        authState.userProfile?.role == "teacher" -> {
            TeacherApp(
                userProfile = authState.userProfile!!,
                authViewModel = authViewModel,
                onLogout = { authViewModel.logout() }
            )
        }
        authState.userProfile?.role == "admin" -> {
            AdminApp(onLogout = { authViewModel.logout() })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudentApp(userProfile: UserProfile, onLogout: () -> Unit) {
    var selectedScreen by remember { mutableStateOf<StudentScreen?>(null) }

    when (selectedScreen) {
        null -> AcademicModule(
            navController = rememberNavController(),
            studentProfile = userProfile,
            onLogout = onLogout,
            onProfileClick = { selectedScreen = StudentScreen.PROFILE }
        )
        StudentScreen.PROFILE -> StudentProfileScreen(
            userProfile = userProfile,
            onBack = { selectedScreen = null }
        )
    }
}

@Composable
fun TeacherApp(userProfile: UserProfile, authViewModel: AuthViewModel, onLogout: () -> Unit) {
    var selectedScreen by remember { mutableStateOf<TeacherScreen?>(null) }

    when (selectedScreen) {
        null -> TeacherDashboard(
            teacherProfile = userProfile,
            onLogout = onLogout,
            onProfileClick = { selectedScreen = TeacherScreen.PROFILE },
            onFeatureClick = { feature ->
                selectedScreen = when (feature.name) {
                    "CLASSES"       -> TeacherScreen.CLASSES
                    "STUDENTS"      -> TeacherScreen.STUDENTS
                    "ATTENDANCE"    -> TeacherScreen.ATTENDANCE
                    "ASSIGNMENTS"   -> TeacherScreen.ASSIGNMENTS
                    "MARKS"         -> TeacherScreen.MARKS
                    "EXAMS"         -> TeacherScreen.EXAMS
                    "OR_PR_EXAM"    -> TeacherScreen.OR_PR_EXAMS
                    "TIMETABLE"     -> TeacherScreen.TIMETABLE
                    "NOTICES"       -> TeacherScreen.NOTICES
                    "QUESTION_PAPER"-> TeacherScreen.QUESTION_PAPER
                    else            -> null
                }
            }
        )
        TeacherScreen.CLASSES       -> TeacherClassesScreen(onBack = { selectedScreen = null })
        TeacherScreen.STUDENTS      -> TeacherStudentsScreen(
            teacherProfile = userProfile, onBack = { selectedScreen = null })
        TeacherScreen.ATTENDANCE    -> TeacherAttendanceScreen(
            teacherProfile = userProfile, onBack = { selectedScreen = null })
        TeacherScreen.ASSIGNMENTS   -> TeacherAssignmentsScreen(
            teacherProfile = userProfile, onBack = { selectedScreen = null })
        TeacherScreen.MARKS         -> TeacherMarksScreen(
            teacherProfile = userProfile, onBack = { selectedScreen = null })
        TeacherScreen.EXAMS         -> TeacherExamsScreen(
            teacherProfile = userProfile, onBack = { selectedScreen = null })
        TeacherScreen.OR_PR_EXAMS   -> TeacherOrPrExamsScreen(
            teacherProfile = userProfile, onBack = { selectedScreen = null })
        TeacherScreen.TIMETABLE     -> TeacherTimetableScreen(
            teacherProfile = userProfile, onBack = { selectedScreen = null })
        TeacherScreen.NOTICES       -> TeacherNoticesScreen(
            teacherProfile = userProfile, onBack = { selectedScreen = null })
        TeacherScreen.QUESTION_PAPER-> TeacherQuestionPaperScreen(onBack = { selectedScreen = null })
        TeacherScreen.PROFILE       -> TeacherProfileScreen(
            userProfile = userProfile,
            onUpdateProfile = { updatedProfile ->
                authViewModel.updateProfile(updatedProfile, onSuccess = {}, onError = {})
            },
            onBack = { selectedScreen = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApp(onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
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
            Text("Admin Dashboard", style = MaterialTheme.typography.headlineMedium)
            Text("Admin features coming soon...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

enum class StudentScreen { PROFILE }

enum class TeacherScreen {
    CLASSES, STUDENTS, ATTENDANCE, ASSIGNMENTS, MARKS,
    EXAMS, OR_PR_EXAMS, TIMETABLE, NOTICES, QUESTION_PAPER, PROFILE
}
