package com.edutrack.ui.academic.mentorship

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
import com.edutrack.data.model.Mentor
import com.edutrack.data.model.Mentorship
import com.edutrack.data.model.MentorshipStatus
import com.edutrack.data.model.Student
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorListScreen(onBack: () -> Unit) {
    var selectedMentor by remember { mutableStateOf<Mentor?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mentor List") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Add new mentor */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Mentor")
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
            if (selectedMentor == null) {
                MentorListView(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onMentorSelected = { selectedMentor = it }
                )
            } else {
                MentorDetailView(
                    mentor = selectedMentor!!,
                    onBack = { selectedMentor = null }
                )
            }
        }
    }
}

@Composable
fun MentorListView(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onMentorSelected: (Mentor) -> Unit
) {
    // Mock data for mentors
    val mentors = remember {
        listOf(
            Mentor(
                id = "M001",
                name = "Dr. Sarah Johnson",
                email = "sarah.johnson@edu.com",
                phone = "1234567890",
                department = "Computer Science",
                designation = "Professor",
                employeeId = "EMP001",
                maxMentees = 15,
                currentMentees = 8,
                specialization = listOf("Machine Learning", "Data Science", "AI"),
                experience = 12
            ),
            Mentor(
                id = "M002",
                name = "Prof. Michael Chen",
                email = "michael.chen@edu.com",
                phone = "1234567891",
                department = "Computer Science",
                designation = "Associate Professor",
                employeeId = "EMP002",
                maxMentees = 12,
                currentMentees = 6,
                specialization = listOf("Software Engineering", "Web Development"),
                experience = 8
            ),
            Mentor(
                id = "M003",
                name = "Dr. Emily Rodriguez",
                email = "emily.rodriguez@edu.com",
                phone = "1234567892",
                department = "Computer Science",
                designation = "Assistant Professor",
                employeeId = "EMP003",
                maxMentees = 10,
                currentMentees = 4,
                specialization = listOf("Cybersecurity", "Network Security"),
                experience = 5
            )
        )
    }

    val filteredMentors = mentors.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.department.contains(searchQuery, ignoreCase = true) ||
        it.specialization?.any { spec -> spec.contains(searchQuery, ignoreCase = true) } == true
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
            label = { Text("Search mentors...") },
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

        // Mentors list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredMentors) { mentor ->
                MentorCard(
                    mentor = mentor,
                    onClick = { onMentorSelected(mentor) }
                )
            }
        }
    }
}

@Composable
fun MentorDetailView(
    mentor: Mentor,
    onBack: () -> Unit
) {
    // Mock data for mentees
    val mentees = remember {
        listOf(
            Student(
                id = "S001",
                name = "John Doe",
                rollNumber = "CS001",
                prn = "S001",
                email = "john@example.com",
                phone = "1234567890",
                dateOfBirth = java.time.LocalDate.of(2000, 1, 1),
                admissionDate = java.time.LocalDate.of(2022, 8, 1),
                course = "Computer Science",
                semester = 3,
                academicYear = "2023-24",
                mentorId = mentor.id
            ),
            Student(
                id = "S002",
                name = "Jane Smith",
                rollNumber = "CS002",
                prn = "S002",
                email = "jane@example.com",
                phone = "1234567891",
                dateOfBirth = java.time.LocalDate.of(2000, 2, 1),
                admissionDate = java.time.LocalDate.of(2022, 8, 1),
                course = "Computer Science",
                semester = 3,
                academicYear = "2023-24",
                mentorId = mentor.id
            )
        )
    }

    val mentorshipData = remember {
        listOf(
            Mentorship(
                id = "MS001",
                mentorId = mentor.id,
                menteeId = "S001",
                assignedDate = LocalDateTime.now().minusDays(30),
                status = MentorshipStatus.ACTIVE,
                notes = "Regular meetings scheduled",
                lastMeetingDate = LocalDateTime.now().minusDays(7),
                nextMeetingDate = LocalDateTime.now().plusDays(7)
            ),
            Mentorship(
                id = "MS002",
                mentorId = mentor.id,
                menteeId = "S002",
                assignedDate = LocalDateTime.now().minusDays(20),
                status = MentorshipStatus.ACTIVE,
                notes = "Focus on career guidance",
                lastMeetingDate = LocalDateTime.now().minusDays(3),
                nextMeetingDate = LocalDateTime.now().plusDays(4)
            )
        )
    }

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
                    text = "Mentor Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            // Mentor profile card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = mentor.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = mentor.designation,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = mentor.department,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Experience",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${mentor.experience} years",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                text = "Current Mentees",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${mentor.currentMentees}/${mentor.maxMentees}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Specializations
                    Text(
                        text = "Specializations:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    mentor.specialization?.forEach { spec ->
                        AssistChip(
                            onClick = { },
                            label = { Text(spec) },
                            modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                        )
                    }
                }
            }
        }

        item {
            // Mentees section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Assigned Mentees (${mentees.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    mentees.forEach { mentee ->
                        MenteeCard(mentee = mentee)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        item {
            // Recent meetings
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Recent Mentorship Activities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    mentorshipData.forEach { mentorship ->
                        MentorshipCard(mentorship = mentorship)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MentorCard(
    mentor: Mentor,
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
                modifier = Modifier.size(50.dp),
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
                        contentDescription = "Mentor",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mentor.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = mentor.designation,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = mentor.department,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Mentees: ${mentor.currentMentees}/${mentor.maxMentees}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
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
fun MenteeCard(mentee: Student) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Mentee",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mentee.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Roll: ${mentee.rollNumber}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Semester ${mentee.semester}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MentorshipCard(mentorship: Mentorship) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (mentorship.status) {
                MentorshipStatus.ACTIVE -> Color(0xFFE8F5E8)
                MentorshipStatus.INACTIVE -> Color(0xFFFFF3E0)
                MentorshipStatus.COMPLETED -> Color(0xFFE3F2FD)
                MentorshipStatus.SUSPENDED -> Color(0xFFFFEBEE)
            }
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
                    text = "Mentee ID: ${mentorship.menteeId}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                AssistChip(
                    onClick = { },
                    label = { Text(mentorship.status.name) }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Last Meeting: ${mentorship.lastMeetingDate?.toLocalDate() ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Next: ${mentorship.nextMeetingDate?.toLocalDate() ?: "TBD"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            mentorship.notes?.let { notes ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes: $notes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
