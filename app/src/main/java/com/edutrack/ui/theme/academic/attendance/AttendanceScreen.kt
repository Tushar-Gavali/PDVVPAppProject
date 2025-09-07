package com.edutrack.ui.academic.attendance

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
import com.edutrack.data.model.AttendanceStatus
import com.edutrack.data.model.Student
import com.edutrack.data.model.Subject
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Management") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter options */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
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
                    text = { Text("General") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Subject-wise") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Day-wise") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (selectedTab) {
                0 -> GeneralAttendanceView(
                    selectedDate = selectedDate,
                    onDateChange = { selectedDate = it }
                )
                1 -> SubjectWiseAttendanceView(
                    selectedSubject = selectedSubject,
                    onSubjectChange = { selectedSubject = it }
                )
                2 -> DayWiseAttendanceView(
                    selectedDate = selectedDate,
                    onDateChange = { selectedDate = it }
                )
            }
        }
    }
}

@Composable
fun GeneralAttendanceView(
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    // Mock data - replace with actual data from repository
    val students = remember {
        listOf(
            Student(
                id = "1",
                name = "John Doe",
                rollNumber = "CS001",
                prn = "S001",
                email = "john@example.com",
                phone = "1234567890",
                dateOfBirth = LocalDate.of(2000, 1, 1),
                admissionDate = LocalDate.of(2022, 8, 1),
                course = "Computer Science",
                semester = 3,
                academicYear = "2023-24"
            ),
            Student(
                id = "2",
                name = "Jane Smith",
                rollNumber = "CS002",
                prn = "S002",
                email = "jane@example.com",
                phone = "1234567891",
                dateOfBirth = LocalDate.of(2000, 2, 1),
                admissionDate = LocalDate.of(2022, 8, 1),
                course = "Computer Science",
                semester = 3,
                academicYear = "2023-24"
            )
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Date Picker
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
                Icon(Icons.Default.DateRange, contentDescription = "Date")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Date: ${selectedDate.toString()}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* Open date picker */ }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Date")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Students List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(students) { student ->
                AttendanceStudentCard(
                    student = student,
                    attendanceStatus = AttendanceStatus.PRESENT, // Mock status
                    onStatusChange = { /* Handle status change */ }
                )
            }
        }
    }
}

@Composable
fun SubjectWiseAttendanceView(
    selectedSubject: Subject?,
    onSubjectChange: (Subject?) -> Unit
) {
    // Mock subjects
    val subjects = remember {
        listOf(
            Subject(
                id = "1",
                name = "Data Structures",
                code = "CS201",
                credits = 4,
                semester = 3,
                academicYear = "2023-24",
                teacherId = "T001"
            ),
            Subject(
                id = "2",
                name = "Algorithms",
                code = "CS202",
                credits = 4,
                semester = 3,
                academicYear = "2023-24",
                teacherId = "T002"
            )
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Subject Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Subject",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                subjects.forEach { subject ->
                    Card(
                        onClick = { onSubjectChange(subject) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedSubject?.id == subject.id) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(
                            text = "${subject.name} (${subject.code})",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Attendance for selected subject
        selectedSubject?.let { subject ->
            Text(
                text = "Attendance for ${subject.name}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            // Add attendance list here
        }
    }
}

@Composable
fun DayWiseAttendanceView(
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Day-wise Attendance for ${selectedDate.toString()}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Add day-wise attendance summary here
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Attendance Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Students: 50")
                    Text("Present: 45")
                    Text("Absent: 5")
                }
            }
        }
    }
}

@Composable
fun AttendanceStudentCard(
    student: Student,
    attendanceStatus: AttendanceStatus,
    onStatusChange: (AttendanceStatus) -> Unit
) {
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
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Roll: ${student.rollNumber}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Status buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AttendanceStatus.values().forEach { status ->
                    FilterChip(
                        onClick = { onStatusChange(status) },
                        label = { Text(status.name) },
                        selected = attendanceStatus == status
                    )
                }
            }
        }
    }
}
