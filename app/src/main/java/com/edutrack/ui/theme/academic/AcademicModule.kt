package com.edutrack.ui.academic

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.edutrack.data.model.UserProfile
import com.edutrack.ui.academic.assignments.AssignmentsScreen
import com.edutrack.ui.academic.attendance.AttendanceScreen
import com.edutrack.ui.academic.aptitude.AptitudeScreen
import com.edutrack.ui.academic.competitive.CompetitiveScreen
import com.edutrack.ui.academic.events.EventNoticeScreen
import com.edutrack.ui.academic.events.EventAttendanceScreen
import com.edutrack.ui.academic.exams.ExamScreen
import com.edutrack.ui.academic.marks.MarksScreen
import com.edutrack.ui.academic.mentorship.MentorListScreen
import com.edutrack.ui.academic.notifications.NotificationsScreen
import com.edutrack.ui.academic.profile.StudentProfileScreen
import com.edutrack.ui.academic.tests.TestsScreen
import com.edutrack.ui.academic.timetable.TimetableScreen

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicModule(
    navController: NavController,
    studentProfile: UserProfile,
    onLogout: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null
) {
    var selectedFeature by remember { mutableStateOf<AcademicFeature?>(null) }

    when (selectedFeature) {
        null -> AcademicDashboard(
            studentProfile = studentProfile,
            onFeatureSelected = { selectedFeature = it },
            onLogout = onLogout,
            onProfileClick = onProfileClick
        )
        // Feature screens
        AcademicFeature.ATTENDANCE         -> AttendanceScreen(
            studentProfile = studentProfile, onBack = { selectedFeature = null })
        AcademicFeature.TESTS              -> TestsScreen(
            studentProfile = studentProfile, onBack = { selectedFeature = null })
        AcademicFeature.EVENT_NOTICE       -> EventNoticeScreen(onBack = { selectedFeature = null })
        AcademicFeature.EVENT_ATTENDANCE   -> EventAttendanceScreen(onBack = { selectedFeature = null })
        AcademicFeature.EXAMS              -> ExamScreen(
            studentProfile = studentProfile, onBack = { selectedFeature = null })
        AcademicFeature.MENTORSHIP         -> MentorListScreen(onBack = { selectedFeature = null })
        // ── Firebase-backed screens ──────────────────────────────────────────
        AcademicFeature.ASSIGNMENTS -> AssignmentsScreen(
            studentProfile = studentProfile,
            onBack = { selectedFeature = null }
        )
        AcademicFeature.NOTIFICATIONS -> NotificationsScreen(
            studentProfile = studentProfile,
            onBack = { selectedFeature = null }
        )
        AcademicFeature.MARKS -> MarksScreen(
            studentProfile = studentProfile,
            onBack = { selectedFeature = null }
        )
        AcademicFeature.TIMETABLE -> TimetableScreen(
            studentProfile = studentProfile,
            onBack = { selectedFeature = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicDashboard(
    studentProfile: UserProfile,
    onFeatureSelected: (AcademicFeature) -> Unit,
    onLogout: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Student Dashboard")
                        Text(
                            "${studentProfile.userClass} — Div ${studentProfile.division}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    onProfileClick?.let {
                        IconButton(onClick = it) {
                            Icon(Icons.Default.Person, contentDescription = "Profile")
                        }
                    }
                    onLogout?.let {
                        IconButton(onClick = it) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = paddingValues,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            items(AcademicFeature.values()) { feature ->
                AcademicFeatureCard(
                    feature = feature,
                    onClick = { onFeatureSelected(feature) }
                )
            }
        }
    }
}

@Composable
fun AcademicFeatureCard(
    feature: AcademicFeature,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

enum class AcademicFeature(
    val title: String,
    val description: String,
    val icon: ImageVector
) {
    ATTENDANCE(      "Attendance",       "Track attendance",            Icons.Default.Person),
    ASSIGNMENTS(     "Assignments",      "View & submit assignments",   Icons.Default.Assignment),
    NOTIFICATIONS(   "Notifications",    "Class notices",               Icons.Default.Notifications),
    MARKS(           "My Marks",         "View your marks & grades",    Icons.Default.Grade),
    TIMETABLE(       "Timetable",        "Class timetable",             Icons.Default.Schedule),
    TESTS(           "Tests",            "Question papers & tests",     Icons.Default.Quiz),
    EVENT_NOTICE(    "Event Notice",     "Academic events",             Icons.Default.Campaign),
    EVENT_ATTENDANCE("Event Attendance", "Track event participation",   Icons.Default.Event),
    EXAMS(           "OR/PR/Exam",       "Oral, Practical exams",       Icons.Default.School),
    MENTORSHIP(      "Mentor List",      "Mentor-Mentee mapping",       Icons.Default.SupervisorAccount)
}
