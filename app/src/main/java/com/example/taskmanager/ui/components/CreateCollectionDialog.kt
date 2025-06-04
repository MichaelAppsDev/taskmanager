package com.example.taskmanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.taskmanager.data.models.Collection
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape

@Composable
fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (Collection) -> Unit,
    userId: String
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf<Color?>(null) }

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
                    text = "Create New Collection",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Collection Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                // Color Selection
                Text("Theme Color", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ColorOption(Color(0xFFE57373), selectedColor) { selectedColor = it }
                    ColorOption(Color(0xFF81C784), selectedColor) { selectedColor = it }
                    ColorOption(Color(0xFF64B5F6), selectedColor) { selectedColor = it }
                    ColorOption(Color(0xFFFFB74D), selectedColor) { selectedColor = it }
                    ColorOption(Color(0xFFBA68C8), selectedColor) { selectedColor = it }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val collection = Collection(
                                name = name,
                                userId = userId,
                                description = description.takeIf { it.isNotBlank() },
                                color = selectedColor?.toArgb()
                            )
                            onConfirm(collection)
                        },
                        enabled = name.isNotBlank()
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorOption(
    color: Color,
    selectedColor: Color?,
    onSelect: (Color) -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp)
            .border(
                width = 2.dp,
                color = if (color == selectedColor) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onSelect(color) }
    ) {
        Surface(
            color = color,
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            shape = CircleShape
        ) {}
    }
} 