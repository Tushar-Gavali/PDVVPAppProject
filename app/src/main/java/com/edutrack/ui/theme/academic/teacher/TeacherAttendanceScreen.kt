package com.edutrack.ui.academic.teacher

import android.os.Build
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edutrack.data.model.UiState
import com.edutrack.data.model.UserProfile
import com.edutrack.ui.viewmodel.AttendanceViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAttendanceScreen(
    teacherProfile: UserProfile,
    onBack: () -> Unit,
    viewModel: AttendanceViewModel = viewModel()
) {
    val studentsState by viewModel.studentsState.collectAsStateWithLifecycle()
    val attendanceSelections by viewModel.currentAttendanceSelections.collectAsStateWithLifecycle()
    val submitState by viewModel.attendanceSubmitState.collectAsStateWithLifecycle()
    
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // Load students on composition
    LaunchedEffect(Unit) {
        viewModel.loadStudentsAndPreviousAttendance(teacherProfile.userClass, teacherProfile.division, selectedDate)
    }

    LaunchedEffect(submitState) {
        if (submitState is UiState.Success) {
            Toast.makeText(context, "Attendance Submitted Successfully", Toast.LENGTH_SHORT).show()
            viewModel.resetSubmitState()
            onBack()
        } else if (submitState is UiState.Error) {
            Toast.makeText(context, (submitState as UiState.Error).message, Toast.LENGTH_SHORT).show()
            viewModel.resetSubmitState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (studentsState is UiState.Success) {
                FloatingActionButton(
                    onClick = {
                        viewModel.submitAttendance(
                            teacherId = teacherProfile.userId,
                            userClass = teacherProfile.userClass,
                            division = teacherProfile.division,
                            date = selectedDate
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Submit")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                    var showDatePicker by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Date: ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Change Date", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }

                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                        )
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        selectedDate = java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                                        // Reload data for new date
                                        viewModel.loadStudentsAndPreviousAttendance(teacherProfile.userClass, teacherProfile.division, selectedDate)
                                    }
                                    showDatePicker = false
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = studentsState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Empty -> { }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is UiState.Success -> {
                    val students = state.data
                    if (students.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No students found for your class and division.")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp) // padding for FAB
                        ) {
                            items(students) { student ->
                                val isPresent = attendanceSelections[student.userId] ?: true
                                AttendanceStudentItem(
                                    student = student,
                                    isPresent = isPresent,
                                    onStatusChange = { present ->
                                        viewModel.markAttendance(student.userId, present)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceStudentItem(
    student: UserProfile,
    isPresent: Boolean,
    onStatusChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.fillMaxSize())
            }

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "PRN: ${student.prn}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Present Button
                FilterChip(
                    selected = isPresent,
                    onClick = { onStatusChange(true) },
                    label = { Text("Present") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4CAF50),
                        selectedLabelColor = Color.White
                    )
                )

                // Absent Button
                FilterChip(
                    selected = !isPresent,
                    onClick = { onStatusChange(false) },
                    label = { Text("Absent") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFF44336),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}
