package com.edutrack.ui.academic.teacher

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboard(
    onLogout: () -> Unit,
    onProfileClick: () -> Unit,
    onFeatureClick: (TeacherFeature) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teacher Dashboard") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(TeacherFeature.values()) { feature ->
                TeacherFeatureCard(
                    feature = feature,
                    onClick = { onFeatureClick(feature) }
                )
            }
        }
    }
}

@Composable
fun TeacherFeatureCard(
    feature: TeacherFeature,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium
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
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 3,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

enum class TeacherFeature(
    val title: String,
    val description: String,
    val icon: ImageVector
) {
    CLASSES(
        title = "My Classes",
        description = "View and manage all your scheduled classes in one place.",
        icon = Icons.Default.School
    ),
    STUDENTS(
        title = "Students",
        description = "Check student details, performance, and contact information.",
        icon = Icons.Default.People
    ),
    ATTENDANCE(
        title = "Attendance",
        description = "Mark daily attendance and track student presence easily.",
        icon = Icons.Default.CheckCircle
    ),
    ASSIGNMENTS(
        title = "Assignments",
        description = "Create, assign, and review student assignments.",
        icon = Icons.Default.Assignment
    ),
    EXAMS(
        title = "Exams",
        description = "Schedule exams, upload question papers, and manage results.",
        icon = Icons.Default.Quiz
    ),
    TIMETABLE(
        title = "TimeTable",
        description = "Access and update your weekly teaching schedule.",
        icon = Icons.Default.Schedule
    ),
    NOTICES(
        title = "Notices",
        description = "Post important announcements for students instantly.",
        icon = Icons.Default.Campaign
    ),
    QUESTION_PAPER(
        title = "Question Paper",
        description = "Create, upload, and manage question papers for exams.",
        icon = Icons.Default.Description
    )
}
