package com.example.taskmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskmanager.data.models.Task
import com.example.taskmanager.ui.components.TaskItem
import com.example.taskmanager.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveCollectionScreen(viewModel: TaskViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val archivedTasks = uiState.tasks.filter { it.isArchived }
    val collections = uiState.collections

    LaunchedEffect(uiState.tasks) {
        android.util.Log.d("ArchiveScreen", "Total tasks: ${uiState.tasks.size}")
        android.util.Log.d("ArchiveScreen", "Archived tasks: ${archivedTasks.size}")
        archivedTasks.forEach { task ->
            android.util.Log.d("ArchiveScreen", "Archived task: ${task.title}, isArchived: ${task.isArchived}, collectionId: ${task.collectionId}, originalCollectionId: ${task.originalCollectionId}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Archive,
                contentDescription = "Archive",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Archive",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (archivedTasks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No archived tasks",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(archivedTasks) { task ->
                    val collection = collections.find { it.id == task.originalCollectionId }
                    TaskItem(
                        task = task,
                        viewModel = viewModel,
                        collectionColor = collection?.color,
                        collectionName = collection?.name
                    )
                }
            }
        }
    }
} 