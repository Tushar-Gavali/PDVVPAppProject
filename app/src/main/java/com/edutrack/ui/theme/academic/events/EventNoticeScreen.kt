package com.edutrack.ui.academic.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edutrack.data.model.Event
import com.edutrack.data.model.EventType
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventNoticeScreen(onBack: () -> Unit) {
    var selectedFilter by remember { mutableStateOf("All") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Notices") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter events */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = { /* Add new event */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Event")
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
            // Filter chips
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("All", "Academic", "Cultural", "Sports", "Workshop", "Seminar")
                items(filters) { filter ->
                    FilterChip(
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        selected = selectedFilter == filter
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            EventsList(selectedFilter = selectedFilter)
        }
    }
}

@Composable
fun EventsList(selectedFilter: String) {
    // Mock data for events
    val events = remember {
        listOf(
            Event(
                id = "E001",
                title = "Annual Tech Fest 2024",
                description = "Join us for the biggest technical event of the year featuring coding competitions, workshops, and guest lectures.",
                eventType = EventType.CULTURAL,
                startDate = LocalDate.now().plusDays(15),
                endDate = LocalDate.now().plusDays(17),
                startTime = "09:00",
                endTime = "18:00",
                venue = "Main Auditorium",
                organizer = "Computer Science Department",
                maxParticipants = 500,
                registrationDeadline = LocalDate.now().plusDays(10),
                isRegistrationRequired = true,
                createdBy = "Admin"
            ),
            Event(
                id = "E002",
                title = "Machine Learning Workshop",
                description = "Hands-on workshop on machine learning fundamentals and practical applications.",
                eventType = EventType.WORKSHOP,
                startDate = LocalDate.now().plusDays(8),
                startTime = "14:00",
                endTime = "17:00",
                venue = "Lab 2",
                organizer = "AI Club",
                maxParticipants = 50,
                registrationDeadline = LocalDate.now().plusDays(5),
                isRegistrationRequired = true,
                createdBy = "Prof. Anderson"
            ),
            Event(
                id = "E003",
                title = "Inter-College Basketball Tournament",
                description = "Annual basketball tournament between various colleges. Come support your team!",
                eventType = EventType.SPORTS,
                startDate = LocalDate.now().plusDays(20),
                endDate = LocalDate.now().plusDays(22),
                startTime = "08:00",
                endTime = "20:00",
                venue = "Sports Complex",
                organizer = "Sports Committee",
                isRegistrationRequired = false,
                createdBy = "Sports Head"
            )
        )
    }

    val filteredEvents = if (selectedFilter == "All") {
        events
    } else {
        events.filter { it.eventType.name.equals(selectedFilter, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filteredEvents) { event ->
            EventCard(event = event)
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { /* Navigate to event details */ }
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
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                AssistChip(
                    onClick = { },
                    label = { Text(event.eventType.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (event.eventType) {
                            EventType.ACADEMIC -> MaterialTheme.colorScheme.primaryContainer
                            EventType.CULTURAL -> MaterialTheme.colorScheme.secondaryContainer
                            EventType.SPORTS -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Date and time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = "Date", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (event.endDate != null) "${event.startDate} - ${event.endDate}" else event.startDate.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Time", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${event.startTime} - ${event.endTime ?: ""}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Venue and organizer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                event.venue?.let { venue ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Venue", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(venue, style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                Text("By: ${event.organizer}", style = MaterialTheme.typography.bodySmall)
            }
            
            // Registration info
            if (event.isRegistrationRequired) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Registration deadline: ${event.registrationDeadline}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    
                    event.maxParticipants?.let { max ->
                        Text(
                            text = "Max: $max",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Register for event */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register Now")
                }
            }
        }
    }
}
