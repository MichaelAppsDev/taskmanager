package com.example.taskmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanager.data.models.Collection
import com.example.taskmanager.data.models.TaskStatus
import com.example.taskmanager.ui.theme.SchoolColor
import com.example.taskmanager.ui.theme.ShoppingColor
import com.example.taskmanager.ui.theme.WorkColor
import com.example.taskmanager.ui.viewmodel.TaskViewModel

@Composable
fun CollectionCard(
    collection: Collection,
    viewModel: TaskViewModel,
    onClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tasks = uiState.tasks.filter { it.collectionId == collection.id }
    val completedTasks = tasks.count { it.status == TaskStatus.COMPLETED }
    val uncompletedTasks = tasks.count { it.status == TaskStatus.UNCOMPLETED }
    val outdatedTasks = tasks.count { it.status == TaskStatus.OUTDATED }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background color
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = collection.color?.let { Color(it) } ?: MaterialTheme.colorScheme.primary
                    )
            )
            
            // Collection info
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = collection.name,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Favorite",
                        tint = if (collection.isFavorite) Color(0xFFFFD700) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                viewModel.toggleFavoriteCollection(collection.id)
                            }
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Task statistics
                Text(
                    text = "Tasks: $uncompletedTasks | Completed: $completedTasks | Outdated: $outdatedTasks",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
} 