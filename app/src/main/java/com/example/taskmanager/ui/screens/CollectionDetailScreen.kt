package com.example.taskmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskmanager.data.models.Collection
import com.example.taskmanager.data.models.Task
import com.example.taskmanager.data.models.Reminder
import com.example.taskmanager.ui.components.TaskItem
import com.example.taskmanager.ui.components.ReminderItem
import com.example.taskmanager.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import com.example.taskmanager.data.models.Priority
import com.example.taskmanager.data.models.TaskStatus
import com.example.taskmanager.ui.components.CreateTaskReminderDialog
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailScreen(
    collectionId: String,
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val collection = uiState.collections.find { it.id == collectionId }
    val tasks = uiState.tasks.filter { it.collectionId == collectionId }
    val reminders = uiState.reminders.filter { reminder -> 
        // Show reminders that are standalone (taskId is empty) or related to tasks in this collection
        reminder.taskId.isEmpty() || tasks.any { task -> task.id == reminder.taskId }
    }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showingTasks by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(collection?.name ?: "Collection") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            collection?.description?.let {
                Text(text = it, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Toggle between Tasks and Reminders
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TabRow(
                    selectedTabIndex = if (showingTasks) 0 else 1,
                    modifier = Modifier.width(300.dp)
                ) {
                    Tab(
                        selected = showingTasks,
                        onClick = { showingTasks = true },
                        text = { Text("Tasks") }
                    )
                    Tab(
                        selected = !showingTasks,
                        onClick = { showingTasks = false },
                        text = { Text("Reminders") }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (showingTasks) {
                // Tasks view
                if (tasks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No tasks in this collection.")
                    }
                } else {
                    LazyColumn {
                        items(tasks) { task ->
                            TaskItem(
                                task = task,
                                viewModel = viewModel,
                                collectionColor = collection?.color
                            )
                        }
                    }
                }
            } else {
                // Reminders view
                if (reminders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No reminders in this collection.")
                    }
                } else {
                    LazyColumn {
                        items(reminders) { reminder ->
                            ReminderItem(
                                reminder = reminder,
                                collectionColor = collection?.color
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        CreateTaskReminderDialog(
            collectionId = collectionId,
            onDismiss = { showAddDialog = false },
            onCreateTask = { title, description, dueDate, priority, status ->
                viewModel.addTask(title, description, collectionId, dueDate, priority, status)
            },
            onCreateReminder = { title, description, date ->
                viewModel.addStandaloneReminder(title, description, date)
            }
        )
    }
} 