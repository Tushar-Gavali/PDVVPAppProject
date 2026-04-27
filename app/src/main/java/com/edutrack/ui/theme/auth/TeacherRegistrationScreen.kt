package com.edutrack.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.edutrack.data.model.UserProfile

/**
 * TeacherRegistrationScreen
 *
 * Collects teacher registration details and calls
 * [onRegister] with the UserProfile + credentials.
 * AuthViewModel handles the actual Firebase Auth + Firestore write.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherRegistrationScreen(
    isLoading: Boolean = false,
    onBack: () -> Unit,
    onRegister: (email: String, password: String, profile: UserProfile) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Class and division selections
    val classOptions = listOf("FE", "SE", "TE", "BE")
    val divisionOptions = listOf("A", "B", "C", "D", "E", "F")
    var selectedClass by remember { mutableStateOf("") }
    var selectedDivision by remember { mutableStateOf("") }
    var classExpanded by remember { mutableStateOf(false) }
    var divisionExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    fun validate(): Boolean {
        return when {
            name.isBlank()                        -> { errorMessage = "Name is required"; false }
            email.isBlank()                       -> { errorMessage = "Email is required"; false }
            !email.contains("@")                  -> { errorMessage = "Enter a valid email"; false }
            password.length < 6                   -> { errorMessage = "Password must be at least 6 characters"; false }
            password != confirmPassword           -> { errorMessage = "Passwords do not match"; false }
            selectedClass.isBlank()              -> { errorMessage = "Please select a class"; false }
            selectedDivision.isBlank()           -> { errorMessage = "Please select a division"; false }
            else                                  -> { errorMessage = ""; true }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teacher Registration") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Create Your Teacher Account",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your account will be reviewed before activation.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            // ── Full Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ── Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ── Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (min 6 chars)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ── Confirm Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm") },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ── Class Dropdown
            ExposedDropdownMenuBox(
                expanded = classExpanded,
                onExpandedChange = { classExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedClass,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Class") },
                    leadingIcon = { Icon(Icons.Default.Class, contentDescription = "Class") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = classExpanded,
                    onDismissRequest = { classExpanded = false }
                ) {
                    classOptions.forEach { cls ->
                        DropdownMenuItem(
                            text = { Text(cls) },
                            onClick = { selectedClass = cls; classExpanded = false }
                        )
                    }
                }
            }

            // ── Division Dropdown
            ExposedDropdownMenuBox(
                expanded = divisionExpanded,
                onExpandedChange = { divisionExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedDivision,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Division") },
                    leadingIcon = { Icon(Icons.Default.Groups, contentDescription = "Division") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = divisionExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = divisionExpanded,
                    onDismissRequest = { divisionExpanded = false }
                ) {
                    divisionOptions.forEach { div ->
                        DropdownMenuItem(
                            text = { Text(div) },
                            onClick = { selectedDivision = div; divisionExpanded = false }
                        )
                    }
                }
            }

            // ── Error display
            if (errorMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Register button
            Button(
                onClick = {
                    if (validate()) {
                        val profile = UserProfile(
                            name = name.trim(),
                            email = email.trim(),
                            role = "teacher",
                            userClass = selectedClass,
                            division = selectedDivision
                        )
                        onRegister(email.trim(), password.trim(), profile)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.HowToReg, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Register", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
