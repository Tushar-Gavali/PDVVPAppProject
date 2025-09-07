package com.edutrack.ui.academic.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edutrack.data.model.AcademicHistory
import com.edutrack.data.model.AcademicStatus
import com.edutrack.data.model.Student
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(onBack: () -> Unit) {
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Profiles") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Add new student */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Student")
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
            if (selectedStudent == null) {
                StudentListView(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onStudentSelected = { selectedStudent = it }
                )
            } else {
                StudentDetailView(
                    student = selectedStudent!!,
                    onBack = { selectedStudent = null }
                )
            }
        }
    }
}

@Composable
fun StudentListView(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onStudentSelected: (Student) -> Unit
) {
    // Mock data for students
    val students = remember {
        listOf(
            Student(
                id = "S001",
                name = "John Doe",
                rollNumber = "CS001",
                prn = "S001",
                email = "john.doe@example.com",
                phone = "1234567890",
                dateOfBirth = LocalDate.of(2000, 1, 15),
                admissionDate = LocalDate.of(2022, 8, 1),
                course = "Computer Science",
                semester = 5,
                academicYear = "2023-24",
                mentorId = "M001",
                address = "123 Main St, City",
                parentName = "Robert Doe",
                parentPhone = "9876543210",
                emergencyContact = "9876543211",
                bloodGroup = "O+",
                medicalInfo = "No known allergies"
            ),
            Student(
                id = "S002",
                name = "Jane Smith",
                rollNumber = "CS002",
                prn = "S002",
                email = "jane.smith@example.com",
                phone = "1234567891",
                dateOfBirth = LocalDate.of(2000, 3, 22),
                admissionDate = LocalDate.of(2022, 8, 1),
                course = "Computer Science",
                semester = 5,
                academicYear = "2023-24",
                mentorId = "M002",
                address = "456 Oak Ave, City",
                parentName = "Michael Smith",
                parentPhone = "9876543212",
                emergencyContact = "9876543213",
                bloodGroup = "A+",
                medicalInfo = "Asthmatic"
            )
        )
    }

    val filteredStudents = students.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.rollNumber.contains(searchQuery, ignoreCase = true) ||
        it.email.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search students...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Students list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredStudents) { student ->
                StudentCard(
                    student = student,
                    onClick = { onStudentSelected(student) }
                )
            }
        }
    }
}

@Composable
fun StudentDetailView(
    student: Student,
    onBack: () -> Unit
) {
    // Mock academic history
    val academicHistory = remember {
        listOf(
            AcademicHistory(
                id = "AH001",
                studentId = student.id,
                semester = 1,
                academicYear = "2022-23",
                cgpa = 8.5,
                sgpa = 8.2,
                totalCredits = 24,
                earnedCredits = 24,
                subjects = listOf("Math", "Physics", "Programming"),
                grades = mapOf("Math" to "A", "Physics" to "B+", "Programming" to "A+"),
                attendance = 92.5,
                status = AcademicStatus.ACTIVE,
                remarks = "Excellent performance"
            ),
            AcademicHistory(
                id = "AH002",
                studentId = student.id,
                semester = 2,
                academicYear = "2022-23",
                cgpa = 8.7,
                sgpa = 8.9,
                totalCredits = 26,
                earnedCredits = 26,
                subjects = listOf("Data Structures", "Chemistry", "English"),
                grades = mapOf("Data Structures" to "A+", "Chemistry" to "A", "English" to "B+"),
                attendance = 95.0,
                status = AcademicStatus.ACTIVE,
                remarks = "Outstanding improvement"
            )
        )
    }

    // Calculate overall stats
    val overallCGPA = academicHistory.lastOrNull()?.cgpa ?: 0.0
    val overallAttendance = academicHistory.lastOrNull()?.attendance ?: 0.0
    val totalSubjects = academicHistory.flatMap { it.subjects }.distinct().size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header with back button
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Student Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            // Enhanced Profile Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile image with gradient background
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = CircleShape
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(70.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Roll: ${student.rollNumber}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "${student.course} - Semester ${student.semester}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quick Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatCard("CGPA", String.format("%.1f", overallCGPA), Icons.Default.Grade)
                        StatCard("Attendance", "${overallAttendance.toInt()}%", Icons.Default.Person)
                        StatCard("Subjects", "$totalSubjects", Icons.Default.Book)
                    }
                }
            }
        }

        item {
            // Personal Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Personal Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    InfoRow("Email", student.email)
                    InfoRow("Phone", student.phone)
                    InfoRow("Date of Birth", student.dateOfBirth.toString())
                    InfoRow("Admission Date", student.admissionDate.toString())
                    InfoRow("Blood Group", student.bloodGroup ?: "Not specified")
                    InfoRow("Address", student.address ?: "Not provided")
                }
            }
        }

        item {
            // Emergency Contact
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Emergency Contact",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    InfoRow("Parent/Guardian", student.parentName ?: "Not provided")
                    InfoRow("Parent Phone", student.parentPhone ?: "Not provided")
                    InfoRow("Emergency Contact", student.emergencyContact ?: "Not provided")
                    InfoRow("Medical Info", student.medicalInfo ?: "None")
                }
            }
        }

        item {
            // Academic History
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Academic History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    academicHistory.forEach { history ->
                        AcademicHistoryItem(history)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StudentCard(
    student: Student,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Roll: ${student.rollNumber}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${student.course} - Sem ${student.semester}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "View Details"
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AcademicHistoryItem(history: AcademicHistory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Semester ${history.semester}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = history.academicYear,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("CGPA: ${history.cgpa}", style = MaterialTheme.typography.bodySmall)
                Text("SGPA: ${history.sgpa}", style = MaterialTheme.typography.bodySmall)
                Text("Attendance: ${history.attendance}%", style = MaterialTheme.typography.bodySmall)
            }

            if (!history.remarks.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = history.remarks ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
