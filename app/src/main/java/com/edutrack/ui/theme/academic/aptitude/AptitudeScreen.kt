package com.edutrack.ui.academic.aptitude

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.edutrack.data.model.DifficultyLevel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AptitudeScreen(onBack: () -> Unit) {
    var selectedCategory by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aptitude Tests & Training") },
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
            // Category Tabs
            TabRow(selectedTabIndex = selectedCategory) {
                Tab(
                    selected = selectedCategory == 0,
                    onClick = { selectedCategory = 0 },
                    text = { Text("Tests") }
                )
                Tab(
                    selected = selectedCategory == 1,
                    onClick = { selectedCategory = 1 },
                    text = { Text("Training") }
                )
                Tab(
                    selected = selectedCategory == 2,
                    onClick = { selectedCategory = 2 },
                    text = { Text("Progress") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (selectedCategory) {
                0 -> AptitudeTestsView()
                1 -> AptitudeTrainingView()
                2 -> AptitudeProgressView()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AptitudeTestsView() {
    // Mock aptitude tests
    val aptitudeTests = remember {
        listOf(
            Test(
                id = "1",
                title = "Quantitative Aptitude - Basic",
                description = "Basic mathematical aptitude questions",
                testType = TestType.APTITUDE,
                totalMarks = 50,
                duration = 60,
                scheduledDate = LocalDate.now().plusDays(1),
                scheduledTime = "10:00",
                createdBy = "T001"
            ),
            Test(
                id = "2",
                title = "Logical Reasoning",
                description = "Logical reasoning and analytical thinking",
                testType = TestType.APTITUDE,
                totalMarks = 40,
                duration = 45,
                scheduledDate = LocalDate.now().plusDays(3),
                scheduledTime = "14:00",
                createdBy = "T002"
            ),
            Test(
                id = "3",
                title = "Verbal Ability",
                description = "English language and communication skills",
                testType = TestType.APTITUDE,
                totalMarks = 60,
                duration = 75,
                scheduledDate = LocalDate.now().plusDays(5),
                scheduledTime = "09:00",
                createdBy = "T003"
            )
        )
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(aptitudeTests) { test ->
            AptitudeTestCard(
                test = test,
                onStartTest = { /* Start test */ },
                onViewDetails = { /* View details */ }
            )
        }
    }
}

@Composable
fun AptitudeTrainingView() {
    val trainingModules = remember {
        listOf(
            TrainingModule(
                id = "1",
                title = "Number System Basics",
                description = "Learn fundamental concepts of number systems",
                duration = "2 hours",
                difficulty = DifficultyLevel.EASY,
                isCompleted = false,
                progress = 0.3f
            ),
            TrainingModule(
                id = "2",
                title = "Percentage and Ratio",
                description = "Master percentage calculations and ratio problems",
                duration = "3 hours",
                difficulty = DifficultyLevel.MEDIUM,
                isCompleted = false,
                progress = 0.7f
            ),
            TrainingModule(
                id = "3",
                title = "Time and Work",
                description = "Solve complex time and work problems",
                duration = "4 hours",
                difficulty = DifficultyLevel.HARD,
                isCompleted = true,
                progress = 1.0f
            )
        )
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(trainingModules) { module ->
            TrainingModuleCard(
                module = module,
                onStartModule = { /* Start module */ },
                onResumeModule = { /* Resume module */ }
            )
        }
    }
}

@Composable
fun AptitudeProgressView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Overall Progress Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Overall Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProgressItem("Tests Taken", "12", "15")
                    ProgressItem("Training Completed", "8", "12")
                    ProgressItem("Average Score", "78%", "")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Recent Performance
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Recent Performance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Add performance chart or list here
                Text("Performance trends and analytics will be displayed here")
            }
        }
    }
}

@Composable
fun AptitudeTestCard(
    test: Test,
    onStartTest: () -> Unit,
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
                        text = test.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = test.description ?: "",
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
                Text("Duration: ${test.duration} min")
                Text("Marks: ${test.totalMarks}")
                Text("Date: ${test.scheduledDate}")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onStartTest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Start Test")
                }
            }
        }
    }
}

@Composable
fun TrainingModuleCard(
    module: TrainingModule,
    onStartModule: () -> Unit,
    onResumeModule: () -> Unit
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
                        text = module.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = module.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                AssistChip(
                    onClick = { },
                    label = { Text(module.difficulty.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (module.difficulty) {
                            DifficultyLevel.EASY -> MaterialTheme.colorScheme.primaryContainer
                            DifficultyLevel.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
                            DifficultyLevel.HARD -> MaterialTheme.colorScheme.errorContainer
                        }
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress Bar
            if (!module.isCompleted) {
                LinearProgressIndicator(
                    progress = module.progress,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Progress: ${(module.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Duration: ${module.duration}")
                
                Button(
                    onClick = if (module.isCompleted) onStartModule else onResumeModule,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (module.isCompleted) 
                            MaterialTheme.colorScheme.secondary 
                        else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (module.isCompleted) "Retake" else "Continue")
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

data class TrainingModule(
    val id: String,
    val title: String,
    val description: String,
    val duration: String,
    val difficulty: DifficultyLevel,
    val isCompleted: Boolean,
    val progress: Float
)
