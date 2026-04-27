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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edutrack.data.model.Exam
import com.edutrack.data.model.ExamAttempt
import com.edutrack.data.model.MCQQuestion
import com.edutrack.data.model.UiState
import com.edutrack.data.model.UserProfile
import com.edutrack.ui.viewmodel.ExamViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherExamsScreen(
    teacherProfile: UserProfile,
    onBack: () -> Unit,
    viewModel: ExamViewModel = viewModel()
) {
    val examsState by viewModel.exams.collectAsStateWithLifecycle()
    val attemptsState by viewModel.examAttempts.collectAsStateWithLifecycle()
    val operationResult by viewModel.operationResult.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedExamForResults by remember { mutableStateOf<Exam?>(null) }

    LaunchedEffect(teacherProfile.userId) {
        viewModel.loadExamsByTeacher(teacherProfile.userId)
    }

    LaunchedEffect(operationResult) {
        operationResult?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearOperationResult()
        }
    }

    // When an exam is selected, fetch attempts
    LaunchedEffect(selectedExamForResults) {
        selectedExamForResults?.let {
            viewModel.loadAttemptsForExam(it.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MCQ Exams") },
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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Create Exam")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = examsState) {
                is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is UiState.Empty -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Assignment, null, Modifier.size(72.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No exams created yet.", color = Color.Gray)
                    }
                }
                is UiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data) { exam ->
                            TeacherExamCard(
                                exam = exam,
                                onViewResults = { selectedExamForResults = exam }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateExamFullScreenDialog(
            teacherProfile = teacherProfile,
            onDismiss = { showCreateDialog = false },
            onSave = { exam ->
                viewModel.createExam(exam)
                showCreateDialog = false
            }
        )
    }

    if (selectedExamForResults != null) {
        AlertDialog(
            onDismissRequest = { selectedExamForResults = null },
            title = { Text("Results: ${selectedExamForResults!!.title}") },
            text = {
                when (val attempts = attemptsState) {
                    is UiState.Loading -> Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    is UiState.Empty -> Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("No submissions yet.", color = Color.Gray)
                    }
                    is UiState.Error -> Text(attempts.message, color = MaterialTheme.colorScheme.error)
                    is UiState.Success -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(attempts.data) { attempt ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(attempt.studentName, fontWeight = FontWeight.Bold)
                                            Text(
                                                SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(Date(attempt.timestamp)),
                                                style = MaterialTheme.typography.bodySmall, color = Color.Gray
                                            )
                                        }
                                        Text(
                                            "${attempt.obtainedMarks} / ${attempt.totalMarks}",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedExamForResults = null }) { Text("Close") }
            }
        )
    }
}

@Composable
fun TeacherExamCard(exam: Exam, onViewResults: () -> Unit) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(exam.timestamp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(exam.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${exam.questions.size} Qs", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text("Assigned to: ${exam.userClass} (Div ${exam.division})", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text("Created on: $dateStr", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onViewResults,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Assessment, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("View Results")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExamFullScreenDialog(
    teacherProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (Exam) -> Unit
) {
    var title by remember { mutableStateOf("") }
    // Hardcode class and division to teacher's profile for now
    var questions by remember { mutableStateOf(mutableListOf(MCQQuestion())) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = onDismiss,
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Create MCQ Exam")
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxSize()) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                }
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Exam Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Text("For Class: ${teacherProfile.userClass} | Div: ${teacherProfile.division}", fontWeight = FontWeight.Bold)
                
                Spacer(Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(questions.size) { index ->
                        val q = questions[index]
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Question ${index + 1}", fontWeight = FontWeight.Bold)
                                    if (questions.size > 1) {
                                        IconButton(onClick = { 
                                            questions = questions.toMutableList().apply { removeAt(index) } 
                                        }) {
                                            Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                                OutlinedTextField(
                                    value = q.text,
                                    onValueChange = { newText ->
                                        questions = questions.toMutableList().apply {
                                            this[index] = q.copy(text = newText)
                                        }
                                    },
                                    label = { Text("Question Text") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("Options (Select Correct Answer):", style = MaterialTheme.typography.bodySmall)
                                
                                // Provide 4 options
                                val currentOptions = if (q.options.size == 4) q.options else listOf("", "", "", "")
                                
                                (0..3).forEach { optIndex ->
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                        RadioButton(
                                            selected = q.correctAnswerIndex == optIndex,
                                            onClick = {
                                                questions = questions.toMutableList().apply {
                                                    this[index] = q.copy(correctAnswerIndex = optIndex, options = currentOptions)
                                                }
                                            }
                                        )
                                        OutlinedTextField(
                                            value = currentOptions[optIndex],
                                            onValueChange = { newOptText ->
                                                val newOpts = currentOptions.toMutableList()
                                                newOpts[optIndex] = newOptText
                                                questions = questions.toMutableList().apply {
                                                    this[index] = q.copy(options = newOpts)
                                                }
                                            },
                                            label = { Text("Option ${'A' + optIndex}") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Button(
                            onClick = { questions = questions.toMutableList().apply { add(MCQQuestion()) } },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Add, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Add Another Question")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedTitle = title.trim()
                    if (trimmedTitle.isBlank()) {
                        errorMessage = "Please enter an Exam Title."
                        return@Button
                    }
                    
                    val invalidQIndex = questions.indexOfFirst { q ->
                        q.text.isBlank() || 
                        q.correctAnswerIndex !in 0..3 || 
                        q.options.size != 4 || 
                        q.options.any { it.isBlank() }
                    }
                    
                    if (invalidQIndex != -1) {
                        errorMessage = "Question ${invalidQIndex + 1} is incomplete. Make sure the question text, all 4 options, and the correct answer are filled out."
                        return@Button
                    }
                    
                    errorMessage = null
                    val exam = Exam(
                        title = trimmedTitle,
                        userClass = teacherProfile.userClass,
                        division = teacherProfile.division,
                        teacherId = teacherProfile.userId,
                        teacherName = teacherProfile.name,
                        questions = questions
                    )
                    onSave(exam)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publish Exam")
            }
        }
    )
}
