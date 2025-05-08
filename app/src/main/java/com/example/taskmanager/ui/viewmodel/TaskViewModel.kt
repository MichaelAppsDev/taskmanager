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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flow

data class TaskUiState(
    val collections: List<Collection> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TaskDatabase.getDatabase(application)
    private val repository = TaskRepository(database)
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val userId = auth.currentUser?.uid ?: return@launch
                
                repository.getAllCollectionsFlow(userId)
                    .catch { e ->
                        _uiState.value = _uiState.value.copy(
                            error = "Failed to load collections: ${e.message}",
                            isLoading = false
                        )
                    }
                    .collect { collections ->
                        _uiState.value = _uiState.value.copy(
                            collections = collections,
                            isLoading = false
                        )
                    }

                repository.getAllTasksFlow(userId)
                    .catch { e ->
                        _uiState.value = _uiState.value.copy(
                            error = "Failed to load tasks: ${e.message}",
                            isLoading = false
                        )
                    }
                    .collect { tasks ->
                        _uiState.value = _uiState.value.copy(
                            tasks = tasks,
                            isLoading = false
                        )
                    }

                repository.getAllRemindersFlow(userId)
                    .catch { e ->
                        _uiState.value = _uiState.value.copy(
                            error = "Failed to load reminders: ${e.message}",
                            isLoading = false
                        )
                    }
                    .collect { reminders ->
                        _uiState.value = _uiState.value.copy(
                            reminders = reminders,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load data: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun getCollection(collectionId: String) = flow {
        try {
            repository.getCollectionByIdFlow(collectionId)
                .catch { e ->
                    emit(null)
                }
                .collect { collection ->
                    emit(collection)
                }
        } catch (e: Exception) {
            emit(null)
        }
    }

    fun getTasksForCollection(collectionId: String) = flow {
        try {
            repository.getTasksByCollectionIdFlow(collectionId)
                .catch { e ->
                    emit(emptyList())
                }
                .collect { tasks ->
                    emit(tasks)
                }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun addCollection(collection: Collection) {
        viewModelScope.launch {
            try {
                repository.addCollection(collection)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to add collection: ${e.message}")
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.addTask(task)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to add task: ${e.message}")
            }
        }
    }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                repository.addReminder(reminder)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to add reminder: ${e.message}")
            }
        }
    }

    fun toggleFavoriteCollection(collectionId: String) {
        viewModelScope.launch {
            try {
                val collection = _uiState.value.collections.find { it.id == collectionId }
                collection?.let {
                    repository.updateCollection(it.copy(isFavorite = !it.isFavorite))
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to update collection: ${e.message}")
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.updateTask(task)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to update task: ${e.message}")
            }
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                repository.updateReminder(reminder)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to update reminder: ${e.message}")
            }
        }
    }

    fun deleteCollection(collectionId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCollection(collectionId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to delete collection: ${e.message}")
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                repository.deleteTask(taskId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to delete task: ${e.message}")
            }
        }
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            try {
                repository.deleteReminder(reminderId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to delete reminder: ${e.message}")
            }
        }
    }
} 