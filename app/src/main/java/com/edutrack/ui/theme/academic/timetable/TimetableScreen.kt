package com.edutrack.ui.academic.timetable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edutrack.data.model.Subject
import com.edutrack.data.model.Teacher
import com.edutrack.data.model.TimetableSlot
import com.edutrack.data.model.BreakType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(onBack: () -> Unit) {
    var selectedDay by remember { mutableStateOf(DayOfWeek.MONDAY) }
    var selectedView by remember { mutableStateOf(1) } // 0: Daily, 1: Weekly (Default to Weekly)
    var showEditDialog by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weekly TimeTable") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showUploadDialog = true }) {
                        Icon(Icons.Default.Upload, contentDescription = "Upload TimeTable")
                    }
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit TimeTable")
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
            // Enhanced View selector with better labels
            TabRow(selectedTabIndex = selectedView) {
                Tab(
                    selected = selectedView == 0,
                    onClick = { selectedView = 0 },
                    text = { Text("Daily View") },
                    icon = { Icon(Icons.Default.Today, contentDescription = "Daily") }
                )
                Tab(
                    selected = selectedView == 1,
                    onClick = { selectedView = 1 },
                    text = { Text("Weekly TimeTable") },
                    icon = { Icon(Icons.Default.CalendarViewWeek, contentDescription = "Weekly") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedView) {
                0 -> DailyTimetableView(
                    selectedDay = selectedDay,
                    onDayChanged = { selectedDay = it }
                )
                1 -> WeeklyTimetableView()
            }
        }
    }

    // Edit Dialog
    if (showEditDialog) {
        EditTimetableDialog(
            onDismiss = { showEditDialog = false },
            onSave = { /* Handle save */ showEditDialog = false }
        )
    }

    // Upload Dialog
    if (showUploadDialog) {
        UploadTimetableDialog(
            onDismiss = { showUploadDialog = false },
            onUpload = { /* Handle upload */ showUploadDialog = false }
        )
    }
}

@Composable
fun DailyTimetableView(
    selectedDay: DayOfWeek,
    onDayChanged: (DayOfWeek) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Day selector
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DayOfWeek.values().toList()) { day ->
                FilterChip(
                    onClick = { onDayChanged(day) },
                    label = { Text(day.getDisplayName(TextStyle.SHORT, Locale.getDefault())) },
                    selected = selectedDay == day
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = selectedDay.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Timetable for selected day
        DaySchedule(day = selectedDay)
    }
}

@Composable
fun WeeklyTimetableView() {
    var showUploadDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Weekly TimeTable Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarViewWeek,
                        contentDescription = "Weekly TimeTable",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Weekly TimeTable",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "View your complete weekly schedule",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Button(
                        onClick = { showUploadDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Icon(
                            Icons.Default.Upload,
                            contentDescription = "Upload",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upload")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weekly Schedule
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(DayOfWeek.values().toList()) { day ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Day",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = day.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        DaySchedule(day = day, isCompact = true)
                    }
                }
            }
        }

        // Upload Dialog for Weekly View
        if (showUploadDialog) {
            UploadTimetableDialog(
                onDismiss = { showUploadDialog = false },
                onUpload = { /* Handle upload */ showUploadDialog = false }
            )
        }
    }
}

@Composable
fun DaySchedule(
    day: DayOfWeek,
    isCompact: Boolean = false
) {
    // Mock timetable data
    val schedule = remember(day) {
        when (day) {
            DayOfWeek.MONDAY -> listOf(
                TimetableSlot(
                    id = "TS001",
                    dayOfWeek = day,
                    startTime = LocalTime.of(9, 0),
                    endTime = LocalTime.of(10, 0),
                    subject = Subject(
                        id = "CS201",
                        name = "Data Structures",
                        code = "CS201",
                        credits = 4,
                        semester = 3,
                        academicYear = "2023-24",
                        teacherId = "T001"
                    ),
                    teacher = Teacher(
                        id = "T001",
                        name = "Dr. Smith",
                        email = "smith@edu.com",
                        phone = "1234567890",
                        employeeId = "EMP001",
                        department = "Computer Science",
                        designation = "Professor",
                        qualification = "Ph.D. in Computer Science",
                        experience = 15,
                        specialization = listOf("Data Structures", "Algorithms"),
                        subjects = listOf("Data Structures", "Algorithms"),
                        dateOfBirth = LocalDate.of(1975, 6, 15),
                        joiningDate = LocalDate.of(2010, 8, 1)
                    ),
                    venue = "Room 101"
                ),
                TimetableSlot(
                    id = "TS002",
                    dayOfWeek = day,
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(10, 15),
                    subject = null,
                    teacher = null,
                    venue = null,
                    isBreak = true,
                    breakType = BreakType.SHORT_BREAK
                ),
                TimetableSlot(
                    id = "TS003",
                    dayOfWeek = day,
                    startTime = LocalTime.of(10, 15),
                    endTime = LocalTime.of(11, 15),
                    subject = Subject(
                        id = "CS202",
                        name = "Algorithms",
                        code = "CS202",
                        credits = 4,
                        semester = 3,
                        academicYear = "2023-24",
                        teacherId = "T002"
                    ),
                    teacher = Teacher(
                        id = "T002",
                        name = "Prof. Johnson",
                        email = "johnson@edu.com",
                        phone = "1234567891",
                        employeeId = "EMP002",
                        department = "Computer Science",
                        designation = "Associate Professor",
                        qualification = "Ph.D. in Software Engineering",
                        experience = 12,
                        specialization = listOf("Software Engineering", "Database Systems"),
                        subjects = listOf("Software Engineering", "Database Systems"),
                        dateOfBirth = LocalDate.of(1980, 3, 20),
                        joiningDate = LocalDate.of(2012, 1, 15)
                    ),
                    venue = "Room 102"
                )
            )
            DayOfWeek.TUESDAY -> listOf(
                TimetableSlot(
                    id = "TS004",
                    dayOfWeek = day,
                    startTime = LocalTime.of(9, 0),
                    endTime = LocalTime.of(10, 0),
                    subject = Subject(
                        id = "CS301",
                        name = "Database Systems",
                        code = "CS301",
                        credits = 3,
                        semester = 3,
                        academicYear = "2023-24",
                        teacherId = "T003"
                    ),
                    teacher = Teacher(
                        id = "T003",
                        name = "Dr. Davis",
                        email = "davis@edu.com",
                        phone = "1234567892",
                        employeeId = "EMP003",
                        department = "Computer Science",
                        designation = "Professor",
                        qualification = "Ph.D. in Machine Learning",
                        experience = 18,
                        specialization = listOf("Machine Learning", "Artificial Intelligence"),
                        subjects = listOf("Machine Learning", "AI"),
                        dateOfBirth = LocalDate.of(1970, 8, 10),
                        joiningDate = LocalDate.of(2008, 9, 1)
                    ),
                    venue = "Lab 1"
                )
            )
            else -> emptyList()
        }
    }

    if (schedule.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No classes scheduled",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(schedule) { slot ->
                TimetableSlotCard(slot = slot, isCompact = isCompact)
            }
        }
    }
}

@Composable
fun TimetableSlotCard(
    slot: TimetableSlot,
    isCompact: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (slot.isBreak) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 8.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = slot.startTime.toString(),
                    style = if (isCompact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "to",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = slot.endTime.toString(),
                    style = if (isCompact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content column
            Column(modifier = Modifier.weight(1f)) {
                if (slot.isBreak) {
                    Text(
                        text = when (slot.breakType) {
                            BreakType.SHORT_BREAK -> "Short Break"
                            BreakType.LUNCH -> "Lunch Break"
                            BreakType.LONG_BREAK -> "Long Break"
                            BreakType.PRAYER_TIME -> "Prayer Time"
                            null -> "Break"
                        },
                        style = if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    slot.subject?.let { subject ->
                        Text(
                            text = subject.name,
                            style = if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subject.code,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    if (!isCompact) {
                        slot.teacher?.let { teacher ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = teacher.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        slot.venue?.let { venue ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Venue",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = venue,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (!slot.isBreak && !isCompact) {
                // Subject indicator
                Card(
                    modifier = Modifier.size(40.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = slot.subject?.code?.take(2) ?: "??",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTimetableDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var selectedDay by remember { mutableStateOf(DayOfWeek.MONDAY) }
    var selectedSubject by remember { mutableStateOf("") }
    var selectedTeacher by remember { mutableStateOf("") }
    var selectedVenue by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit TimeTable")
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Day Selection
                OutlinedTextField(
                    value = selectedDay.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Day") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Subject Selection
                OutlinedTextField(
                    value = selectedSubject,
                    onValueChange = { selectedSubject = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Teacher Selection
                OutlinedTextField(
                    value = selectedTeacher,
                    onValueChange = { selectedTeacher = it },
                    label = { Text("Teacher") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Venue Selection
                OutlinedTextField(
                    value = selectedVenue,
                    onValueChange = { selectedVenue = it },
                    label = { Text("Venue") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Time Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        placeholder = { Text("HH:MM") },
                        keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        placeholder = { Text("HH:MM") },
                        keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = selectedSubject.isNotEmpty() && selectedTeacher.isNotEmpty()
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadTimetableDialog(
    onDismiss: () -> Unit,
    onUpload: () -> Unit
) {
    var selectedFileType by remember { mutableStateOf("PDF") }
    var fileName by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Upload,
                    contentDescription = "Upload",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Upload TimeTable")
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // File Type Selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Supported File Types",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("PDF", "Excel", "Image").forEach { type ->
                                FilterChip(
                                    onClick = { selectedFileType = type },
                                    label = { Text(type) },
                                    selected = selectedFileType == type,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // File Selection
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name") },
                    placeholder = { Text("Select timetable file...") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { /* File picker logic */ }) {
                            Icon(Icons.Default.FolderOpen, contentDescription = "Browse Files")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Upload Instructions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Info",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Upload Instructions",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Ensure the timetable is clear and readable\n• PDF files are recommended\n• Maximum file size: 10MB\n• Supported formats: PDF, XLSX, JPG, PNG",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Upload Progress (if uploading)
                if (isUploading) {
                    Column {
                        Text(
                            text = "Uploading... ${(uploadProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = uploadProgress,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isUploading = true
                    // Simulate upload progress
                    // In real implementation, this would handle actual file upload
                    onUpload()
                },
                enabled = fileName.isNotEmpty() && !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Upload File")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
