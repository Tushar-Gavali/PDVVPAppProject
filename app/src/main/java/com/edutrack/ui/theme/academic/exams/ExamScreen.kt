package com.edutrack.ui.academic.exams

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edutrack.data.model.OrPrExamDoc
import com.edutrack.data.model.OrPrExamResultDoc
import com.edutrack.data.model.UiState
import com.edutrack.data.model.UserProfile
import com.edutrack.ui.viewmodel.OrPrExamViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    studentProfile: UserProfile,
    onBack: () -> Unit,
    viewModel: OrPrExamViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    val examsState by viewModel.studentExams.collectAsStateWithLifecycle()
    val resultsState by viewModel.studentResults.collectAsStateWithLifecycle()

    LaunchedEffect(studentProfile.userClass, studentProfile.division) {
        viewModel.loadExamsForStudent(studentProfile.userClass, studentProfile.division)
        viewModel.loadResultsForStudent(studentProfile.userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OR / PR / Exams") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                    text = { Text("Oral (OR)") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Practical (PR)") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Written") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> FilteredExamsView(examsState, "ORAL", "No oral exams scheduled")
                1 -> FilteredExamsView(examsState, "PRACTICAL", "No practical exams scheduled")
                2 -> FilteredExamsView(examsState, "WRITTEN", "No written exams scheduled")
            }
        }
    }
}

@Composable
fun FilteredExamsView(
    examsState: UiState<List<OrPrExamDoc>>,
    typeFilter: String,
    emptyMessage: String
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (examsState) {
            is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            is UiState.Empty -> Text(emptyMessage, color = Color.Gray, modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> Text(examsState.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            is UiState.Success -> {
                val filteredList = examsState.data.filter { it.testType.equals(typeFilter, ignoreCase = true) }
                if (filteredList.isEmpty()) {
                    Text(emptyMessage, color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredList, key = { it.id }) { exam ->
                            ExamCard(exam = exam)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExamResultsView(resultsState: UiState<List<OrPrExamResultDoc>>) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (resultsState) {
            is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            is UiState.Empty -> {
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No results uploaded yet.", color = Color.Gray)
                }
            }
            is UiState.Error -> Text(resultsState.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Exam Results",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(resultsState.data, key = { it.id }) { result ->
                        ExamResultCard(result = result)
                    }
                }
            }
        }
    }
}

@Composable
fun ExamCard(exam: OrPrExamDoc) {
    val dateString = if (exam.scheduledDate > 0) 
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(exam.scheduledDate)) 
    else "TBD"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { /* Navigate to exam details if capable */ }
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
                        text = exam.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = exam.description.ifBlank { "No description" },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                AssistChip(
                    onClick = { },
                    label = { Text(exam.testType) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (exam.testType.uppercase()) {
                            "ORAL" -> Color(0xFFE3F2FD)
                            "PRACTICAL" -> Color(0xFFF3E5F5)
                            "WRITTEN" -> Color(0xFFE8F5E8)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Exam details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = "Date", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(dateString, style = MaterialTheme.typography.bodySmall)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Time", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${exam.scheduledTime.ifBlank { "TBD" }} (${exam.duration} mins)", style = MaterialTheme.typography.bodySmall)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Venue", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(exam.venue.ifBlank { "TBD" }, style = MaterialTheme.typography.bodySmall)
                }
                Text("Marks: ${exam.totalMarks}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            }
            
            // Instructions
            if (exam.instructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "Instructions: ${exam.instructions}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ExamResultCard(result: OrPrExamResultDoc) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Test Name: Assigned Test", // Can join locally if needed
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result.remarks.ifBlank { "No remarks" },
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = "${result.obtainedMarks}/${result.totalMarks}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val percentage = if (result.totalMarks > 0) ((result.obtainedMarks.toFloat() / result.totalMarks) * 100).toInt() else 0
                    Text(
                        text = "($percentage%)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when (result.grade.uppercase()) {
                        "A+", "A" -> Color(0xFF4CAF50)
                        "B+", "B" -> Color(0xFF2196F3)
                        "C+", "C" -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )
            ) {
                Text(
                    text = result.grade.ifBlank { "N/A" },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
