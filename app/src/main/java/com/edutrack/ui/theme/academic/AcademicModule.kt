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
import com.edutrack.ui.academic.attendance.AttendanceScreen
import com.edutrack.ui.academic.aptitude.AptitudeScreen
import com.edutrack.ui.academic.assignments.AssignmentsScreen
import com.edutrack.ui.academic.competitive.CompetitiveScreen
import com.edutrack.ui.academic.events.EventNoticeScreen
import com.edutrack.ui.academic.events.EventAttendanceScreen
import com.edutrack.ui.academic.exams.ExamScreen
import com.edutrack.ui.academic.mentorship.MentorListScreen
import com.edutrack.ui.academic.profile.StudentProfileScreen
import com.edutrack.ui.academic.tests.TestsScreen
import com.edutrack.ui.academic.timetable.TimetableScreen

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicModule(
    navController: NavController,
    onLogout: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null
) {
    var selectedFeature by remember { mutableStateOf<AcademicFeature?>(null) }
    
    when (selectedFeature) {
        null -> AcademicDashboard(
            onFeatureSelected = { selectedFeature = it },
            onLogout = onLogout,
            onProfileClick = onProfileClick
        )
        AcademicFeature.ATTENDANCE -> AttendanceScreen(
            onBack = { selectedFeature = null }
        )
        AcademicFeature.APTITUDE -> AptitudeScreen(
            onBack = { selectedFeature = null }
        )
        AcademicFeature.COMPETITIVE -> CompetitiveScreen(
            onBack = { selectedFeature = null }
        )
        AcademicFeature.TESTS -> TestsScreen(
            onBack = { selectedFeature = null }
        )
        AcademicFeature.EVENT_NOTICE -> EventNoticeScreen(
            onBack = { selectedFeature = null }
        )
        AcademicFeature.EVENT_ATTENDANCE -> EventAttendanceScreen(
            onBack = { selectedFeature = null }
        )
        AcademicFeature.STUDENT_PROFILE -> StudentProfileScreen(
            onBack = { selectedFeature = null }
        )
        AcademicFeature.TIMETABLE -> TimetableScreen(
            onBack = { selectedFeature = null }
        )
        AcademicFeature.EXAMS -> ExamScreen(
            onBack = { selectedFeature = null }
        )
        AcademicFeature.ASSIGNMENTS -> AssignmentsScreen(
            onBack = { selectedFeature = null }
        )
        AcademicFeature.MENTORSHIP -> MentorListScreen(
            onBack = { selectedFeature = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicDashboard(
    onFeatureSelected: (AcademicFeature) -> Unit,
    onLogout: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Academic Module") },
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
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
    ATTENDANCE(
        title = "Attendance",
        description = "Track attendance by subject or day",
        icon = Icons.Default.Person
    ),
    APTITUDE(
        title = "Aptitude",
        description = "Aptitude tests and training",
        icon = Icons.Default.Psychology
    ),
    COMPETITIVE(
        title = "Competitive",
        description = "Competitive exam preparation",
        icon = Icons.Default.EmojiEvents
    ),
    TESTS(
        title = "Tests",
        description = "Question papers and internal tests",
        icon = Icons.Default.Quiz
    ),
    EVENT_NOTICE(
        title = "Event Notice",
        description = "Academic and extracurricular events",
        icon = Icons.Default.Campaign
    ),
    EVENT_ATTENDANCE(
        title = "Event Attendance",
        description = "Track event participation",
        icon = Icons.Default.Event
    ),
    STUDENT_PROFILE(
        title = "Student Profile",
        description = "Individual student information",
        icon = Icons.Default.AccountCircle
    ),
    TIMETABLE(
        title = "Time-table",
        description = "Class timetable management",
        icon = Icons.Default.Schedule
    ),
    EXAMS(
        title = "OR/PR/Exam",
        description = "Oral, Practical, Written exams",
        icon = Icons.Default.Assignment
    ),
    ASSIGNMENTS(
        title = "Assignments",
        description = "Assignment distribution and evaluation",
        icon = Icons.Default.Assignment
    ),
    MENTORSHIP(
        title = "Mentor List",
        description = "Mentor-Mentee mapping",
        icon = Icons.Default.SupervisorAccount
    )
}
