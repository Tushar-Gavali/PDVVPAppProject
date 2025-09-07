package com.edutrack.ui.academic.teacher

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherNoticesScreen(
    onBack: () -> Unit,
    onPublish: (noticeText: String?, fileUri: Uri?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var noticeText by remember { mutableStateOf("") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fileUri = uri
        if (uri != null) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (it.moveToFirst() && nameIndex >= 0) {
                    fileName = it.getString(nameIndex) ?: "Unknown"
                } else {
                    fileName = "Unknown"
                }
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
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onPublish(noticeText.takeIf { it.isNotBlank() }, fileUri) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(8.dp)
                    .alpha(if (noticeText.isNotBlank() || fileUri != null) 1f else 0.5f) // enable only if text or file
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Publish, contentDescription = "Publish")
                    Spacer(Modifier.width(8.dp))
                    Text("Publish")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Post Notice",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            // Manual notice text input
            OutlinedTextField(
                value = noticeText,
                onValueChange = { noticeText = it },
                label = { Text("Write notice here") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(Modifier.height(20.dp))

            // Upload card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable {
                        filePickerLauncher.launch("*/*") // allow all, will filter mime type later
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.UploadFile,
                        contentDescription = "Upload",
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Tap to upload JPG, PNG, PDF, or TXT file")
                }
            }

            Spacer(Modifier.height(20.dp))

            // Show selected file preview/details
            if (fileUri != null) {
                val mimeType = context.contentResolver.getType(fileUri!!)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("File: $fileName", fontWeight = FontWeight.Bold)

                        Spacer(Modifier.height(10.dp))

                        when {
                            mimeType?.startsWith("image/") == true -> {
                                AsyncImage(
                                    model = fileUri,
                                    contentDescription = "Selected Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                            }
                            mimeType == "application/pdf" -> {
                                Text("PDF selected: $fileName", color = Color.Gray)
                            }
                            mimeType?.startsWith("text/") == true -> {
                                Text("Text file selected: $fileName", color = Color.Gray)
                            }
                            else -> {
                                Text("Unsupported file type", color = Color.Red)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // View file button
                        Button(onClick = {
                            fileUri?.let { uri ->
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, mimeType)
                                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                }
                                context.startActivity(Intent.createChooser(intent, "Open with"))
                            }
                        }) {
                            Text("View File")
                        }
                    }
                }
            }
        }
    }
}
