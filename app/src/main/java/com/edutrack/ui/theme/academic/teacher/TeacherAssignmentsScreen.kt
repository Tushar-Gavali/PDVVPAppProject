package com.edutrack.ui.academic.teacher

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
import androidx.compose.ui.text.style.TextAlign
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
 * TeacherAssignmentsScreen
 *
 * - Lists all assignments posted by this teacher (real-time Firestore)
 * - FAB to create a new assignment
 * - Each card: Edit, Delete, View Submissions
 * - Submission sheet shows student submissions with evaluation dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAssignmentsScreen(
    teacherProfile: UserProfile,
    onBack: () -> Unit,
    viewModel: AssignmentViewModel = viewModel()
) {
    val assignmentsState by viewModel.assignments.collectAsStateWithLifecycle()
    val submissionsState by viewModel.submissions.collectAsStateWithLifecycle()
    val submissionCounts by viewModel.submissionCounts.collectAsStateWithLifecycle()
    val operationResult by viewModel.operationResult.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog states
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingAssignment by remember { mutableStateOf<Assignment?>(null) }
    var viewingSubmissionsFor by remember { mutableStateOf<Assignment?>(null) }
    var evaluatingSubmission by remember { mutableStateOf<Submission?>(null) }

    // Load teacher's assignments on launch
    LaunchedEffect(teacherProfile.userId) {
        viewModel.loadForTeacher(teacherProfile.userId)
    }

    // Show snackbar on operation result
    LaunchedEffect(operationResult) {
        operationResult?.let { msg ->
            snackbarHostState.showSnackbar(msg)
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("New Assignment") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        when (val state = assignmentsState) {
            is UiState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is UiState.Empty -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Assignment,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No assignments posted yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Tap + to create one.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }

            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.data, key = { it.id }) { assignment ->
                        TeacherAssignmentCard(
                            assignment = assignment,
                            submissionCount = submissionCounts[assignment.id] ?: 0,
                            onEdit = { editingAssignment = assignment },
                            onDelete = { viewModel.deleteAssignment(assignment.id) },
                            onViewSubmissions = {
                                viewingSubmissionsFor = assignment
                                viewModel.loadSubmissionsFor(assignment.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // ── Create / Edit Assignment Dialog ───────────────────────────────────────
    if (showCreateDialog || editingAssignment != null) {
        AssignmentFormDialog(
            initial = editingAssignment,
            teacherProfile = teacherProfile,
            onDismiss = { showCreateDialog = false; editingAssignment = null },
            onSave = { assignment ->
                if (editingAssignment != null) viewModel.updateAssignment(assignment)
                else viewModel.uploadAssignment(assignment)
                showCreateDialog = false
                editingAssignment = null
            }
        )
    }

    // ── View Submissions Bottom Sheet ───────────────────────────────────────────
    viewingSubmissionsFor?.let { assignment ->
        ModalBottomSheet(
            onDismissRequest = { viewingSubmissionsFor = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Submissions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            assignment.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    // Submission count badge
                    if (submissionsState is UiState.Success) {
                        val count = (submissionsState as UiState.Success<List<Submission>>).data.size
                        val evaluated = (submissionsState as UiState.Success<List<Submission>>).data.count { it.marksAwarded >= 0 }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "$count",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    if (count == 1) "submitted" else "submitted",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (evaluated > 0)
                                    Text(
                                        "$evaluated evaluated",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))

                when (val subs = submissionsState) {
                    is UiState.Loading -> Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    is UiState.Empty -> {
                        Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Inbox, null,
                                    Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "No submissions yet.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Students haven't submitted this assignment.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    is UiState.Error -> Text(subs.message, color = MaterialTheme.colorScheme.error)
                    is UiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 500.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(subs.data) { submission ->
                                SubmissionCard(
                                    submission = submission,
                                    onEvaluate = { evaluatingSubmission = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Evaluate Submission Dialog ─────────────────────────────────────────────
    evaluatingSubmission?.let { sub ->
        EvaluateSubmissionDialog(
            submission = sub,
            onDismiss = { evaluatingSubmission = null },
            onSave = { marks, feedback ->
                viewModel.evaluateSubmission(sub.id, marks, feedback)
                evaluatingSubmission = null
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Assignment Card (Teacher view)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun TeacherAssignmentCard(
    assignment: Assignment,
    submissionCount: Int = 0,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewSubmissions: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val dateStr = remember(assignment.dueDate) {
        if (assignment.dueDate > 0L)
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(assignment.dueDate))
        else "No due date"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        assignment.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                "${assignment.userClass} - Div ${assignment.division}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Submission count badge
                    if (submissionCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF1B5E20).copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.People, null,
                                    Modifier.size(12.dp),
                                    tint = Color(0xFF2E7D32)
                                )
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    "$submissionCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                        Spacer(Modifier.width(4.dp))
                    }
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
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
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Text("Due: $dateStr", style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onViewSubmissions,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (submissionCount > 0) "View Submissions ($submissionCount)"
                    else "View Submissions"
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Assignment") },
            text = { Text("Are you sure you want to delete \"${assignment.title}\"? This will also delete all submissions.") },
            confirmButton = {
                Button(
                    onClick = { onDelete(); showDeleteConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Submission Card (inside bottom sheet)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SubmissionCard(
    submission: Submission,
    onEvaluate: (Submission) -> Unit
) {
    val dateStr = remember(submission.submittedAt) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(submission.submittedAt))
    }
    val isEvaluated = submission.marksAwarded >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEvaluated)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top row: student avatar + name + status chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    // Avatar
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                submission.studentName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            submission.studentName,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, Modifier.size(10.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(3.dp))
                            Text(
                                dateStr,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Status chip
                if (isEvaluated) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.EmojiEvents, null, Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(3.dp))
                            Text(
                                "${submission.marksAwarded} marks",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1B5E20).copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, null, Modifier.size(12.dp),
                                tint = Color(0xFF2E7D32))
                            Spacer(Modifier.width(3.dp))
                            Text(
                                "Submitted",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            // Student note
            if (submission.note.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Notes, null, Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            submission.note,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Teacher feedback
            if (submission.feedback.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.RateReview, null, Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            submission.feedback,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = { onEvaluate(submission) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    if (isEvaluated) Icons.Default.EditNote else Icons.Default.RateReview,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(if (isEvaluated) "Edit Evaluation" else "Evaluate & Award Marks")
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Create / Edit Assignment Form Dialog
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentFormDialog(
    initial: Assignment? = null,
    teacherProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (Assignment) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var dueDate by remember { mutableStateOf(initial?.dueDate ?: 0L) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = if (dueDate > 0L) dueDate else null)
    val isEditing = initial != null

    val dueDateLabel = if (dueDate > 0L)
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(dueDate))
    else "Select Due Date"

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dueDate = datePickerState.selectedDateMillis ?: 0L
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edit Assignment" else "New Assignment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(dueDateLabel)
                }
                // Class + division (inherited from teacher profile)
                Text(
                    "For: ${teacherProfile.userClass} — Division ${teacherProfile.division}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) return@Button
                    val assignment = (initial ?: Assignment()).copy(
                        title = title.trim(),
                        description = description.trim(),
                        userClass = teacherProfile.userClass,
                        division = teacherProfile.division,
                        uploadedBy = teacherProfile.userId,
                        uploadedByName = teacherProfile.name,
                        dueDate = dueDate,
                        timestamp = System.currentTimeMillis()
                    )
                    onSave(assignment)
                }
            ) { Text(if (isEditing) "Update" else "Post") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Evaluate Submission Dialog
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun EvaluateSubmissionDialog(
    submission: Submission,
    onDismiss: () -> Unit,
    onSave: (marks: Int, feedback: String) -> Unit
) {
    var marksText by remember { mutableStateOf(if (submission.marksAwarded >= 0) submission.marksAwarded.toString() else "") }
    var feedback by remember { mutableStateOf(submission.feedback) }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Evaluate — ${submission.studentName}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (submission.note.isNotBlank()) {
                    Text("Note: ${submission.note}", style = MaterialTheme.typography.bodySmall)
                    Divider()
                }
                OutlinedTextField(
                    value = marksText,
                    onValueChange = { marksText = it.filter { c -> c.isDigit() } },
                    label = { Text("Marks Awarded *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    label = { Text("Feedback (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
        },
        confirmButton = {
            Button(onClick = {
                val marks = marksText.toIntOrNull()
                if (marks == null) { error = "Enter a valid number"; return@Button }
                onSave(marks, feedback)
            }) { Text("Save") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
