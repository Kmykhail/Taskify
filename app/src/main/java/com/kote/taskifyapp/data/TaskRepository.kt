package com.kote.taskifyapp.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun getTaskById(taskId: Int): Task? = taskDao.getTaskById(taskId)
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}