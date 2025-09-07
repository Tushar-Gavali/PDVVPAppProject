package com.edutrack.ui.academic.events

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
import com.edutrack.data.model.Event
import com.edutrack.data.model.EventAttendance
import com.edutrack.data.model.EventAttendanceStatus
import com.edutrack.data.model.EventType
import com.edutrack.data.model.Student
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventAttendanceScreen(onBack: () -> Unit) {
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Attendance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Export attendance */ }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export")
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
            if (selectedEvent == null) {
                EventSelectionView(onEventSelected = { selectedEvent = it })
            } else {
                EventAttendanceView(
                    event = selectedEvent!!,
                    onBack = { selectedEvent = null }
                )
            }
        }
    }
}

@Composable
fun EventSelectionView(onEventSelected: (Event) -> Unit) {
    // Mock data for events
    val events = remember {
        listOf(
            Event(
                id = "E001",
                title = "Annual Tech Fest 2024",
                description = "Technical event with competitions and workshops",
                eventType = EventType.CULTURAL,
                startDate = LocalDate.now().minusDays(2),
                endDate = LocalDate.now(),
                venue = "Main Auditorium",
                organizer = "CS Department",
                createdBy = "Admin"
            ),
            Event(
                id = "E002",
                title = "Machine Learning Workshop",
                description = "Hands-on ML workshop",
                eventType = EventType.WORKSHOP,
                startDate = LocalDate.now().minusDays(5),
                venue = "Lab 2",
                organizer = "AI Club",
                createdBy = "Prof. Anderson"
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Select Event for Attendance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(events) { event ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                onClick = { onEventSelected(event) }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${event.startDate}", style = MaterialTheme.typography.bodySmall)
                        Text(event.venue ?: "TBD", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun EventAttendanceView(
    event: Event,
    onBack: () -> Unit
) {
    // Mock data for event attendance
    val attendanceList = remember {
        listOf(
            EventAttendance(
                id = "EA001",
                eventId = event.id,
                studentId = "S001",
                attendanceStatus = EventAttendanceStatus.ATTENDED,
                registeredAt = LocalDateTime.now().minusDays(10),
                attendedAt = LocalDateTime.now().minusDays(2)
            ),
            EventAttendance(
                id = "EA002",
                eventId = event.id,
                studentId = "S002",
                attendanceStatus = EventAttendanceStatus.REGISTERED,
                registeredAt = LocalDateTime.now().minusDays(8)
            ),
            EventAttendance(
                id = "EA003",
                eventId = event.id,
                studentId = "S003",
                attendanceStatus = EventAttendanceStatus.ABSENT,
                registeredAt = LocalDateTime.now().minusDays(12)
            )
        )
    }

    val students = remember {
        listOf(
            Student(
                id = "S001",
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
                id = "S002",
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
            ),
            Student(
                id = "S003",
                name = "Bob Johnson",
                rollNumber = "CS003",
                prn = "S003",
                email = "bob@example.com",
                phone = "1234567892",
                dateOfBirth = LocalDate.of(2000, 3, 1),
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
        // Event header
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
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${event.startDate} at ${event.venue}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Attendance summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val presentCount = attendanceList.count { it.attendanceStatus == EventAttendanceStatus.ATTENDED }
                val registeredCount = attendanceList.count { it.attendanceStatus == EventAttendanceStatus.REGISTERED }
                val absentCount = attendanceList.count { it.attendanceStatus == EventAttendanceStatus.ABSENT }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = presentCount.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text("Present", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = registeredCount.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                    Text("Registered", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = absentCount.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF44336)
                    )
                    Text("Absent", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Attendance List",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Attendance list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(attendanceList) { attendance ->
                val student = students.find { it.id == attendance.studentId }
                student?.let {
                    EventAttendanceCard(
                        student = it,
                        attendance = attendance,
                        onStatusChange = { /* Update attendance status */ }
                    )
                }
            }
        }
    }
}

@Composable
fun EventAttendanceCard(
    student: Student,
    attendance: EventAttendance,
    onStatusChange: (EventAttendanceStatus) -> Unit
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
                attendance.attendedAt?.let {
                    Text(
                        text = "Attended: ${it.toLocalDate()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Status buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EventAttendanceStatus.values().forEach { status ->
                    FilterChip(
                        onClick = { onStatusChange(status) },
                        label = { Text(status.name) },
                        selected = attendance.attendanceStatus == status,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (status) {
                                EventAttendanceStatus.ATTENDED -> Color(0xFF4CAF50)
                                EventAttendanceStatus.REGISTERED -> Color(0xFF2196F3)
                                EventAttendanceStatus.ABSENT -> Color(0xFFF44336)
                                EventAttendanceStatus.CANCELLED -> Color(0xFF9E9E9E)
                            }
                        )
                    )
                }
            }
        }
    }
}
