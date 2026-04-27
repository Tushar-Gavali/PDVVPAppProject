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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.edutrack.data.model.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboard(
    teacherProfile: UserProfile,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit,
    onFeatureClick: (TeacherFeature) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Teacher Dashboard")
                        Text(
                            "Welcome, ${teacherProfile.name.ifBlank { "Teacher" }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
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
        // Class+division info banner
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Info chip
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Class ${teacherProfile.userClass} — Division ${teacherProfile.division}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
            Spacer(Modifier.height(12.dp))
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
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
        description = "View and manage all your scheduled classes.",
        icon = Icons.Default.School
    ),
    STUDENTS(
        title = "Students",
        description = "Check student details and performance.",
        icon = Icons.Default.People
    ),
    ATTENDANCE(
        title = "Attendance",
        description = "Mark daily attendance and track presence.",
        icon = Icons.Default.CheckCircle
    ),
    ASSIGNMENTS(
        title = "Assignments",
        description = "Create, assign, and review student assignments.",
        icon = Icons.Default.Assignment
    ),
    MARKS(
        title = "Marks",
        description = "Add and update student marks per subject.",
        icon = Icons.Default.Grade
    ),
    EXAMS(
        title = "MCQ Exams",
        description = "Schedule MCQ exams and manage results.",
        icon = Icons.Default.Quiz
    ),
    OR_PR_EXAM(
        title = "OR/PR/Exam",
        description = "Create Oral, Practical & Written Exams.",
        icon = Icons.Default.School
    ),
    TIMETABLE(
        title = "TimeTable",
        description = "Manage and publish the weekly schedule.",
        icon = Icons.Default.Schedule
    ),
    NOTICES(
        title = "Notices",
        description = "Post important announcements for students.",
        icon = Icons.Default.Campaign
    ),
    QUESTION_PAPER(
        title = "Question Paper",
        description = "Create and manage question papers for exams.",
        icon = Icons.Default.Description
    )
}
