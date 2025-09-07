package com.edutrack.ui.academic.assignments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edutrack.data.model.Assignment
import com.edutrack.data.model.AssignmentSubmission
import com.edutrack.data.model.SubmissionStatus
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentsScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assignments") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Create new assignment */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Assignment")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Active") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Submitted") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Graded") }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("All") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> ActiveAssignmentsView()
                1 -> SubmittedAssignmentsView()
                2 -> GradedAssignmentsView()
                3 -> AllAssignmentsView()
            }
        }
    }
}

@Composable
fun ActiveAssignmentsView() {
    // Mock data for active assignments
    val activeAssignments = remember {
        listOf(
            Assignment(
                id = "A001",
                title = "Data Structures Implementation",
                description = "Implement various data structures including linked lists, stacks, and queues in your preferred programming language.",
                subjectId = "CS201",
                assignedBy = "Dr. Smith",
                assignedTo = listOf("S001", "S002", "S003"),
                totalMarks = 50,
                dueDate = LocalDate.now().plusDays(7),
                dueTime = "23:59",
                instructions = "Submit source code with proper documentation and test cases.",
                attachments = listOf("assignment_guidelines.pdf")
            ),
            Assignment(
                id = "A002",
                title = "Database Design Project",
                description = "Design and implement a database for a library management system.",
                subjectId = "CS301",
                assignedBy = "Dr. Davis",
                assignedTo = listOf("S001", "S002"),
                totalMarks = 75,
                dueDate = LocalDate.now().plusDays(14),
                dueTime = "18:00",
                instructions = "Include ER diagram, normalized tables, and sample queries.",
                attachments = listOf("project_requirements.pdf", "sample_data.sql")
            )
        )
    }

    AssignmentList(
        assignments = activeAssignments,
        emptyMessage = "No active assignments",
        showStatus = false
    )
}

@Composable
fun SubmittedAssignmentsView() {
    // Mock data for submitted assignments
    val submittedAssignments = remember {
        listOf(
            Assignment(
                id = "A003",
                title = "Algorithm Analysis Report",
                description = "Analyze time and space complexity of sorting algorithms.",
                subjectId = "CS202",
                assignedBy = "Prof. Johnson",
                assignedTo = listOf("S001"),
                totalMarks = 40,
                dueDate = LocalDate.now().minusDays(3),
                dueTime = "23:59",
                instructions = "Submit detailed report with complexity analysis."
            )
        )
    }

    AssignmentList(
        assignments = submittedAssignments,
        emptyMessage = "No submitted assignments",
        showStatus = true
    )
}

@Composable
fun GradedAssignmentsView() {
    // Mock data for graded assignments with submissions
    val gradedSubmissions = remember {
        listOf(
            Pair(
                Assignment(
                    id = "A004",
                    title = "Web Development Portfolio",
                    description = "Create a personal portfolio website using HTML, CSS, and JavaScript.",
                    subjectId = "CS303",
                    assignedBy = "Prof. Taylor",
                    assignedTo = listOf("S001"),
                    totalMarks = 100,
                    dueDate = LocalDate.now().minusDays(10),
                    dueTime = "23:59"
                ),
                AssignmentSubmission(
                    id = "AS004",
                    assignmentId = "A004",
                    studentId = "S001",
                    submissionText = "Portfolio website with responsive design and interactive features.",
                    attachments = listOf("portfolio.zip", "documentation.pdf"),
                    submittedAt = LocalDate.now().minusDays(8).atTime(20, 30),
                    status = SubmissionStatus.EVALUATED,
                    grade = "A",
                    obtainedMarks = 92,
                    feedback = "Excellent work! Great use of modern web technologies and responsive design."
                )
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Graded Assignments",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(gradedSubmissions) { (assignment, submission) ->
            GradedAssignmentCard(assignment = assignment, submission = submission)
        }
    }
}

@Composable
fun AllAssignmentsView() {
    // Combine all assignments
    val allAssignments = remember {
        listOf(
            Assignment(
                id = "A001",
                title = "Data Structures Implementation",
                description = "Implement various data structures including linked lists, stacks, and queues.",
                subjectId = "CS201",
                assignedBy = "Dr. Smith",
                assignedTo = listOf("S001"),
                totalMarks = 50,
                dueDate = LocalDate.now().plusDays(7),
                dueTime = "23:59"
            ),
            Assignment(
                id = "A002",
                title = "Database Design Project",
                description = "Design and implement a database for a library management system.",
                subjectId = "CS301",
                assignedBy = "Dr. Davis",
                assignedTo = listOf("S001"),
                totalMarks = 75,
                dueDate = LocalDate.now().plusDays(14),
                dueTime = "18:00"
            ),
            Assignment(
                id = "A003",
                title = "Algorithm Analysis Report",
                description = "Analyze time and space complexity of sorting algorithms.",
                subjectId = "CS202",
                assignedBy = "Prof. Johnson",
                assignedTo = listOf("S001"),
                totalMarks = 40,
                dueDate = LocalDate.now().minusDays(3),
                dueTime = "23:59"
            )
        )
    }

    AssignmentList(
        assignments = allAssignments,
        emptyMessage = "No assignments found",
        showStatus = true
    )
}

@Composable
fun AssignmentList(
    assignments: List<Assignment>,
    emptyMessage: String,
    showStatus: Boolean = true
) {
    if (assignments.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(assignments) { assignment ->
                AssignmentCard(
                    assignment = assignment,
                    showStatus = showStatus
                )
            }
        }
    }
}

@Composable
fun AssignmentCard(
    assignment: Assignment,
    showStatus: Boolean = true
) {
    val isOverdue = assignment.dueDate.isBefore(LocalDate.now())
    val isDueSoon = assignment.dueDate.isBefore(LocalDate.now().plusDays(2))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { /* Navigate to assignment details */ }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = assignment.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = assignment.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                if (showStatus) {
                    AssistChip(
                        onClick = { },
                        label = { 
                            Text(
                                when {
                                    isOverdue -> "Overdue"
                                    isDueSoon -> "Due Soon"
                                    else -> "Active"
                                }
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = when {
                                isOverdue -> Color(0xFFFFEBEE)
                                isDueSoon -> Color(0xFFFFF3E0)
                                else -> Color(0xFFE8F5E8)
                            }
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Assignment details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = "Due Date", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Due: ${assignment.dueDate} at ${assignment.dueTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOverdue) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text("Marks: ${assignment.totalMarks}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Assigned by: ${assignment.assignedBy}", style = MaterialTheme.typography.bodySmall)
                Text("Subject: ${assignment.subjectId}", style = MaterialTheme.typography.bodySmall)
            }
            
            // Instructions
            assignment.instructions?.let { instructions ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "Instructions: $instructions",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            
            // Attachments
            assignment.attachments?.let { attachments ->
                if (attachments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Attachments", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Attachments: ${attachments.size} files",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GradedAssignmentCard(
    assignment: Assignment,
    submission: AssignmentSubmission
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = assignment.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = assignment.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (submission.grade) {
                            "A+", "A" -> Color(0xFF4CAF50)
                            "B+", "B" -> Color(0xFF2196F3)
                            "C+", "C" -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        }
                    )
                ) {
                    Text(
                        text = submission.grade ?: "N/A",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Submitted: ${submission.submittedAt?.toLocalDate()}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Marks: ${submission.obtainedMarks}/${assignment.totalMarks}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
            
            submission.feedback?.let { feedback ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = "Feedback: $feedback",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
