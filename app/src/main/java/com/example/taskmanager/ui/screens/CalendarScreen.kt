package com.example.taskmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.taskmanager.ui.theme.LightPurple
import com.example.taskmanager.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(viewModel: TaskViewModel) {
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.time) }
    val uiState by viewModel.uiState.collectAsState()
    
    // Format for month and year display
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    var currentMonthYear by remember { mutableStateOf(monthYearFormat.format(selectedDate)) }
    
    // Day of week labels
    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
    
    // Calculate days in month
    val daysInMonthList = getDaysInMonthList(selectedDate)
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Handle add task */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Calendar header with edit button
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightPurple
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Day and date header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(selectedDate),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        IconButton(onClick = { /* Handle edit */ }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit"
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Month selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentMonthYear,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Row {
                            IconButton(
                                onClick = {
                                    val newCal = Calendar.getInstance()
                                    newCal.time = selectedDate
                                    newCal.add(Calendar.MONTH, -1)
                                    selectedDate = newCal.time
                                    currentMonthYear = monthYearFormat.format(selectedDate)
                                }
                            ) {
                                Text("<")
                            }
                            
                            IconButton(
                                onClick = {
                                    val newCal = Calendar.getInstance()
                                    newCal.time = selectedDate
                                    newCal.add(Calendar.MONTH, 1)
                                    selectedDate = newCal.time
                                    currentMonthYear = monthYearFormat.format(selectedDate)
                                }
                            ) {
                                Text(">")
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Calendar grid with days of week
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysOfWeek.forEach { day ->
                            Text(
                                text = day,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Calendar grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(daysInMonthList) { dayInfo ->
                            CalendarDay(
                                day = dayInfo.day,
                                isCurrentMonth = dayInfo.isCurrentMonth,
                                isSelected = dayInfo.isSelected,
                                onClick = {
                                    selectedDate = dayInfo.date
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tasks for selected date
            TaskList(
                date = selectedDate,
                tasks = uiState.tasks.filter { task ->
                    task.deadline?.toLocalDate()?.equals(selectedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()) == true
                }
            )
        }
    }
}

@Composable
fun CalendarDay(
    day: Int,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> Color.Transparent
                }
            )
            .clickable(isCurrentMonth) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (day > 0) {
            Text(
                text = day.toString(),
                color = when {
                    isSelected -> Color.White
                    !isCurrentMonth -> Color.Gray
                    else -> Color.Black
                },
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun TaskList(date: Date, tasks: List<com.example.taskmanager.data.models.Task>) {
    Column {
        Text(
            text = "Task List",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("no tasks listed...")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Task items will be added here
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Progress",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.0f) // Progress percentage
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        
        Text(
            text = "0%",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// Helper data class for calendar days
data class CalendarDayInfo(
    val day: Int,
    val date: Date,
    val isCurrentMonth: Boolean,
    val isSelected: Boolean
)

// Helper function to get all days in the current month view
fun getDaysInMonthList(selectedDate: Date): List<CalendarDayInfo> {
    val calendar = Calendar.getInstance()
    calendar.time = selectedDate
    
    // Get current day
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    
    // Move to the first day of the month
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    
    // Get the day of week for the first day (0 = Sunday, 1 = Monday, etc.)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    
    // Move to the first day shown on the calendar (may be in the previous month)
    calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek)
    
    // Get the days to display (6 weeks = 42 days)
    val days = mutableListOf<CalendarDayInfo>()
    val currentMonth = selectedDate.month
    
    repeat(42) {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val isCurrentMonth = calendar.get(Calendar.MONTH) == currentMonth
        val isSelected = isCurrentMonth && day == currentDay
        
        days.add(
            CalendarDayInfo(
                day = day,
                date = calendar.time,
                isCurrentMonth = isCurrentMonth,
                isSelected = isSelected
            )
        )
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    
    return days
} 