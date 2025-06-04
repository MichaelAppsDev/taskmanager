package com.example.taskmanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.taskmanager.data.models.Priority
import com.example.taskmanager.data.models.TaskStatus
import java.util.Date
import java.util.Calendar

enum class ItemType {
    TASK, REMINDER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskReminderDialog(
    collectionId: String,
    onDismiss: () -> Unit,
    onCreateTask: (String, String, Date?, Priority, TaskStatus) -> Unit,
    onCreateReminder: (String, String, Date) -> Unit
) {
    var selectedType by remember { mutableStateOf(ItemType.TASK) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var dateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableStateOf(12) }
    var selectedMinute by remember { mutableStateOf(0) }
    var priority by remember { mutableStateOf(Priority.NORMAL) }
    var status by remember { mutableStateOf(TaskStatus.UNCOMPLETED) }
    
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
    val date: Date? = dateMillis?.let { 
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = it
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)
        calendar.time
    }
    val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Create New Item",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                // Type selection with improved visual feedback
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(Modifier.selectableGroup()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedType == ItemType.TASK,
                                    onClick = { selectedType = ItemType.TASK },
                                    role = Role.RadioButton
                                )
                                .padding(16.dp)
                        ) {
                            RadioButton(
                                selected = selectedType == ItemType.TASK,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Task",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Create a task with priority and status",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        
                        Divider()
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedType == ItemType.REMINDER,
                                    onClick = { selectedType = ItemType.REMINDER },
                                    role = Role.RadioButton
                                )
                                .padding(16.dp)
                        ) {
                            RadioButton(
                                selected = selectedType == ItemType.REMINDER,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Reminder",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Set a reminder for a specific time",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
                
                // Common fields
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                
                // Date and time selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (selectedType == ItemType.TASK) "Due Date & Time:" else "Reminder Date & Time:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (date != null) {
                                Text(
                                    text = dateFormat.format(date),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                TextButton(onClick = { 
                                    dateMillis = null
                                    selectedHour = 12
                                    selectedMinute = 0
                                }) {
                                    Text("Clear")
                                }
                            } else {
                                Text(
                                    text = "Not set",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showDatePicker = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Pick Date")
                            }
                            Button(
                                onClick = { showTimePicker = true },
                                modifier = Modifier.weight(1f),
                                enabled = dateMillis != null
                            ) {
                                Text("Pick Time")
                            }
                        }
                    }
                }
                
                // Task-specific fields
                if (selectedType == ItemType.TASK) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Priority:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PriorityButton(
                                    text = "Low",
                                    selected = priority == Priority.LOW,
                                    onClick = { priority = Priority.LOW },
                                    modifier = Modifier.weight(1f)
                                )
                                PriorityButton(
                                    text = "Normal",
                                    selected = priority == Priority.NORMAL,
                                    onClick = { priority = Priority.NORMAL },
                                    modifier = Modifier.weight(1f)
                                )
                                PriorityButton(
                                    text = "High",
                                    selected = priority == Priority.HIGH,
                                    onClick = { priority = Priority.HIGH },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Status:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatusButton(
                                    text = "Uncompleted",
                                    selected = status == TaskStatus.UNCOMPLETED,
                                    onClick = { status = TaskStatus.UNCOMPLETED },
                                    modifier = Modifier.weight(1f)
                                )
                                StatusButton(
                                    text = "Completed",
                                    selected = status == TaskStatus.COMPLETED,
                                    onClick = { status = TaskStatus.COMPLETED },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                
                // Reminder-specific validation
                val isReminderValid = selectedType != ItemType.REMINDER || date != null
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (selectedType == ItemType.TASK) {
                                onCreateTask(title, description, date, priority, status)
                            } else {
                                date?.let { onCreateReminder(title, description, it) }
                            }
                            onDismiss()
                        },
                        enabled = title.isNotBlank() && isReminderValid
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
    
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateMillis = datePickerState.selectedDateMillis
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
    
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Hour picker
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Hour")
                            NumberPicker(
                                value = selectedHour,
                                onValueChange = { selectedHour = it },
                                range = 0..23
                            )
                        }
                        
                        // Minute picker
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Minute")
                            NumberPicker(
                                value = selectedMinute,
                                onValueChange = { selectedMinute = it },
                                range = 0..59
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PriorityButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    ) {
        Text(text)
    }
}

@Composable
private fun StatusButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    ) {
        Text(text, maxLines = 1)
    }
}

@Composable
private fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    Column {
        IconButton(
            onClick = { 
                if (value < range.last) onValueChange(value + 1)
            }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Increase"
            )
        }
        
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        IconButton(
            onClick = { 
                if (value > range.first) onValueChange(value - 1)
            }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Decrease"
            )
        }
    }
} 