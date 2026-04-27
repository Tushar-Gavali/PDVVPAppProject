package com.edutrack.ui.academic.assignments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edutrack.data.model.Assignment
import com.edutrack.data.model.Submission
import com.edutrack.data.model.UiState
import com.edutrack.data.model.UserProfile
import com.edutrack.ui.viewmodel.AssignmentViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * AssignmentsScreen — Student view
 *
 * - Lists assignments filtered by the student's class+division (real-time)
 * - Shows submission status per assignment
 * - Submit dialog with optional note
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentsScreen(
    studentProfile: UserProfile,
    onBack: () -> Unit,
    viewModel: AssignmentViewModel = viewModel()
) {
    val assignmentsState by viewModel.assignments.collectAsStateWithLifecycle()
    val mySubmissions by viewModel.mySubmissions.collectAsStateWithLifecycle()
    val operationResult by viewModel.operationResult.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var submittingAssignment by remember { mutableStateOf<Assignment?>(null) }

    // Build a quick lookup: assignmentId -> Submission (for status)
    val submissionMap: Map<String, Submission> = remember(mySubmissions) {
        if (mySubmissions is UiState.Success) {
            (mySubmissions as UiState.Success<List<Submission>>).data.associateBy { it.assignmentId }
        } else emptyMap()
    }

    LaunchedEffect(studentProfile.userId, studentProfile.userClass, studentProfile.division) {
        viewModel.loadForStudent(studentProfile.userClass, studentProfile.division)
        viewModel.loadMySubmissions(studentProfile.userId)
    }

    LaunchedEffect(operationResult) {
        operationResult?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearOperationResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assignments") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        when (val state = assignmentsState) {
            is UiState.Loading -> Box(
                Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is UiState.Empty -> Box(
                Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Assignment, contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("No assignments posted yet.", style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            is UiState.Error -> Box(
                Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center
            ) { Text(state.message, color = MaterialTheme.colorScheme.error) }

            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.data, key = { it.id }) { assignment ->
                        val existingSubmission = submissionMap[assignment.id]
                        StudentAssignmentCard(
                            assignment = assignment,
                            submission = existingSubmission,
                            onSubmit = { submittingAssignment = assignment }
                        )
                    }
                }
            }
        }
    }

    // ── Submit Assignment Dialog ────────────────────────────────────────
    submittingAssignment?.let { assignment ->
        SubmitAssignmentDialog(
            assignment = assignment,
            studentProfile = studentProfile,
            onDismiss = { submittingAssignment = null },
            onSubmit = { note ->
                viewModel.submitAssignment(
                    Submission(
                        assignmentId = assignment.id,
                        assignmentTitle = assignment.title,
                        studentId = studentProfile.userId,
                        studentName = studentProfile.name,
                        note = note,
                        submittedAt = System.currentTimeMillis()
                    )
                )
                submittingAssignment = null
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Student Assignment Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun StudentAssignmentCard(
    assignment: Assignment,
    submission: Submission?,
    onSubmit: () -> Unit
) {
    val isOverdue = assignment.dueDate in 1 until System.currentTimeMillis()
    val dueDateStr = remember(assignment.dueDate) {
        if (assignment.dueDate > 0L)
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(assignment.dueDate))
        else "No due date"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        assignment.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "By: ${assignment.uploadedByName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // ── Status Badge ─────────────────────────────────────────────
                when {
                    // Evaluated: teacher awarded marks
                    submission != null && submission.marksAwarded >= 0 -> {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            tonalElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.EmojiEvents, null,
                                    Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "${submission.marksAwarded} marks",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    // Submitted: waiting for teacher evaluation
                    submission != null -> {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF1B5E20).copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle, null,
                                    Modifier.size(14.dp),
                                    tint = Color(0xFF2E7D32)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Submitted",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                    // Overdue: past due date, not submitted
                    isOverdue -> {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning, null,
                                    Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Overdue",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    // Pending: not yet submitted
                    else -> {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFF57F17).copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Schedule, null,
                                    Modifier.size(14.dp),
                                    tint = Color(0xFFE65100)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Pending",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                assignment.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarToday, null,
                    modifier = Modifier.size(14.dp),
                    tint = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "Due: $dueDateStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }

            // ── Submission timestamp
            if (submission != null) {
                val submittedStr = remember(submission.submittedAt) {
                    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(submission.submittedAt))
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Done, null, Modifier.size(12.dp), tint = Color(0xFF2E7D32))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Submitted on $submittedStr",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Feedback from teacher
            if (submission != null && submission.feedback.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.RateReview, null, Modifier.size(14.dp).padding(top = 2.dp),
                            tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Teacher: ${submission.feedback}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // ── Submit button only when not yet submitted
            if (submission == null) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Upload, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Submit Assignment")
                }
            }
        }
    }
}

// ───────────────────────────────────────────────────────────────────────────
// Submit Assignment Dialog
// ───────────────────────────────────────────────────────────────────────────
@Composable
fun SubmitAssignmentDialog(
    assignment: Assignment,
    studentProfile: UserProfile,
    onDismiss: () -> Unit,
    onSubmit: (note: String) -> Unit
) {
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Submit Assignment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Submitting for: ${assignment.title}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Student: ${studentProfile.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Add a note (optional)") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(note) }) { Text("Submit") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
