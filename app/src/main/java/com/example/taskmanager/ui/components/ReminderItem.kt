package com.example.taskmanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.taskmanager.data.models.Reminder
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderItem(
    reminder: Reminder,
    collectionColor: Int? = null
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = collectionColor?.let { Color(it).copy(alpha = 0.1f) } ?: MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = reminder.title,
                style = MaterialTheme.typography.titleMedium,
                color = collectionColor?.let { Color(it) } ?: MaterialTheme.colorScheme.onSurface
            )
            
            if (reminder.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reminder.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = collectionColor?.let { Color(it).copy(alpha = 0.7f) } ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Reminder: ${dateFormat.format(reminder.date)}",
                style = MaterialTheme.typography.bodySmall,
                color = collectionColor?.let { Color(it).copy(alpha = 0.7f) } ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
} 