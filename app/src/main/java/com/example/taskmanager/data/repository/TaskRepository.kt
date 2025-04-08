package com.example.taskmanager.data.repository

import com.example.taskmanager.data.local.CollectionDao
import com.example.taskmanager.data.local.TaskDao
import com.example.taskmanager.data.local.ReminderDao
import com.example.taskmanager.data.models.Collection
import com.example.taskmanager.data.models.Task
import com.example.taskmanager.data.models.Reminder
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val collectionDao: CollectionDao,
    private val taskDao: TaskDao,
    private val reminderDao: ReminderDao
) {
    // Collections
    suspend fun getAllCollections(userId: String): List<Collection> {
        return collectionDao.getCollectionsByUserId(userId)
    }

    suspend fun getCollectionById(collectionId: String): Collection? {
        return collectionDao.getCollectionById(collectionId)
    }

    suspend fun addCollection(collection: Collection) {
        collectionDao.insertCollection(collection)
    }

    suspend fun updateCollection(collection: Collection) {
        collectionDao.updateCollection(collection)
    }

    suspend fun deleteCollection(collection: Collection) {
        collectionDao.deleteCollection(collection)
    }

    // Tasks
    suspend fun getTasksByCollectionId(collectionId: String): List<Task> {
        return taskDao.getTasksByCollectionId(collectionId)
    }

    suspend fun getTaskById(taskId: String): Task? {
        return taskDao.getTaskById(taskId)
    }

    suspend fun addTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    // Reminders
    suspend fun getAllReminders(userId: String): List<Reminder> {
        return reminderDao.getRemindersByUserId(userId)
    }

    suspend fun getRemindersByTaskId(taskId: String): List<Reminder> {
        return reminderDao.getRemindersByTaskId(taskId)
    }

    suspend fun getReminderById(reminderId: String): Reminder? {
        return reminderDao.getReminderById(reminderId)
    }

    suspend fun addReminder(reminder: Reminder) {
        reminderDao.insertReminder(reminder)
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    // Flow-based queries
    fun getAllCollectionsFlow(userId: String): Flow<List<Collection>> {
        return collectionDao.getAllCollections(userId)
    }

    fun getAllTasksFlow(userId: String): Flow<List<Task>> {
        return taskDao.getAllTasks(userId)
    }

    fun getAllRemindersFlow(userId: String): Flow<List<Reminder>> {
        return reminderDao.getAllReminders(userId)
    }
} 