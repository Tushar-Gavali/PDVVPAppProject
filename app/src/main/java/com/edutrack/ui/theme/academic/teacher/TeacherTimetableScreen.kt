package com.edutrack.ui.academic.teacher

import android.app.TimePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.*

// ✅ Each slot is reactive
class LectureSlot(
    time: String,
    subject: String
) {
    var time by mutableStateOf(time)
    var subject by mutableStateOf(subject)
}

// ✅ One day schedule
class DaySchedule(
    val day: String,
    val slots: SnapshotStateList<LectureSlot>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherTimetableScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // --- Timetable state ---
    val timetable = remember {
        mutableStateListOf<DaySchedule>(
            DaySchedule("Monday", mutableStateListOf(
                LectureSlot("09:00 - 10:00", "Maths"),
                LectureSlot("10:15 - 11:00", "Physics"),
                LectureSlot("11:15 - 12:45", "Chemistry"),
                LectureSlot("01:00 - 02:00", "English"),
                LectureSlot("02:15 - 04:15", "Physics Practical")
            )),
            DaySchedule("Tuesday", mutableStateListOf(
                LectureSlot("09:00 - 10:00", "Biology"),
                LectureSlot("10:15 - 11:00", "Maths"),
                LectureSlot("11:15 - 12:45", "Computer"),
                LectureSlot("01:00 - 02:00", "History"),
                LectureSlot("02:15 - 04:15", "Biology Practical")
            )),
            DaySchedule("Wednesday", mutableStateListOf(
                LectureSlot("09:00 - 10:00", "English"),
                LectureSlot("10:15 - 11:00", "Economics"),
                LectureSlot("11:15 - 12:45", "Maths"),
                LectureSlot("01:00 - 02:00", "Physics"),
                LectureSlot("02:15 - 04:15", "Chemistry Practical")
            )),
            DaySchedule("Thursday", mutableStateListOf(
                LectureSlot("09:00 - 10:00", "Chemistry"),
                LectureSlot("10:15 - 11:00", "Biology"),
                LectureSlot("11:15 - 12:45", "Maths"),
                LectureSlot("01:00 - 02:00", "English"),
                LectureSlot("02:15 - 04:15", "Computer Practical")
            )),
            DaySchedule("Friday", mutableStateListOf(
                LectureSlot("09:00 - 10:00", "Physics"),
                LectureSlot("10:15 - 11:00", "History"),
                LectureSlot("11:15 - 12:45", "Biology"),
                LectureSlot("01:00 - 02:00", "Economics"),
                LectureSlot("02:15 - 04:15", "Maths Practical")
            ))
        )
    }

    // --- Edit dialog state ---
    var editingDayIndex by remember { mutableStateOf<Int?>(null) }
    var editingSlotIndex by remember { mutableStateOf<Int?>(null) }
    var editSubject by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TimeTable") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            items(timetable.size) { dayIndex ->
                val daySchedule = timetable[dayIndex]

                Text(
                    text = daySchedule.day,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                ) {
                    daySchedule.slots.forEachIndexed { slotIndex, slot ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, Color.LightGray)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(slot.time, fontWeight = FontWeight.Bold)
                                Text(slot.subject)
                            }
                            IconButton(
                                onClick = {
                                    editingDayIndex = dayIndex
                                    editingSlotIndex = slotIndex
                                    editSubject = slot.subject
                                    val times = slot.time.split(" - ")
                                    if (times.size == 2) {
                                        startTime = times[0]
                                        endTime = times[1]
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Slot")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // --- Edit Dialog ---
    if (editingDayIndex != null && editingSlotIndex != null) {
        AlertDialog(
            onDismissRequest = {
                editingDayIndex = null
                editingSlotIndex = null
                errorMessage = ""
            },
            title = { Text("Edit Timetable Slot") },
            text = {
                Column {
                    // Start Time Picker
                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    startTime = String.format("%02d:%02d", hour, minute)
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (startTime.isNotEmpty()) "Start: $startTime" else "Select Start Time")
                    }

                    Spacer(Modifier.height(8.dp))

                    // End Time Picker
                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    endTime = String.format("%02d:%02d", hour, minute)
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (endTime.isNotEmpty()) "End: $endTime" else "Select End Time")
                    }

                    Spacer(Modifier.height(8.dp))

                    // Subject Field
                    OutlinedTextField(
                        value = editSubject,
                        onValueChange = { editSubject = it },
                        label = { Text("Subject") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(errorMessage, color = Color.Red)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (startTime.isEmpty() || endTime.isEmpty()) {
                        errorMessage = "Please select both start and end time"
                        return@Button
                    }

                    val (startHour, startMin) = startTime.split(":").map { it.toInt() }
                    val (endHour, endMin) = endTime.split(":").map { it.toInt() }

                    if (endHour < startHour || (endHour == startHour && endMin <= startMin)) {
                        errorMessage = "End time must be after start time"
                        return@Button
                    }

                    // ✅ Update slot
                    val slot = timetable[editingDayIndex!!].slots[editingSlotIndex!!]
                    slot.time = "$startTime - $endTime"
                    slot.subject = editSubject

                    editingDayIndex = null
                    editingSlotIndex = null
                    errorMessage = ""
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    editingDayIndex = null
                    editingSlotIndex = null
                    errorMessage = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
