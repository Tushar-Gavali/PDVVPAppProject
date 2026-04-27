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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edutrack.data.model.UiState
import com.edutrack.data.model.UserProfile
import com.edutrack.ui.viewmodel.CompetitiveViewModel
import com.edutrack.ui.viewmodel.ProgressStats
import com.edutrack.ui.viewmodel.UiCompetitiveExam
import com.edutrack.ui.viewmodel.UiPreparationMaterial

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitiveScreen(
    studentProfile: UserProfile,
    onBack: () -> Unit,
    viewModel: CompetitiveViewModel = viewModel()
) {
    val examsState by viewModel.examsState.collectAsStateWithLifecycle()
    val materialsState by viewModel.materialsState.collectAsStateWithLifecycle()
    val progressStats by viewModel.progressStats.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    
    LaunchedEffect(studentProfile.userId) {
        viewModel.setStudentId(studentProfile.userId)
    }
    
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
                0 -> CompetitiveExamsView(examsState, onRegister = { viewModel.registerForExam(it) })
                1 -> PreparationMaterialsView(materialsState, onStart = { viewModel.startOrRetakeMaterial(it) })
                2 -> CompetitiveProgressView(progressStats)
            }
        }
    }
}

@Composable
fun CompetitiveExamsView(
    examsState: UiState<List<UiCompetitiveExam>>,
    onRegister: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (examsState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Empty -> Text("No competitive exams found.", modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> Text(examsState.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(examsState.data, key = { it.id }) { exam ->
                        CompetitiveExamCard(
                            exam = exam,
                            onRegister = { onRegister(exam.id) },
                            onViewDetails = { /* Handle details */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreparationMaterialsView(
    materialsState: UiState<List<UiPreparationMaterial>>,
    onStart: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (materialsState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Empty -> Text("No preparation materials found.", modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> Text(materialsState.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(materialsState.data, key = { it.id }) { material ->
                        PreparationMaterialCard(
                            material = material,
                            onStart = { onStart(material.id) },
                            onViewDetails = { /* View details */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompetitiveProgressView(
    progress: ProgressStats
) {
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
                    ProgressItem("Exams Registered", progress.examsRegistered.toString(), progress.totalExams.toString())
                    ProgressItem("Materials", progress.materialsCompleted.toString(), progress.totalMaterials.toString())
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Performance Chart Mock
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
                Text("Keep registering and studying! Analytics will be dynamically graphed here.")
            }
        }
    }
}

@Composable
fun CompetitiveExamCard(
    exam: UiCompetitiveExam,
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
                    label = { Text(exam.preparationStatus) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (exam.preparationStatus) {
                            "NOT_STARTED" -> MaterialTheme.colorScheme.errorContainer
                            "IN_PROGRESS" -> MaterialTheme.colorScheme.primaryContainer
                            "COMPLETED" -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surface
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
    material: UiPreparationMaterial,
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
                        label = { Text(material.type) },
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

