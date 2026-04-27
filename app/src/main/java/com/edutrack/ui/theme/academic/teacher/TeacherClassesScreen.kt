/*package com.edutrack.ui.academic.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherClassesScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Classes") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "My Classes",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "My Classes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "View and manage all your scheduled classes in one place.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Coming Soon",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This feature is under development",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
*/
package com.edutrack.ui.academic.teacher
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class StudentCountSummary(
    val totalStudents: Int,
    val atktStudents: Int,
    val failStudents: Int,
    val passStudents: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherClassesScreen(
    onBack: () -> Unit
) {

    val classOptions = listOf("FE", "SE", "TE", "BE")
    val branchOptions = listOf("IT", "ETC", "Mech", "Civil", "CSD", "CE")
    val divisionOptions = listOf("A", "B", "C", "D")

    var selectedClass by remember { mutableStateOf("") }
    var selectedBranch by remember { mutableStateOf("") }
    var selectedDivision by remember { mutableStateOf("") }

    val summary = remember(selectedClass, selectedBranch, selectedDivision) {
        if (
            selectedClass.isNotBlank() &&
            selectedBranch.isNotBlank() &&
            selectedDivision.isNotBlank()
        ) {
            getStudentCountSummary(selectedClass, selectedBranch, selectedDivision)
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Classes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                "Select Class Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            DropdownField("Class", classOptions, selectedClass) { selectedClass = it }
            DropdownField("Branch", branchOptions, selectedBranch) { selectedBranch = it }
            DropdownField("Division", divisionOptions, selectedDivision) { selectedDivision = it }

            if (
                selectedClass.isNotBlank() &&
                selectedBranch.isNotBlank() &&
                selectedDivision.isNotBlank()
            ) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Selected Class", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("$selectedClass - $selectedBranch - $selectedDivision")
                    }
                }

                Text("Student Summary", fontWeight = FontWeight.Bold)

                summary?.let {
                    SummaryCard("Total Students", it.totalStudents, Icons.Default.Groups)
                    SummaryCard("ATKT Students", it.atktStudents, Icons.Default.Warning)
                    SummaryCard("Fail Students", it.failStudents, Icons.Default.Cancel)
                    SummaryCard("Pass Students", it.passStudents, Icons.Default.CheckCircle)
                }

            } else {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.School, contentDescription = null)
                        Spacer(Modifier.height(10.dp))
                        Text("Select all fields to view data", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))

        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (selected.isEmpty()) "Select $label" else selected)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelect(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, count: Int, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title)
            Spacer(Modifier.width(16.dp))

            Column {
                Text(title)
                Text(count.toString(), fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun getStudentCountSummary(c: String, b: String, d: String): StudentCountSummary {
    return when ("$c-$b-$d") {
        "FE-IT-A" -> StudentCountSummary(60, 8, 5, 47)
        "SE-IT-B" -> StudentCountSummary(62, 5, 4, 53)
        else -> StudentCountSummary(50, 5, 3, 42)
    }
}