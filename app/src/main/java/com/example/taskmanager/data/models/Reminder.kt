package com.example.taskmanager.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.taskmanager.data.local.converters.DateTimeConverters
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "reminders")
@TypeConverters(DateTimeConverters::class)
data class Reminder(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val taskId: String,
    val userId: String,
    val title: String,
    val description: String,
    val date: LocalDateTime,
    val isRead: Boolean = false
) 