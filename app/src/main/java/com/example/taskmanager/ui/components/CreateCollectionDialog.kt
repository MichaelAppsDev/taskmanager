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

@Composable
fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (Collection) -> Unit,
    userId: String
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf<Color?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }

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
                Text("Theme Color (Optional)")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ColorOption(Color(0xFFE57373)) { selectedColor = it }
                    ColorOption(Color(0xFF81C784)) { selectedColor = it }
                    ColorOption(Color(0xFF64B5F6)) { selectedColor = it }
                    ColorOption(Color(0xFFFFB74D)) { selectedColor = it }
                    ColorOption(Color(0xFFBA68C8)) { selectedColor = it }
                }

                // Image Selection (Placeholder for now)
                Button(
                    onClick = { /* TODO: Implement image picker */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Banner Image (Optional)")
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
                                color = selectedColor?.toArgb(),
                                imageUrl = imageUrl
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
    onSelect: (Color) -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp)
            .clickable { onSelect(color) }
    ) {
        Surface(
            color = color,
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            shape = MaterialTheme.shapes.small
        ) {}
    }
} 