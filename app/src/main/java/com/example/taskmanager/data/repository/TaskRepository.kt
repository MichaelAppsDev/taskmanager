package com.example.taskmanager.data.repository

import com.example.taskmanager.data.local.TaskDatabase
import com.example.taskmanager.data.local.CollectionDao
import com.example.taskmanager.data.local.TaskDao
import com.example.taskmanager.data.local.ReminderDao
import com.example.taskmanager.data.models.Collection
import com.example.taskmanager.data.models.Task
import com.example.taskmanager.data.models.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(private val database: TaskDatabase) {
    private val collectionDao: CollectionDao = database.collectionDao()
    private val taskDao: TaskDao = database.taskDao()
    private val reminderDao: ReminderDao = database.reminderDao()

    fun getAllCollectionsFlow(userId: String): Flow<List<Collection>> = flow {
        try {
            withContext(Dispatchers.IO) {
                collectionDao.getAllCollections(userId).collect { collections ->
                    emit(collections)
                }
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getCollectionByIdFlow(collectionId: String): Flow<Collection?> = flow {
        try {
            withContext(Dispatchers.IO) {
                collectionDao.getCollectionByIdFlow(collectionId).collect { collection ->
                    emit(collection)
                }
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    fun getTasksByCollectionIdFlow(collectionId: String): Flow<List<Task>> = flow {
        try {
            withContext(Dispatchers.IO) {
                taskDao.getTasksByCollectionIdFlow(collectionId).collect { tasks ->
                    emit(tasks)
                }
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getAllTasksFlow(userId: String): Flow<List<Task>> = flow {
        try {
            withContext(Dispatchers.IO) {
                taskDao.getAllTasksFlow(userId).collect { tasks ->
                    emit(tasks)
                }
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getAllRemindersFlow(userId: String): Flow<List<Reminder>> = flow {
        try {
            withContext(Dispatchers.IO) {
                reminderDao.getAllRemindersFlow(userId).collect { reminders ->
                    emit(reminders)
                }
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun addCollection(collection: Collection) {
        withContext(Dispatchers.IO) {
            try {
                collectionDao.insert(collection)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun updateCollection(collection: Collection) {
        withContext(Dispatchers.IO) {
            try {
                collectionDao.update(collection)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun deleteCollection(collectionId: String) {
        withContext(Dispatchers.IO) {
            try {
                collectionDao.deleteById(collectionId)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun addTask(task: Task) {
        withContext(Dispatchers.IO) {
            try {
                taskDao.insert(task)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun updateTask(task: Task) {
        withContext(Dispatchers.IO) {
            try {
                taskDao.update(task)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun deleteTask(taskId: String) {
        withContext(Dispatchers.IO) {
            try {
                taskDao.deleteById(taskId)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun addReminder(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            try {
                reminderDao.insert(reminder)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun updateReminder(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            try {
                reminderDao.update(reminder)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun deleteReminder(reminderId: String) {
        withContext(Dispatchers.IO) {
            try {
                reminderDao.deleteById(reminderId)
            } catch (e: Exception) {
                throw e
            }
        }
    }
} 