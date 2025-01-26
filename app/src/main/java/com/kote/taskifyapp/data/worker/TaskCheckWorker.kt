package com.kote.taskifyapp.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kote.taskifyapp.OVERDUE_NOTIFICATION_ID
import com.kote.taskifyapp.data.TaskDatabase
import com.kote.taskifyapp.data.TaskNotification
import com.kote.taskifyapp.data.repository.TaskRepository
import java.time.LocalDate
import java.time.ZoneOffset

class TaskCheckWorker(
    private val ctx: Context,
    params: WorkerParameters
): CoroutineWorker(ctx, params) {
    private lateinit var taskNotification: TaskNotification

    override suspend fun doWork(): Result {
        val dateInMillis: Long = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val taskRepository = TaskRepository(TaskDatabase.getDataBase(ctx).taskDao())
        val tasksByReminder = taskRepository.getOutdatedTasks(dateInMillis)
        Log.d("Debug", "Running daily task check, dateInMillis: $dateInMillis, tasksByReminder size: ${tasksByReminder.size}")

        if (tasksByReminder.isNotEmpty()) {
            taskNotification = TaskNotification(ctx)
            if (tasksByReminder.size == 1) {
                taskNotification.showNotification(
                    tasksByReminder.first().id,
                    tasksByReminder.first().title ?: "",
                    tasksByReminder.first().description ?: ""
                )
            } else {
                taskNotification.showNotification(
                    OVERDUE_NOTIFICATION_ID,
                    "Pending Tasks",
                    "You have ${tasksByReminder.size} outdated tasks!"
                )
            }
        }
        return Result.success()
    }
}