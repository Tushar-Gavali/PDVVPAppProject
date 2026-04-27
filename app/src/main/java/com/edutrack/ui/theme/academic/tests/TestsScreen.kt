package com.edutrack.ui.academic.tests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edutrack.data.model.Exam
import com.edutrack.data.model.ExamAttempt
import com.edutrack.data.model.UiState
import com.edutrack.data.model.UserProfile
import com.edutrack.ui.viewmodel.ExamViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestsScreen(
    studentProfile: UserProfile,
    onBack: () -> Unit,
    viewModel: ExamViewModel = viewModel()
) {
    val examsState by viewModel.exams.collectAsStateWithLifecycle()
    val attemptsState by viewModel.examAttempts.collectAsStateWithLifecycle()
    val operationResult by viewModel.operationResult.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTab by remember { mutableStateOf(0) }
    var activeExam by remember { mutableStateOf<Exam?>(null) } // Set when student starts an exam

    LaunchedEffect(studentProfile.userId, studentProfile.userClass, studentProfile.division) {
        viewModel.loadExamsForStudent(studentProfile.userClass, studentProfile.division)
        viewModel.loadAttemptsForStudent(studentProfile.userId)
    }

    LaunchedEffect(operationResult) {
        operationResult?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearOperationResult()
        }
    }

    if (activeExam != null) {
        ExamTakingScreen(
            exam = activeExam!!,
            studentProfile = studentProfile,
            onDismiss = { activeExam = null },
            onSubmit = { attempt ->
                viewModel.submitExamAttempt(attempt)
                activeExam = null
                selectedTab = 1 // Switch to results tab automatically
            }
        )
        return // Take up full screen instead of standard scaffold
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tests & Exams") },
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
                    text = { Text("Available Exams") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Test Results") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // Filter exams that have already been attempted
                    val attemptsList = if (attemptsState is UiState.Success) {
                        (attemptsState as UiState.Success<List<ExamAttempt>>).data
                    } else emptyList()
                    val attemptedExamIds = attemptsList.map { it.examId }.toSet()

                    when (val state = examsState) {
                        is UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
                        is UiState.Empty -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No exams assigned to your class.", color = Color.Gray) }
                        is UiState.Success -> {
                            val availableExams = state.data.filter { it.id !in attemptedExamIds }
                            if (availableExams.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("You've completed all assigned exams!", color = Color.Gray) }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(availableExams) { exam ->
                                        StudentExamCard(exam = exam, onClick = { activeExam = exam })
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    when (val state = attemptsState) {
                        is UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
                        is UiState.Empty -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No test results available yet.", color = Color.Gray) }
                        is UiState.Success -> {
                            // Need exam details to map exam ID to Exam Title (or we can just store title in Attempt)
                            // We didn't store title in Attempt in FirestoreModels, so let's cross-reference exams
                            val examsList = if (examsState is UiState.Success) {
                                (examsState as UiState.Success<List<Exam>>).data
                            } else emptyList()
                            val examMap = examsList.associateBy { it.id }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.data) { attempt ->
                                    val examTitle = examMap[attempt.examId]?.title ?: "Unknown Exam"
                                    TestResultCard(attempt = attempt, examTitle = examTitle)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentExamCard(exam: Exam, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = exam.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "By Instructor: ${exam.teacherName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${exam.questions.size} Questions", style = MaterialTheme.typography.labelMedium)
                Button(onClick = onClick) {
                    Text("Start Exam")
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, null, Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun TestResultCard(attempt: ExamAttempt, examTitle: String) {
    val percentage = if (attempt.totalMarks > 0) ((attempt.obtainedMarks.toFloat() / attempt.totalMarks) * 100).toInt() else 0
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = examTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Submitted on ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(attempt.timestamp))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Score: ${attempt.obtainedMarks}/${attempt.totalMarks} ($percentage%)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        percentage >= 80 -> Color(0xFF4CAF50)
                        percentage >= 60 -> Color(0xFF2196F3)
                        percentage >= 40 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )
            ) {
                Text(
                    text = when {
                        percentage >= 80 -> "A"
                        percentage >= 60 -> "B"
                        percentage >= 40 -> "C"
                        else -> "Fail"
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamTakingScreen(
    exam: Exam,
    studentProfile: UserProfile,
    onDismiss: () -> Unit,
    onSubmit: (ExamAttempt) -> Unit
) {
    // Map to store student's selected option index for each question index
    var selectedAnswers by remember { mutableStateOf(mutableMapOf<Int, Int>()) }
    var submitConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exam.title) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel Exam")
                    }
                },
                actions = {
                    Text(
                        "${selectedAnswers.size}/${exam.questions.size} Answered",
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { submitConfirmDialog = true },
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Icon(Icons.Default.Send, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Submit Exam")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(exam.questions) { qIndex, question ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Question ${qIndex + 1}", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(question.text, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(12.dp))
                        
                        question.options.forEachIndexed { optIndex, optionText ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedAnswers[qIndex] == optIndex,
                                    onClick = { 
                                        val newAnswers = selectedAnswers.toMutableMap()
                                        newAnswers[qIndex] = optIndex
                                        selectedAnswers = newAnswers
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(optionText, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }

        if (submitConfirmDialog) {
            AlertDialog(
                onDismissRequest = { submitConfirmDialog = false },
                title = { Text("Submit Exam?") },
                text = { Text("Are you sure you want to submit? You have answered ${selectedAnswers.size} out of ${exam.questions.size} questions.") },
                confirmButton = {
                    Button(onClick = {
                        // Auto-grade submission
                        var obtainedMarks = 0
                        var totalMarks = 0
                        exam.questions.forEachIndexed { idx, q ->
                            totalMarks += q.marks
                            val studentAnswer = selectedAnswers[idx] ?: -1
                            if (studentAnswer == q.correctAnswerIndex) {
                                obtainedMarks += q.marks
                            }
                        }

                        val attempt = ExamAttempt(
                            examId = exam.id,
                            studentId = studentProfile.userId,
                            studentName = studentProfile.name,
                            obtainedMarks = obtainedMarks,
                            totalMarks = totalMarks
                        )
                        onSubmit(attempt)
                        submitConfirmDialog = false
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { submitConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
