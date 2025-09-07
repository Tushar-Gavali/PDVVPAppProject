package com.edutrack.ui.academic.tests

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
fun TestsScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tests & Exams") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Add new test */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Test")
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
                    text = { Text("Question Papers") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Internal Tests") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Results") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> QuestionPapersView()
                1 -> InternalTestsView()
                2 -> TestResultsView()
            }
        }
    }
}

@Composable
fun QuestionPapersView() {
    // Mock data for question papers
    val questionPapers = remember {
        listOf(
            Test(
                id = "QP001",
                title = "Data Structures - Mid Term",
                description = "Mid semester examination for Data Structures",
                subjectId = "CS201",
                testType = TestType.WRITTEN,
                totalMarks = 100,
                duration = 180,
                scheduledDate = LocalDate.now().plusDays(7),
                scheduledTime = "09:00",
                venue = "Hall A",
                createdBy = "Prof. Smith"
            ),
            Test(
                id = "QP002",
                title = "Algorithms - Final Exam",
                description = "Final examination for Algorithms course",
                subjectId = "CS202",
                testType = TestType.WRITTEN,
                totalMarks = 100,
                duration = 180,
                scheduledDate = LocalDate.now().plusDays(14),
                scheduledTime = "14:00",
                venue = "Hall B",
                createdBy = "Prof. Johnson"
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
                text = "Upcoming Question Papers",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(questionPapers) { test ->
            TestCard(test = test)
        }
    }
}

@Composable
fun InternalTestsView() {
    // Mock data for internal tests
    val internalTests = remember {
        listOf(
            Test(
                id = "IT001",
                title = "Database Quiz 1",
                description = "First quiz on database fundamentals",
                subjectId = "CS301",
                testType = TestType.INTERNAL,
                totalMarks = 25,
                duration = 30,
                scheduledDate = LocalDate.now().plusDays(3),
                scheduledTime = "11:00",
                venue = "Lab 1",
                createdBy = "Prof. Davis"
            ),
            Test(
                id = "IT002",
                title = "Software Engineering Assignment Test",
                description = "Test on software development lifecycle",
                subjectId = "CS302",
                testType = TestType.INTERNAL,
                totalMarks = 50,
                duration = 60,
                scheduledDate = LocalDate.now().plusDays(5),
                scheduledTime = "15:00",
                venue = "Room 201",
                createdBy = "Prof. Wilson"
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
                text = "Internal Tests",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(internalTests) { test ->
            TestCard(test = test)
        }
    }
}

@Composable
fun TestResultsView() {
    // Mock data for test results
    val testResults = remember {
        listOf(
            TestResult(
                id = "TR001",
                testId = "QP001",
                studentId = "S001",
                obtainedMarks = 85,
                totalMarks = 100,
                percentage = 85.0,
                grade = "A",
                remarks = "Excellent performance"
            ),
            TestResult(
                id = "TR002",
                testId = "IT001",
                studentId = "S001",
                obtainedMarks = 22,
                totalMarks = 25,
                percentage = 88.0,
                grade = "A+",
                remarks = "Outstanding"
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
                text = "Test Results",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(testResults) { result ->
            TestResultCard(result = result)
        }
    }
}

@Composable
fun TestCard(test: Test) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { /* Navigate to test details */ }
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
                        text = test.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = test.description ?: "No description",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                AssistChip(
                    onClick = { },
                    label = { Text(test.testType.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (test.testType) {
                            TestType.WRITTEN -> MaterialTheme.colorScheme.primaryContainer
                            TestType.INTERNAL -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.tertiaryContainer
                        }
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = "Date", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${test.scheduledDate} at ${test.scheduledTime}", style = MaterialTheme.typography.bodySmall)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Duration", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${test.duration} mins", style = MaterialTheme.typography.bodySmall)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                test.venue?.let { venue ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Venue", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(venue, style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                Text("Marks: ${test.totalMarks}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun TestResultCard(result: TestResult) {
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
                    text = "Test ID: ${result.testId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result.remarks ?: "No remarks",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${result.obtainedMarks}/${result.totalMarks} (${result.percentage}%)",
                    style = MaterialTheme.typography.bodySmall
                )
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
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
