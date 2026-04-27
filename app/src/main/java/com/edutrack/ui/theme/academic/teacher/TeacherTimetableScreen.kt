package com.edutrack.ui.academic.teacher

import android.app.TimePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edutrack.data.model.TimetableEntry
import com.edutrack.data.model.UiState
import com.edutrack.data.model.UserProfile
import com.edutrack.ui.viewmodel.TimetableViewModel
import java.util.*

/** Reactive lecture slot used only in the local edit state. */
class LectureSlot(time: String, subject: String) {
    var time by mutableStateOf(time)
    var subject by mutableStateOf(subject)
}

/** One day's schedule in local edit state. */
class DaySchedule(val day: String, val slots: SnapshotStateList<LectureSlot>)

private val DEFAULT_DAYS = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
private val DEFAULT_SLOTS = listOf(
    "09:00 - 10:00", "10:15 - 11:15", "11:30 - 12:30",
    "01:00 - 02:00", "02:15 - 04:15"
)

/**
 * TeacherTimetableScreen
 *
 * - Loads the live timetable from Firestore for the teacher's class+division
 * - Allows editing slots locally
 * - "Save to Firestore" persists the full week via TimetableViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherTimetableScreen(
    teacherProfile: UserProfile,
    onBack: () -> Unit,
    viewModel: TimetableViewModel = viewModel()
) {
    val timetableState by viewModel.timetable.collectAsStateWithLifecycle()
    val operationResult by viewModel.operationResult.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Local editable timetable state — initialised from Firestore or defaults
    val timetable = remember {
        mutableStateListOf<DaySchedule>().apply {
            DEFAULT_DAYS.forEach { day ->
                add(
                    DaySchedule(
                        day = day,
                        slots = mutableStateListOf<LectureSlot>().apply {
                            DEFAULT_SLOTS.forEach { time -> add(LectureSlot(time, "")) }
                        }
                    )
                )
            }
        }
    }

    // Populate local timetable from Firestore data when it arrives
    var firestoreLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(timetableState) {
        if (!firestoreLoaded && timetableState is UiState.Success) {
            val entries = (timetableState as UiState.Success<List<TimetableEntry>>).data
            entries.forEach { entry ->
                val dayIndex = timetable.indexOfFirst { it.day == entry.day }
                if (dayIndex >= 0 && entry.slots.isNotEmpty()) {
                    timetable[dayIndex].slots.clear()
                    entry.slots.forEach { slotMap ->
                        timetable[dayIndex].slots.add(
                            LectureSlot(
                                time = slotMap["time"] ?: "",
                                subject = slotMap["subject"] ?: ""
                            )
                        )
                    }
                }
            }
            firestoreLoaded = true
        }
    }

    // Load from Firestore
    LaunchedEffect(teacherProfile.userClass, teacherProfile.division) {
        viewModel.loadTimetable(teacherProfile.userClass, teacherProfile.division)
    }

    LaunchedEffect(operationResult) {
        operationResult?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearOperationResult()
        }
    }

    // Edit dialog state
    var editingDayIndex by remember { mutableStateOf<Int?>(null) }
    var editingSlotIndex by remember { mutableStateOf<Int?>(null) }
    var editSubject by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timetable — ${teacherProfile.userClass} Div ${teacherProfile.division}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    // Build TimetableEntry list and save
                    val entries = timetable.map { daySchedule ->
                        TimetableEntry(
                            userClass = teacherProfile.userClass,
                            division = teacherProfile.division,
                            day = daySchedule.day,
                            slots = daySchedule.slots.map { slot ->
                                mapOf("time" to slot.time, "subject" to slot.subject)
                            },
                            updatedBy = teacherProfile.userId,
                            updatedAt = System.currentTimeMillis()
                        )
                    }
                    viewModel.saveFullWeekTimetable(entries)
                },
                icon = { Icon(Icons.Default.Save, contentDescription = "Save") },
                text = { Text("Save to Firestore") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        if (timetableState is UiState.Loading && !firestoreLoaded) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                items(timetable.size) { dayIndex ->
                    val daySchedule = timetable[dayIndex]
                    Spacer(Modifier.height(12.dp))
                    Text(
                        daySchedule.day,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant,
                                shape = MaterialTheme.shapes.medium)
                    ) {
                        daySchedule.slots.forEachIndexed { slotIndex, slot ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(slot.time, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        slot.subject.ifBlank { "— Not set" },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (slot.subject.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(onClick = {
                                    editingDayIndex = dayIndex
                                    editingSlotIndex = slotIndex
                                    editSubject = slot.subject
                                    val parts = slot.time.split(" - ")
                                    startTime = if (parts.size == 2) parts[0] else ""
                                    endTime = if (parts.size == 2) parts[1] else ""
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    // ── Edit Slot Dialog ──────────────────────────────────────────────────────
    if (editingDayIndex != null && editingSlotIndex != null) {
        AlertDialog(
            onDismissRequest = {
                editingDayIndex = null; editingSlotIndex = null; errorMessage = ""
            },
            title = {
                Text("Edit — ${timetable[editingDayIndex!!].day} Slot ${editingSlotIndex!! + 1}")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, h, m -> startTime = String.format("%02d:%02d", h, m) },
                                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (startTime.isNotEmpty()) "Start: $startTime" else "Select Start Time")
                    }
                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, h, m -> endTime = String.format("%02d:%02d", h, m) },
                                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (endTime.isNotEmpty()) "End: $endTime" else "Select End Time")
                    }
                    OutlinedTextField(
                        value = editSubject,
                        onValueChange = { editSubject = it },
                        label = { Text("Subject") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (startTime.isBlank() || endTime.isBlank()) {
                        errorMessage = "Select both start and end time"; return@Button
                    }
                    val (sh, sm) = startTime.split(":").map { it.toInt() }
                    val (eh, em) = endTime.split(":").map { it.toInt() }
                    if (eh < sh || (eh == sh && em <= sm)) {
                        errorMessage = "End time must be after start time"; return@Button
                    }
                    val slot = timetable[editingDayIndex!!].slots[editingSlotIndex!!]
                    slot.time = "$startTime - $endTime"
                    slot.subject = editSubject
                    editingDayIndex = null; editingSlotIndex = null; errorMessage = ""
                }) { Text("Save") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    editingDayIndex = null; editingSlotIndex = null; errorMessage = ""
                }) { Text("Cancel") }
            }
        )
    }
}
