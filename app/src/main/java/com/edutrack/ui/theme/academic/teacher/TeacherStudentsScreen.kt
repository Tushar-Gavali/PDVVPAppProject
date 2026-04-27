package com.edutrack.ui.academic.teacher

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edutrack.data.model.AuthResult
import com.edutrack.data.model.UserProfile
import com.edutrack.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────
class StudentManagementViewModel : ViewModel() {
    private val repo = AuthRepository()

    private val _students = MutableStateFlow<List<UserProfile>>(emptyList())
    val students: StateFlow<List<UserProfile>> = _students.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun loadStudents(userClass: String, division: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _students.value = repo.getStudentsForClass(userClass, division)
            _isLoading.value = false
        }
    }

    fun registerStudent(
        profile: UserProfile,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repo.registerStudent(profile)
            _isLoading.value = false
            when (result) {
                is AuthResult.Success -> {
                    _message.value = "✅ Student '${result.profile.name}' registered!"
                    // Refresh list
                    _students.value = repo.getStudentsForClass(
                        result.profile.userClass, result.profile.division)
                    onSuccess()
                }
                is AuthResult.Error -> _message.value = "❌ ${result.message}"
                else -> {}
            }
        }
    }

    fun deleteStudent(userId: String, userClass: String, division: String) {
        viewModelScope.launch {
            repo.deleteStudent(userId)
            _students.value = repo.getStudentsForClass(userClass, division)
            _message.value = "Student removed."
        }
    }

    fun clearMessage() { _message.value = null }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main Screen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherStudentsScreen(
    teacherProfile: UserProfile? = null,
    onBack: () -> Unit,
    viewModel: StudentManagementViewModel = viewModel()
) {
    val students by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<UserProfile?>(null) }

    // Use teacher's own class+division as default filter
    val defaultClass = teacherProfile?.userClass ?: ""
    val defaultDiv   = teacherProfile?.division ?: ""

    var filterClass   by remember { mutableStateOf(defaultClass) }
    var filterDiv     by remember { mutableStateOf(defaultDiv) }

    // Load on first compose
    LaunchedEffect(filterClass, filterDiv) {
        if (filterClass.isNotBlank() && filterDiv.isNotBlank()) {
            viewModel.loadStudents(filterClass, filterDiv)
        }
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Students")
                        if (filterClass.isNotBlank())
                            Text("$filterClass — Div $filterDiv",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Add Student") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Class + Division filter row ───────────────────────────────────
            ClassDivisionFilterRow(
                selectedClass = filterClass,
                selectedDiv   = filterDiv,
                onClassChange = { filterClass = it; filterDiv = "" },
                onDivChange   = { filterDiv = it }
            )

            // ── Students list ─────────────────────────────────────────────────
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (students.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.People, null,
                            Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        Spacer(Modifier.height(12.dp))
                        Text("No students registered yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Tap '+' to add the first student.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text("${students.size} Students",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp))
                    }
                    items(students, key = { it.userId }) { student ->
                        StudentCard(
                            student = student,
                            onDelete = { deleteTarget = student }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) } // FAB clearance
                }
            }
        }
    }

    // ── Add Student Dialog ────────────────────────────────────────────────────
    if (showAddDialog) {
        AddStudentDialog(
            defaultClass = filterClass,
            defaultDiv   = filterDiv,
            isLoading    = isLoading,
            onDismiss    = { showAddDialog = false },
            onRegister   = { profile ->
                viewModel.registerStudent(profile) {
                    showAddDialog = false
                    filterClass = profile.userClass
                    filterDiv   = profile.division
                }
            }
        )
    }

    // ── Confirm Delete Dialog ─────────────────────────────────────────────────
    deleteTarget?.let { student ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            icon = { Icon(Icons.Default.Warning, null) },
            title = { Text("Remove Student") },
            text = { Text("Remove '${student.name}' (PRN: ${student.prn})?\nThis cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStudent(student.userId, student.userClass, student.division)
                        deleteTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Remove") }
            },
            dismissButton = { OutlinedButton(onClick = { deleteTarget = null }) { Text("Cancel") } }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Student Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun StudentCard(student: UserProfile, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar circle
            Card(
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.size(48.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        student.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(student.name, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall)
                Text("PRN: ${student.prn}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (student.branch.isNotBlank())
                        AssistChip(onClick = {}, label = {
                            Text(student.branch, style = MaterialTheme.typography.labelSmall) })
                    AssistChip(onClick = {}, label = {
                        Text("${student.userClass}-${student.division}",
                            style = MaterialTheme.typography.labelSmall) })
                }
                if (student.dateOfBirth.isNotBlank())
                    Text("DOB: ${student.dateOfBirth}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.PersonRemove, contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Class + Division Filter Row
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDivisionFilterRow(
    selectedClass: String,
    selectedDiv: String,
    onClassChange: (String) -> Unit,
    onDivChange: (String) -> Unit
) {
    val classes   = listOf("FE", "SE", "TE", "BE")
    val divisions = listOf("A", "B", "C", "D", "E", "F")
    var classExpanded by remember { mutableStateOf(false) }
    var divExpanded   by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Class dropdown
        ExposedDropdownMenuBox(
            expanded = classExpanded,
            onExpandedChange = { classExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = selectedClass.ifBlank { "Class" },
                onValueChange = {},
                readOnly = true,
                label = { Text("Class") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = classExpanded, onDismissRequest = { classExpanded = false }) {
                classes.forEach { c ->
                    DropdownMenuItem(text = { Text(c) }, onClick = {
                        onClassChange(c); classExpanded = false
                    })
                }
            }
        }
        // Division dropdown
        ExposedDropdownMenuBox(
            expanded = divExpanded,
            onExpandedChange = { divExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = selectedDiv.ifBlank { "Div" },
                onValueChange = {},
                readOnly = true,
                label = { Text("Division") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = divExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = divExpanded, onDismissRequest = { divExpanded = false }) {
                divisions.forEach { d ->
                    DropdownMenuItem(text = { Text(d) }, onClick = {
                        onDivChange(d); divExpanded = false
                    })
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Add Student Dialog — Full Registration Form
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentDialog(
    defaultClass: String,
    defaultDiv: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onRegister: (UserProfile) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName  by remember { mutableStateOf("") }
    var address   by remember { mutableStateOf("") }
    var prn       by remember { mutableStateOf("") }
    var dob       by remember { mutableStateOf("") }   // dd/MM/yyyy
    var selClass  by remember { mutableStateOf(defaultClass) }
    var selDiv    by remember { mutableStateOf(defaultDiv) }
    var selBranch by remember { mutableStateOf("") }
    var error     by remember { mutableStateOf("") }

    var classExpanded  by remember { mutableStateOf(false) }
    var divExpanded    by remember { mutableStateOf(false) }
    var branchExpanded by remember { mutableStateOf(false) }

    val classes  = listOf("FE", "SE", "TE", "BE")
    val divs     = listOf("A", "B", "C", "D", "E", "F")
    val branches = listOf("IT", "CS", "CSD", "ETC", "Mech", "Civil", "Electrical")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonAdd, null,
                    tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Register New Student", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (error.isNotBlank()) {
                    Card(colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Text(error, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(10.dp))
                    }
                }

                // Name row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                }

                // PRN — accepts alphanumeric e.g. BE721425R
                OutlinedTextField(
                    value = prn,
                    onValueChange = { prn = it.uppercase() },
                    label = { Text("PRN Number *") },
                    placeholder = { Text("e.g. BE721425R or 2240040001") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    leadingIcon = { Icon(Icons.Default.Badge, null) }
                )

                // DOB — Calendar Date Picker
                var showDatePicker by remember { mutableStateOf(false) }
                val datePickerState = rememberDatePickerState()

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    dob = sdf.format(Date(millis))
                                }
                                showDatePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = true
                        )
                    }
                }

                OutlinedTextField(
                    value = dob,
                    onValueChange = {},
                    label = { Text("Date of Birth *") },
                    placeholder = { Text("Select from calendar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    singleLine = true,
                    readOnly = true,
                    enabled = false,
                    leadingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Pick date")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // Class dropdown
                ExposedDropdownMenuBox(
                    expanded = classExpanded,
                    onExpandedChange = { classExpanded = it }) {
                    OutlinedTextField(
                        value = selClass.ifBlank { "Select Class *" },
                        onValueChange = {}, readOnly = true,
                        label = { Text("Class *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(classExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(classExpanded, { classExpanded = false }) {
                        classes.forEach { c ->
                            DropdownMenuItem(text = { Text(c) }, onClick = { selClass = c; classExpanded = false })
                        }
                    }
                }

                // Division dropdown
                ExposedDropdownMenuBox(
                    expanded = divExpanded,
                    onExpandedChange = { divExpanded = it }) {
                    OutlinedTextField(
                        value = selDiv.ifBlank { "Select Division *" },
                        onValueChange = {}, readOnly = true,
                        label = { Text("Division *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(divExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(divExpanded, { divExpanded = false }) {
                        divs.forEach { d ->
                            DropdownMenuItem(text = { Text(d) }, onClick = { selDiv = d; divExpanded = false })
                        }
                    }
                }

                // Branch dropdown
                ExposedDropdownMenuBox(
                    expanded = branchExpanded,
                    onExpandedChange = { branchExpanded = it }) {
                    OutlinedTextField(
                        value = selBranch.ifBlank { "Select Branch *" },
                        onValueChange = {}, readOnly = true,
                        label = { Text("Branch *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(branchExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(branchExpanded, { branchExpanded = false }) {
                        branches.forEach { b ->
                            DropdownMenuItem(text = { Text(b) }, onClick = { selBranch = b; branchExpanded = false })
                        }
                    }
                }

                // Address
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    maxLines = 3,
                    leadingIcon = { Icon(Icons.Default.Home, null) }
                )

                Text("* Required fields",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
        },
        confirmButton = {
            Button(
                enabled = !isLoading,
                onClick = {
                    // Validate
                    error = when {
                        firstName.isBlank() -> "First name is required."
                        lastName.isBlank()  -> "Last name is required."
                        prn.isBlank()       -> "PRN Number is required."
                        dob.isBlank()       -> "Date of Birth is required."
                        !dob.matches(Regex("""\d{2}/\d{2}/\d{4}""")) ->
                            "Please select a date of birth using the calendar."
                        selClass.isBlank()  -> "Please select a class."
                        selDiv.isBlank()    -> "Please select a division."
                        selBranch.isBlank() -> "Please select a branch."
                        else -> ""
                    }
                    if (error.isBlank()) {
                        onRegister(
                            UserProfile(
                                firstName   = firstName.trim(),
                                lastName    = lastName.trim(),
                                prn         = prn.trim(),
                                dateOfBirth = dob.trim(),
                                userClass   = selClass,
                                division    = selDiv,
                                branch      = selBranch,
                                address     = address.trim(),
                                role        = "student"
                            )
                        )
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.PersonAdd, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Register")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancel") }
        }
    )
}
