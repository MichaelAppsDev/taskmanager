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
                        color = when (collection.name) {
                            "School" -> SchoolColor
                            "Work" -> WorkColor
                            "Shopping" -> ShoppingColor
                            else -> MaterialTheme.colorScheme.primary
                        }
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
                    text = "Tasks: 0 | Completed: 0 | All: 0",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
} 