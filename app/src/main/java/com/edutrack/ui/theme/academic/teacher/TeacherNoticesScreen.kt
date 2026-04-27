package com.edutrack.ui.academic.teacher

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.edutrack.data.model.Notification
import com.edutrack.data.model.UiState
import com.edutrack.data.model.UserProfile
import com.edutrack.ui.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * TeacherNoticesScreen
 *
 * Teacher can:
 *  - Write a notice (text) and optionally attach a file (image/PDF)
 *  - Target a specific class+division or broadcast to ALL
 *  - View full history of their own posted notifications (real-time)
 *  - Delete notifications
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherNoticesScreen(
    teacherProfile: UserProfile,
    onBack: () -> Unit,
    viewModel: NotificationViewModel = viewModel()
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val operationResult by viewModel.operationResult.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Form state
    var noticeTitle by remember { mutableStateOf("") }
    var noticeText by remember { mutableStateOf("") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }
    var fileBytes by remember { mutableStateOf<ByteArray?>(null) }

    // Class + Division targeting
    val classOptions = listOf("ALL", "FE", "SE", "TE", "BE")
    val divisionOptions = listOf("ALL", "A", "B", "C", "D", "E", "F")
    var targetClass by remember { mutableStateOf(teacherProfile.userClass.ifBlank { "ALL" }) }
    var targetDivision by remember { mutableStateOf(teacherProfile.division.ifBlank { "ALL" }) }
    var classExpanded by remember { mutableStateOf(false) }
    var divisionExpanded by remember { mutableStateOf(false) }

    // File picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fileUri = uri
        uri?.let { u ->
            val cursor = context.contentResolver.query(u, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (it.moveToFirst() && nameIndex >= 0) fileName = it.getString(nameIndex) ?: "file"
            }
            // Read file bytes for upload
            try {
                fileBytes = context.contentResolver.openInputStream(u)?.readBytes()
            } catch (e: Exception) {
                fileBytes = null
            }
        }
    }

    // Load teacher's notifications on screen entry
    LaunchedEffect(teacherProfile.userId) {
        viewModel.loadTeacherNotifications(teacherProfile.userId)
    }

    LaunchedEffect(operationResult) {
        operationResult?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearOperationResult()
            // Clear form on success
            if (it.contains("published", ignoreCase = true)) {
                noticeTitle = ""; noticeText = ""; fileUri = null; fileName = ""; fileBytes = null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notices") },
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Compose Notice Section ──────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Post New Notice",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = noticeTitle,
                            onValueChange = { noticeTitle = it },
                            label = { Text("Notice Title *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = noticeText,
                            onValueChange = { noticeText = it },
                            label = { Text("Notice Message") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5
                        )

                        // ── Target dropdowns ──────────────────────────
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Class
                            ExposedDropdownMenuBox(
                                expanded = classExpanded,
                                onExpandedChange = { classExpanded = it },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = targetClass,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Class") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(classExpanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(expanded = classExpanded, onDismissRequest = { classExpanded = false }) {
                                    classOptions.forEach { cls ->
                                        DropdownMenuItem(text = { Text(cls) }, onClick = { targetClass = cls; classExpanded = false })
                                    }
                                }
                            }

                            // Division
                            ExposedDropdownMenuBox(
                                expanded = divisionExpanded,
                                onExpandedChange = { divisionExpanded = it },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = targetDivision,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Division") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(divisionExpanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(expanded = divisionExpanded, onDismissRequest = { divisionExpanded = false }) {
                                    divisionOptions.forEach { div ->
                                        DropdownMenuItem(text = { Text(div) }, onClick = { targetDivision = div; divisionExpanded = false })
                                    }
                                }
                            }
                        }

                        // ── Attach File ───────────────────────────────
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clickable { filePickerLauncher.launch("*/*") },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (fileUri != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(8.dp))
                                        Text(fileName, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth(0.7f))
                                        IconButton(onClick = { fileUri = null; fileName = ""; fileBytes = null }) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove file")
                                        }
                                    }
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.UploadFile, contentDescription = "Upload", modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                                        Text("Attach File (optional)", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        // Image preview
                        if (fileUri != null) {
                            val mimeType = context.contentResolver.getType(fileUri!!)
                            if (mimeType?.startsWith("image/") == true) {
                                AsyncImage(
                                    model = fileUri,
                                    contentDescription = "Preview",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                )
                            }
                        }

                        // ── Publish Button ────────────────────────────
                        val canPublish = noticeTitle.isNotBlank() || noticeText.isNotBlank()
                        Button(
                            onClick = {
                                if (!canPublish) return@Button
                                val notification = Notification(
                                    title = noticeTitle.trim(),
                                    message = noticeText.trim(),
                                    userClass = targetClass,
                                    division = targetDivision,
                                    postedBy = teacherProfile.userId,
                                    postedByName = teacherProfile.name,
                                    timestamp = System.currentTimeMillis()
                                )
                                viewModel.postNotification(notification, fileBytes, fileName.ifBlank { null })
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(if (canPublish) 1f else 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Publish, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Publish Notice")
                        }
                    }
                }
            }

            // ── Notification History ───────────────────────────────────
            item {
                Text(
                    "Posted Notices",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            when (val state = notifications) {
                is UiState.Loading -> item {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Empty -> item {
                    Text(
                        "No notices posted yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                is UiState.Error -> item {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
                is UiState.Success -> items(state.data, key = { it.id }) { notification ->
                    NotificationHistoryCard(
                        notification = notification,
                        onDelete = { viewModel.deleteNotification(notification.id) }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Notification History Card (Teacher view)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun NotificationHistoryCard(
    notification: Notification,
    onDelete: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }
    val dateStr = remember(notification.timestamp) {
        SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(notification.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (notification.title.isNotBlank()) {
                        Text(notification.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text(
                        "${notification.userClass} — Div ${notification.division}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { showConfirm = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
            if (notification.message.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(notification.message, style = MaterialTheme.typography.bodySmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(4.dp))
            Text(dateStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Delete Notice") },
            text = { Text("This notice will be removed for all students. Continue?") },
            confirmButton = {
                Button(
                    onClick = { onDelete(); showConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = { OutlinedButton(onClick = { showConfirm = false }) { Text("Cancel") } }
        )
    }
}
