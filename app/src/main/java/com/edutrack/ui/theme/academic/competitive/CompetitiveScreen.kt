package com.edutrack.ui.academic.competitive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edutrack.data.model.Test
import com.edutrack.data.model.TestType
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitiveScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Competitive Exam Preparation") },
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
            // Tab Row
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Exams") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Preparation") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Progress") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (selectedTab) {
                0 -> CompetitiveExamsView()
                1 -> PreparationMaterialsView()
                2 -> CompetitiveProgressView()
            }
        }
    }
}

@Composable
fun CompetitiveExamsView() {
    val competitiveExams = remember {
        listOf(
            CompetitiveExam(
                id = "1",
                name = "JEE Main",
                description = "Joint Entrance Examination - Main",
                examDate = LocalDate.of(2024, 4, 15),
                registrationDeadline = LocalDate.of(2024, 3, 15),
                isRegistered = true,
                preparationStatus = PreparationStatus.IN_PROGRESS
            ),
            CompetitiveExam(
                id = "2",
                name = "GATE",
                description = "Graduate Aptitude Test in Engineering",
                examDate = LocalDate.of(2024, 2, 10),
                registrationDeadline = LocalDate.of(2024, 1, 10),
                isRegistered = false,
                preparationStatus = PreparationStatus.NOT_STARTED
            ),
            CompetitiveExam(
                id = "3",
                name = "CAT",
                description = "Common Admission Test",
                examDate = LocalDate.of(2024, 11, 24),
                registrationDeadline = LocalDate.of(2024, 9, 15),
                isRegistered = true,
                preparationStatus = PreparationStatus.COMPLETED
            )
        )
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(competitiveExams) { exam ->
            CompetitiveExamCard(
                exam = exam,
                onRegister = { /* Handle registration */ },
                onViewDetails = { /* View exam details */ }
            )
        }
    }
}

@Composable
fun PreparationMaterialsView() {
    val preparationMaterials = remember {
        listOf(
            PreparationMaterial(
                id = "1",
                title = "JEE Main Previous Year Papers",
                type = MaterialType.PREVIOUS_PAPERS,
                subject = "Mathematics",
                difficulty = "Advanced",
                isCompleted = false
            ),
            PreparationMaterial(
                id = "2",
                title = "GATE Mock Tests",
                type = MaterialType.MOCK_TESTS,
                subject = "Computer Science",
                difficulty = "Expert",
                isCompleted = true
            ),
            PreparationMaterial(
                id = "3",
                title = "CAT Quantitative Aptitude",
                type = MaterialType.STUDY_MATERIAL,
                subject = "Quantitative Aptitude",
                difficulty = "Intermediate",
                isCompleted = false
            )
        )
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(preparationMaterials) { material ->
            PreparationMaterialCard(
                material = material,
                onStart = { /* Start material */ },
                onViewDetails = { /* View details */ }
            )
        }
    }
}

@Composable
fun CompetitiveProgressView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Overall Progress
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Competitive Exam Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProgressItem("Exams Registered", "2", "3")
                    ProgressItem("Mock Tests", "15", "20")
                    ProgressItem("Study Hours", "120", "200")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Performance Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Performance Trends",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Add performance chart here
                Text("Performance analytics and trends will be displayed here")
            }
        }
    }
}

@Composable
fun CompetitiveExamCard(
    exam: CompetitiveExam,
    onRegister: () -> Unit,
    onViewDetails: () -> Unit
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
                        text = exam.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = exam.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = onViewDetails) {
                    Icon(Icons.Default.Info, contentDescription = "Details")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Exam Date: ${exam.examDate}")
                Text("Registration: ${exam.registrationDeadline}")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text(exam.preparationStatus.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (exam.preparationStatus) {
                            PreparationStatus.NOT_STARTED -> MaterialTheme.colorScheme.errorContainer
                            PreparationStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer
                            PreparationStatus.COMPLETED -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
                )
                
                if (!exam.isRegistered) {
                    Button(
                        onClick = onRegister,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Register")
                    }
                } else {
                    Text(
                        text = "Registered",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PreparationMaterialCard(
    material: PreparationMaterial,
    onStart: () -> Unit,
    onViewDetails: () -> Unit
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
                        text = material.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Subject: ${material.subject}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = onViewDetails) {
                    Icon(Icons.Default.Info, contentDescription = "Details")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = { },
                        label = { Text(material.type.name) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    AssistChip(
                        onClick = { },
                        label = { Text(material.difficulty) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
                
                Button(
                    onClick = onStart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (material.isCompleted) 
                            MaterialTheme.colorScheme.secondary 
                        else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (material.isCompleted) "Retake" else "Start")
                }
            }
        }
    }
}

@Composable
fun ProgressItem(label: String, value: String, total: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        if (total.isNotEmpty()) {
            Text(
                text = "of $total",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class CompetitiveExam(
    val id: String,
    val name: String,
    val description: String,
    val examDate: LocalDate,
    val registrationDeadline: LocalDate,
    val isRegistered: Boolean,
    val preparationStatus: PreparationStatus
)

data class PreparationMaterial(
    val id: String,
    val title: String,
    val type: MaterialType,
    val subject: String,
    val difficulty: String,
    val isCompleted: Boolean
)

enum class PreparationStatus {
    NOT_STARTED, IN_PROGRESS, COMPLETED
}

enum class MaterialType {
    PREVIOUS_PAPERS, MOCK_TESTS, STUDY_MATERIAL, VIDEO_LECTURES, PRACTICE_QUESTIONS
}
