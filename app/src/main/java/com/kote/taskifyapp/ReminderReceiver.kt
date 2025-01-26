package com.kote.taskifyapp

import android.content.Context
import android.content.Intent
import android.content.BroadcastReceiver
import android.util.Log
import com.kote.taskifyapp.data.TaskDatabase
import com.kote.taskifyapp.data.TaskNotification
import com.kote.taskifyapp.data.repository.TaskRepository
import com.kote.taskifyapp.data.repository.WorkManagerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class ReminderReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTIVE_NOTIFICATION_ALARM -> handleActiveNotification(context, intent)
            OVERDUE_NOTIFICATION_ALARM -> handleOverdueNotifications(context, intent)
        }
    }

    private fun handleActiveNotification(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra(TASK_ID, -1)
        val title = intent.getStringExtra(KEY_TITLE) ?: ""
        val description = intent.getStringExtra(KEY_DESCRIPTION) ?: ""
        Log.d("ReminderReceiver", "ActiveNotification, reminder for task id:$taskId, title: $title, description: $description")
        TaskNotification(context).showNotification(taskId, title, description)
    }

    private fun handleOverdueNotifications(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val dateInMillis: Long = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val taskRepository = TaskRepository(TaskDatabase.getDataBase(context).taskDao())
            val tasksByReminder = taskRepository.getOverdueTasks(dateInMillis)
            Log.d("ReminderReceiver", "OverdueNotifications, running daily task check, dateInMillis: $dateInMillis, tasksByReminder size: ${tasksByReminder.size}")

            if (tasksByReminder.isNotEmpty()) {
                val taskNotification = TaskNotification(context)
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
                        "You have ${tasksByReminder.size} overdue tasks!"
                    )
                }
            }
            WorkManagerRepository(context).scheduleDailyCheckAlarm()
        }
    }
}