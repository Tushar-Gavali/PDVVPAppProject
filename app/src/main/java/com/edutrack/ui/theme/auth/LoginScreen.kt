package com.edutrack.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.edutrack.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var prn by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showProfilePreview by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Profile Preview Tab at Top Center
        if (showProfilePreview) {
            ProfilePreviewCard(
                role = selectedRole,
                onClose = { showProfilePreview = false }
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo/Icon
                Card(
                    modifier = Modifier.size(80.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF8B4513) // Brown color like in the reference
                    ),
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = "EduTrack",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "PDVVP",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Academic Management System",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Role Selection
                Text(
                    text = "Select Role",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(UserRole.STUDENT, UserRole.TEACHER).forEach { role ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .selectable(
                                    selected = selectedRole == role,
                                    onClick = { selectedRole = role }
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedRole == role)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = when (role) {
                                        UserRole.STUDENT -> Icons.Default.Person
                                        UserRole.TEACHER -> Icons.Default.School
                                        else -> Icons.Default.Person
                                    },
                                    contentDescription = role.name,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (selectedRole == role)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = when (role) {
                                        UserRole.STUDENT -> "Student"
                                        UserRole.TEACHER -> "Teacher"
                                        else -> "Student"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (selectedRole == role) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Dynamic input fields based on role
                if (selectedRole == UserRole.STUDENT) {
                    // PRN Field for Students - Full width
                    OutlinedTextField(
                        value = prn,
                        onValueChange = { prn = it },
                        label = {
                            Text(
                                text = "Enter PRN Number",
                                textAlign = TextAlign.Start
                            )
                        },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = "PRN") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Text)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        dateOfBirth = sdf.format(Date(millis))
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
                            DatePicker(state = datePickerState, showModeToggle = true)
                        }
                    }

                    OutlinedTextField(
                        value = dateOfBirth,
                        onValueChange = { }, // disable manual typing
                        label = { Text("DOB (DD/MM/YYYY)") },
                        leadingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date")
                            }
                        },
                        placeholder = { Text("DD/MM/YYYY") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        readOnly = true // 👈 prevents typing, only via calendar
                    )

                    // Date of Birth Field for Students - Full width
//                    OutlinedTextField(
//                        value = dateOfBirth,
//                        onValueChange = {
//                            // Format input as DD/MM/YYYY
//                            val formatted = formatDateInput(it)
//                            if (formatted.length <= 10) {
//                                dateOfBirth = formatted
//                            }
//                        },
//                        label = { Text("DOB (DD/MM/YYYY)") },
//                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Date of Birth") },
//                        placeholder = { Text("DD/MM/YYYY") },
//                        modifier = Modifier.fillMaxWidth(),
//                        singleLine = true,
//                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
//                    )

                    Spacer(modifier = Modifier.height(32.dp))
                } else {
                    // Email Field for Teachers and Admins
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field for Teachers and Admins
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Error Message
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Login Button
                Button(
                    onClick = {
                        if (selectedRole == UserRole.STUDENT) {
                            // ✅ Student Login: Just PRN + DOB validation
                            if (prn.isNotEmpty() && dateOfBirth.isNotEmpty()) {
                                isLoading = true
                                errorMessage = ""
                                onLoginSuccess(UserRole.STUDENT)
                            } else {
                                errorMessage = "Please fill in PRN and Date of Birth"
                            }
                        } else {
                            // ✅ Teacher Login: FirebaseAuth Email + Password
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                isLoading = true
                                errorMessage = ""
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess(UserRole.TEACHER)
                                        } else {

                                            val errorMessage = when (val errorCode = (task.exception as? FirebaseAuthException)?.errorCode) {
                                                "ERROR_INVALID_EMAIL" -> "Please enter a valid email address."
                                                "ERROR_USER_NOT_FOUND" -> "No account found with this email."
                                                "ERROR_WRONG_PASSWORD" -> "Incorrect password, try again."
                                                "ERROR_USER_DISABLED" -> "This account has been disabled."
                                                else -> task.exception?.localizedMessage ?: "Login failed. Please try again."
                                            }

                                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()

                                        }
                                    }
                            } else {
                                Toast.makeText(context, "Please fill Email and Password", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Login")
                    }
                }


                // Registration Link - Only for Teachers
                if (selectedRole == UserRole.TEACHER) {
                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = onRegisterClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Don't have an account? Register")
                    }
                }


            }
        }
    }
}

@Composable
fun ProfilePreviewCard(
    role: UserRole,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${role.name} Profile Preview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile preview content
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile icon
                Card(
                    modifier = Modifier.size(60.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (role) {
                                UserRole.STUDENT -> Icons.Default.Person
                                UserRole.TEACHER -> Icons.Default.School
                                UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                            },
                            contentDescription = role.name,
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = when (role) {
                            UserRole.STUDENT -> "John Doe"
                            UserRole.TEACHER -> "Dr. Sarah Johnson"
                            UserRole.ADMIN -> "Admin User"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (role) {
                            UserRole.STUDENT -> "Computer Science - Semester 5"
                            UserRole.TEACHER -> "Professor - Computer Science"
                            UserRole.ADMIN -> "System Administrator"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = when (role) {
                            UserRole.STUDENT -> "Roll: CS001"
                            UserRole.TEACHER -> "EMP001"
                            UserRole.ADMIN -> "Admin Panel Access"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                when (role) {
                    UserRole.STUDENT -> {
                        PreviewStat("CGPA", "8.7", Icons.Default.Grade)
                        PreviewStat("Attendance", "95%", Icons.Default.Person)
                        PreviewStat("Subjects", "6", Icons.Default.Book)
                    }
                    UserRole.TEACHER -> {
                        PreviewStat("Students", "120", Icons.Default.People)
                        PreviewStat("Subjects", "4", Icons.Default.Book)
                        PreviewStat("Experience", "15 years", Icons.Default.Work)
                    }
                    UserRole.ADMIN -> {
                        PreviewStat("Users", "500+", Icons.Default.People)
                        PreviewStat("Modules", "8", Icons.Default.Apps)
                        PreviewStat("System", "Active", Icons.Default.CheckCircle)
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewStat(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Helper function to format date input as DD/MM/YYYY
private fun formatDateInput(input: String): String {
    val digitsOnly = input.filter { it.isDigit() }
    return when {
        digitsOnly.length <= 2 -> digitsOnly
        digitsOnly.length <= 4 -> "${digitsOnly.take(2)}/${digitsOnly.drop(2)}"
        else -> "${digitsOnly.take(2)}/${digitsOnly.drop(2).take(2)}/${digitsOnly.drop(4).take(4)}"
    }
}
