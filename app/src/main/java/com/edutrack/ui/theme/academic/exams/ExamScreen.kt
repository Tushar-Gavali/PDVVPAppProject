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
import com.edutrack.data.model.Test
import com.edutrack.data.model.TestType
import com.edutrack.data.model.TestResult
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OR / PR / Exams") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Schedule new exam */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Schedule Exam")
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
                    text = { Text("Written Exams") }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Results") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> OralExamsView()
                1 -> PracticalExamsView()
                2 -> WrittenExamsView()
                3 -> ExamResultsView()
            }
        }
    }
}

@Composable
fun OralExamsView() {
    // Mock data for oral exams
    val oralExams = remember {
        listOf(
            Test(
                id = "OR001",
                title = "Database Viva",
                description = "Oral examination on database concepts and queries",
                subjectId = "CS301",
                testType = TestType.ORAL,
                totalMarks = 25,
                duration = 15,
                scheduledDate = LocalDate.now().plusDays(5),
                scheduledTime = "10:00",
                venue = "Faculty Room 1",
                instructions = "Prepare topics: SQL, Normalization, Transactions",
                createdBy = "Dr. Davis"
            ),
            Test(
                id = "OR002",
                title = "Software Engineering Presentation",
                description = "Project presentation and Q&A session",
                subjectId = "CS302",
                testType = TestType.ORAL,
                totalMarks = 30,
                duration = 20,
                scheduledDate = LocalDate.now().plusDays(8),
                scheduledTime = "14:00",
                venue = "Conference Room",
                instructions = "Prepare project demo and documentation",
                createdBy = "Prof. Wilson"
            )
        )
    }

    ExamList(
        exams = oralExams,
        emptyMessage = "No oral exams scheduled"
    )
}

@Composable
fun PracticalExamsView() {
    // Mock data for practical exams
    val practicalExams = remember {
        listOf(
            Test(
                id = "PR001",
                title = "Programming Lab Practical",
                description = "Coding practical examination on data structures",
                subjectId = "CS201",
                testType = TestType.PRACTICAL,
                totalMarks = 50,
                duration = 120,
                scheduledDate = LocalDate.now().plusDays(10),
                scheduledTime = "09:00",
                venue = "Computer Lab 1",
                instructions = "Bring student ID and be familiar with IDE",
                createdBy = "Dr. Smith"
            ),
            Test(
                id = "PR002",
                title = "Web Development Practical",
                description = "Build a complete web application",
                subjectId = "CS303",
                testType = TestType.PRACTICAL,
                totalMarks = 75,
                duration = 180,
                scheduledDate = LocalDate.now().plusDays(12),
                scheduledTime = "13:00",
                venue = "Computer Lab 2",
                instructions = "HTML, CSS, JavaScript, and frameworks allowed",
                createdBy = "Prof. Taylor"
            )
        )
    }

    ExamList(
        exams = practicalExams,
        emptyMessage = "No practical exams scheduled"
    )
}

@Composable
fun WrittenExamsView() {
    // Mock data for written exams
    val writtenExams = remember {
        listOf(
            Test(
                id = "WE001",
                title = "Computer Networks Final Exam",
                description = "Final written examination covering all network concepts",
                subjectId = "CS304",
                testType = TestType.WRITTEN,
                totalMarks = 100,
                duration = 180,
                scheduledDate = LocalDate.now().plusDays(20),
                scheduledTime = "09:00",
                venue = "Examination Hall A",
                instructions = "Bring calculator and writing materials only",
                createdBy = "Dr. Brown"
            ),
            Test(
                id = "WE002",
                title = "Operating Systems Mid-Term",
                description = "Mid-semester written examination",
                subjectId = "CS305",
                testType = TestType.WRITTEN,
                totalMarks = 50,
                duration = 90,
                scheduledDate = LocalDate.now().plusDays(15),
                scheduledTime = "14:00",
                venue = "Examination Hall B",
                instructions = "Closed book examination",
                createdBy = "Prof. Martinez"
            )
        )
    }

    ExamList(
        exams = writtenExams,
        emptyMessage = "No written exams scheduled"
    )
}

@Composable
fun ExamResultsView() {
    // Mock data for exam results
    val examResults = remember {
        listOf(
            TestResult(
                id = "ER001",
                testId = "OR001",
                studentId = "S001",
                obtainedMarks = 22,
                totalMarks = 25,
                percentage = 88.0,
                grade = "A+",
                remarks = "Excellent understanding of concepts"
            ),
            TestResult(
                id = "ER002",
                testId = "PR001",
                studentId = "S001",
                obtainedMarks = 42,
                totalMarks = 50,
                percentage = 84.0,
                grade = "A",
                remarks = "Good coding skills, minor optimization issues"
            ),
            TestResult(
                id = "ER003",
                testId = "WE001",
                studentId = "S001",
                obtainedMarks = 78,
                totalMarks = 100,
                percentage = 78.0,
                grade = "B+",
                remarks = "Good theoretical knowledge"
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
                text = "Exam Results",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(examResults) { result ->
            ExamResultCard(result = result)
        }
    }
}

@Composable
fun ExamList(
    exams: List<Test>,
    emptyMessage: String
) {
    if (exams.isEmpty()) {
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
            items(exams) { exam ->
                ExamCard(exam = exam)
            }
        }
    }
}

@Composable
fun ExamCard(exam: Test) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { /* Navigate to exam details */ }
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
                        text = exam.description ?: "No description",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                AssistChip(
                    onClick = { },
                    label = { Text(exam.testType.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (exam.testType) {
                            TestType.ORAL -> Color(0xFFE3F2FD)
                            TestType.PRACTICAL -> Color(0xFFF3E5F5)
                            TestType.WRITTEN -> Color(0xFFE8F5E8)
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
                    Text("${exam.scheduledDate}", style = MaterialTheme.typography.bodySmall)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Time", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${exam.scheduledTime} (${exam.duration} mins)", style = MaterialTheme.typography.bodySmall)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                exam.venue?.let { venue ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Venue", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(venue, style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                Text("Marks: ${exam.totalMarks}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            }
            
            // Instructions
            exam.instructions?.let { instructions ->
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
        }
    }
}

@Composable
fun ExamResultCard(result: TestResult) {
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
                    text = "Exam ID: ${result.testId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result.remarks ?: "No remarks",
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
                    Text(
                        text = "(${result.percentage}%)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when (result.grade) {
                        "A+", "A" -> Color(0xFF4CAF50)
                        "B+", "B" -> Color(0xFF2196F3)
                        "C+", "C" -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )
            ) {
                Text(
                    text = result.grade,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
