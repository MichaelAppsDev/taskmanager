package com.example.taskmanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.local.TaskDatabase
import com.example.taskmanager.data.models.Collection
import com.example.taskmanager.data.models.Task
import com.example.taskmanager.data.models.Reminder
import com.example.taskmanager.data.models.Priority
import com.example.taskmanager.data.models.TaskStatus
import com.example.taskmanager.data.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.*

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TaskDatabase.getDatabase(application)
    private val repository = TaskRepository(
        collectionDao = database.collectionDao(),
        taskDao = database.taskDao(),
        reminderDao = database.reminderDao()
    )
    
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    // Track if we've already started loading data
    private var isDataLoaded = false
    
    private val notificationCheckJob = viewModelScope.launch {
        while (true) {
            checkTaskDeadlines()
            delay(60000) // Check every minute
        }
    }
    
    init {
        // Wait for auth to be ready before loading data
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            if (auth.currentUser != null && !isDataLoaded) {
                loadUserData()
                isDataLoaded = true
            }
        }
    }
    
    private fun loadUserData() {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            
            // Load collections
            viewModelScope.launch {
                try {
                    val collections = repository.getAllCollections(userId)
                    _uiState.value = _uiState.value.copy(collections = collections)
                } catch (e: Exception) {
                    android.util.Log.e("TaskViewModel", "Error loading collections", e)
                }
            }
            
            // Load tasks in a separate coroutine
            viewModelScope.launch {
                try {
                    repository.getAllTasksFlow(userId)
                        .catch { e -> 
                            android.util.Log.e("TaskViewModel", "Error in tasks flow", e)
                        }
                        .collect { tasks ->
                            android.util.Log.d("TaskViewModel", "Loaded ${tasks.size} tasks")
                            _uiState.value = _uiState.value.copy(tasks = tasks)
                        }
                } catch (e: Exception) {
                    android.util.Log.e("TaskViewModel", "Error loading tasks", e)
                }
            }
            
            // Load reminders in a separate coroutine
            viewModelScope.launch {
                try {
                    val reminders = repository.getAllReminders(userId)
                    _uiState.value = _uiState.value.copy(reminders = reminders)
                } catch (e: Exception) {
                    android.util.Log.e("TaskViewModel", "Error loading reminders", e)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("TaskViewModel", "Error in loadUserData", e)
        }
    }
    
    private suspend fun checkTaskDeadlines() {
        val currentTime = Calendar.getInstance().time
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val tasks = repository.getAllTasksFlow(userId).first()
        
        tasks.forEach { task ->
            task.dueDate?.let { dueDate ->
                val timeDiff = dueDate.time - currentTime.time
                val hoursUntilDeadline = timeDiff / (1000.0 * 60 * 60)
                
                // Update task status if overdue
                if (timeDiff < 0 && task.status == TaskStatus.UNCOMPLETED) {
                    viewModelScope.launch {
                        updateTask(task.copy(status = TaskStatus.OUTDATED))
                        createDeadlineNotification(task, true)
                    }
                }
                // Create notification for approaching deadline (24 hours before)
                else if (hoursUntilDeadline in 1.0..24.0 && task.status == TaskStatus.UNCOMPLETED) {
                    viewModelScope.launch {
                        createDeadlineNotification(task, false)
                    }
                }
            }
        }
    }
    
    private suspend fun createDeadlineNotification(task: Task, isOverdue: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val title = if (isOverdue) "Task Overdue: ${task.title}" else "Deadline Approaching: ${task.title}"
        val description = if (isOverdue) {
            "The task '${task.title}' is now overdue. Please complete it as soon as possible."
        } else {
            "The task '${task.title}' is due in less than 24 hours. Don't forget to complete it!"
        }
        
        val reminder = Reminder(
            title = title,
            description = description,
            taskId = task.id,
            userId = userId,
            date = Calendar.getInstance().time,
            isRead = false
        )
        
        repository.addReminder(reminder)
        loadUserData()
    }
    
    fun addCollection(name: String, description: String? = null, color: Int? = null) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val collection = Collection(
                name = name,
                userId = userId,
                description = description,
                color = color
            )
            repository.addCollection(collection)
            loadUserData()
        }
    }
    
    fun updateCollection(collection: Collection) {
        viewModelScope.launch {
            repository.updateCollection(collection)
            loadUserData()
        }
    }
    
    fun deleteCollection(collection: Collection) {
        viewModelScope.launch {
            repository.deleteCollection(collection)
            loadUserData()
        }
    }
    
    fun toggleFavoriteCollection(collectionId: String) {
        viewModelScope.launch {
            val collection = repository.getCollectionById(collectionId)
            collection?.let {
                repository.updateCollection(it.copy(isFavorite = !it.isFavorite))
                loadUserData()
            }
        }
    }
    
    fun addTask(title: String, description: String, collectionId: String, dueDate: java.util.Date?, priority: Priority = Priority.NORMAL, status: TaskStatus = TaskStatus.UNCOMPLETED) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            android.util.Log.e("TaskViewModel", "User not authenticated")
            return
        }
        viewModelScope.launch {
            try {
                val task = Task(
                    title = title,
                    description = description,
                    collectionId = collectionId,
                    userId = userId,
                    dueDate = dueDate,
                    priority = priority,
                    status = status
                )
                repository.addTask(task)
                android.util.Log.d("TaskViewModel", "Task added successfully: ${task.id}")
                loadUserData()
            } catch (e: Exception) {
                android.util.Log.e("TaskViewModel", "Error adding task", e)
            }
        }
    }
    
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
            loadUserData()
        }
    }
    
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            loadUserData()
        }
    }
    
    fun addReminder(title: String, description: String, taskId: String, date: java.util.Date) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val reminder = Reminder(
                title = title,
                description = description,
                taskId = taskId,
                userId = userId,
                date = date
            )
            repository.addReminder(reminder)
            loadUserData()
        }
    }
    
    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.updateReminder(reminder)
            loadUserData()
        }
    }
    
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
            loadUserData()
        }
    }
    
    fun addStandaloneReminder(title: String, description: String, date: java.util.Date) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val reminder = Reminder(
                title = title,
                description = description,
                taskId = "", // Empty string indicates standalone reminder
                userId = userId,
                date = date
            )
            repository.addReminder(reminder)
            loadUserData()
        }
    }
}

data class TaskUiState(
    val collections: List<Collection> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
    val currentDateTasks: List<Task> = emptyList(),
    val unreadRemindersCount: Int = 0
) 