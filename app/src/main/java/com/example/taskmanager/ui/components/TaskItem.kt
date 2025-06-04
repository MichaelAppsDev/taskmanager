package com.example.taskmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.taskmanager.data.models.Task
import com.example.taskmanager.data.models.TaskStatus
import com.example.taskmanager.data.models.Priority
import com.example.taskmanager.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert

@Composable
fun TaskItem(
    task: Task,
    viewModel: TaskViewModel,
    collectionColor: Int? = null,
    collectionName: String? = null
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    var showStatusMenu by remember { mutableStateOf(false) }
    var showActionMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    
    // Calculate days until deadline
    val daysUntilDeadline = task.dueDate?.let { dueDate ->
        val currentDate = Calendar.getInstance().time
        val diff = dueDate.time - currentDate.time
        val days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
        when {
            days < 0 -> "Overdue"
            days == 0L -> "Due today"
            days == 1L -> "Due tomorrow"
            else -> "$days days left"
        }
    }
    
    // Update task status if overdue
    LaunchedEffect(task.dueDate) {
        task.dueDate?.let { dueDate ->
            if (dueDate.before(Calendar.getInstance().time) && task.status == TaskStatus.UNCOMPLETED) {
                viewModel.updateTask(task.copy(status = TaskStatus.OUTDATED))
            }
        }
    }
    
    if (showEditDialog) {
        EditTaskDialog(
            task = task,
            viewModel = viewModel,
            onDismiss = { showEditDialog = false }
        )
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = collectionColor?.let { Color(it).copy(alpha = 0.1f) } ?: MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = collectionColor?.let { Color(it) } ?: MaterialTheme.colorScheme.onSurface
                    )
                    if (collectionName != null) {
                        Text(
                            text = collectionName,
                            style = MaterialTheme.typography.bodySmall,
                            color = collectionColor?.let { Color(it).copy(alpha = 0.7f) } ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                Row {
                    Box {
                        TextButton(
                            onClick = { showStatusMenu = true }
                        ) {
                            Text(
                                text = when (task.status) {
                                    TaskStatus.UNCOMPLETED -> "Uncompleted"
                                    TaskStatus.COMPLETED -> "Completed"
                                    TaskStatus.OUTDATED -> "Outdated"
                                },
                                color = when (task.status) {
                                    TaskStatus.UNCOMPLETED -> collectionColor?.let { Color(it) } ?: MaterialTheme.colorScheme.primary
                                    TaskStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
                                    TaskStatus.OUTDATED -> MaterialTheme.colorScheme.error
                                }
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showStatusMenu,
                            onDismissRequest = { showStatusMenu = false }
                        ) {
                            TaskStatus.values().forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.name) },
                                    onClick = {
                                        viewModel.updateTask(task.copy(status = status))
                                        showStatusMenu = false
                                    }
                                )
                            }
                        }
                    }
                    
                    IconButton(onClick = { showActionMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More actions"
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showActionMenu,
                        onDismissRequest = { showActionMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showEditDialog = true
                                showActionMenu = false
                            }
                        )
                        if (task.isArchived) {
                            DropdownMenuItem(
                                text = { Text("Unarchive") },
                                onClick = {
                                    viewModel.updateTask(task.copy(
                                        isArchived = false,
                                        collectionId = task.originalCollectionId
                                    ))
                                    showActionMenu = false
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Archive") },
                                onClick = {
                                    viewModel.updateTask(task.copy(
                                        isArchived = true,
                                        originalCollectionId = task.collectionId,
                                        collectionId = null
                                    ))
                                    showActionMenu = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                viewModel.deleteTask(task)
                                showActionMenu = false
                            }
                        )
                    }
                }
            }
            
            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = collectionColor?.let { Color(it).copy(alpha = 0.7f) } ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                task.dueDate?.let { date ->
                    Column {
                        Text(
                            text = "Due: ${dateFormat.format(date)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = collectionColor?.let { Color(it).copy(alpha = 0.7f) } ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = daysUntilDeadline ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                daysUntilDeadline == "Overdue" -> MaterialTheme.colorScheme.error
                                daysUntilDeadline == "Due today" -> MaterialTheme.colorScheme.tertiary
                                else -> collectionColor?.let { Color(it).copy(alpha = 0.7f) } ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            }
                        )
                    }
                }
                
                Text(
                    text = task.priority.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (task.priority) {
                        Priority.HIGH -> MaterialTheme.colorScheme.error
                        Priority.NORMAL -> collectionColor?.let { Color(it) } ?: MaterialTheme.colorScheme.primary
                        Priority.LOW -> MaterialTheme.colorScheme.tertiary
                    }
                )
            }
        }
    }
} 