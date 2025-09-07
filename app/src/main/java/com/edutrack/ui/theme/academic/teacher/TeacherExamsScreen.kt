package com.edutrack.ui.academic.teacher

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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

data class MCQQuestion(
    var questionText: String = "",
    var options: MutableList<String> = mutableListOf("", "", "", ""),
    var correctAnswerIndex: Int? = null,
    var fileUri: Uri? = null,
    var fileName: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherExamsScreen(
    onBack: () -> Unit,
    onPublish: (examTitle: String, questions: List<MCQQuestion>) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var examTitle by remember { mutableStateOf("") }
    var questions by remember { mutableStateOf(mutableListOf(MCQQuestion())) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create MCQ Test") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onPublish(examTitle, questions) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(8.dp)
                    .alpha(if (examTitle.isNotBlank() && questions.all { it.questionText.isNotBlank() && it.correctAnswerIndex != null }) 1f else 0.5f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Publish, contentDescription = "Publish")
                    Spacer(Modifier.width(8.dp))
                    Text("Publish Test")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                OutlinedTextField(
                    value = examTitle,
                    onValueChange = { examTitle = it },
                    label = { Text("Exam Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    questions.add(MCQQuestion())
                }) {
                    Text("Add New Question")
                }

                Spacer(Modifier.height(16.dp))
            }

            itemsIndexed(questions) { index, question ->
                val filePickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    questions[index] = questions[index].copy(
                        fileUri = uri,
                        fileName = uri?.let {
                            val cursor = context.contentResolver.query(uri, null, null, null, null)
                            cursor?.use {
                                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                if (it.moveToFirst() && nameIndex >= 0) {
                                    it.getString(nameIndex)
                                } else null
                            }
                        } ?: ""
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Question ${index + 1}", fontWeight = FontWeight.Bold)
                            IconButton(onClick = { questions.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Question")
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = question.questionText,
                            onValueChange = {
                                questions[index] = questions[index].copy(questionText = it)
                            },
                            label = { Text("Question Text") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        question.options.forEachIndexed { optIndex, option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = question.correctAnswerIndex == optIndex,
                                    onClick = {
                                        questions[index] = questions[index].copy(correctAnswerIndex = optIndex)
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = option,
                                    onValueChange = {
                                        val newOptions = question.options.toMutableList()
                                        newOptions[optIndex] = it
                                        questions[index] = questions[index].copy(options = newOptions)
                                    },
                                    label = { Text("Option ${'A' + optIndex}") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                        }

                        Spacer(Modifier.height(8.dp))

                        // Upload optional file
                        Button(onClick = { filePickerLauncher.launch("*/*") }) {
                            Text("Attach File (optional)")
                        }

                        if (question.fileUri != null) {
                            Spacer(Modifier.height(4.dp))
                            val mimeType = context.contentResolver.getType(question.fileUri!!)
                            Text("File: ${question.fileName}")
                            if (mimeType?.startsWith("image/") == true) {
                                AsyncImage(
                                    model = question.fileUri,
                                    contentDescription = "Attached Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                )
                            } else if (mimeType == "application/pdf" || mimeType?.startsWith("text/") == true) {
                                Text("(Preview not available)")
                            }
                            Spacer(Modifier.height(4.dp))
                            Button(onClick = {
                                question.fileUri?.let { uri ->
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
}
