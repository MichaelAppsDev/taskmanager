package com.example.taskmanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.local.TaskDatabase
import com.example.taskmanager.data.models.Collection
import com.example.taskmanager.data.models.Task
import com.example.taskmanager.data.models.Reminder
import com.example.taskmanager.data.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TaskDatabase.getDatabase(application)
    private val repository = TaskRepository(
        collectionDao = database.collectionDao(),
        taskDao = database.taskDao(),
        reminderDao = database.reminderDao()
    )
    
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    init {
        loadUserData()
    }
    
    private fun loadUserData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            // Load collections
            val collections = repository.getAllCollections(userId)
            _uiState.value = _uiState.value.copy(collections = collections)
            
            // Load tasks
            val tasks = repository.getAllTasksFlow(userId).collect { tasks ->
                _uiState.value = _uiState.value.copy(tasks = tasks)
            }
            
            // Load reminders
            val reminders = repository.getAllReminders(userId)
            _uiState.value = _uiState.value.copy(reminders = reminders)
        }
    }
    
    fun addCollection(name: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val collection = Collection(
                name = name,
                userId = userId
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
    
    fun addTask(title: String, description: String, collectionId: String, dueDate: java.util.Date?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                collectionId = collectionId,
                userId = userId,
                dueDate = dueDate
            )
            repository.addTask(task)
            loadUserData()
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
}

data class TaskUiState(
    val collections: List<Collection> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
    val currentDateTasks: List<Task> = emptyList(),
    val unreadRemindersCount: Int = 0
) 