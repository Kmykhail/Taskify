package com.kote.taskifyapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.kote.taskifyapp.data.ReminderType
import com.kote.taskifyapp.data.TaskDatabase
import com.kote.taskifyapp.data.repository.TaskRepository
import com.kote.taskifyapp.data.repository.WorkManagerRepository
import com.kote.taskifyapp.util.calculateReminderTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class BootReceiver: BroadcastReceiver() {
    private lateinit var workManagerRepository: WorkManagerRepository
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("BootReceiver", "onReceive")
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            workManagerRepository = WorkManagerRepository(context)
            restoreActiveNotifications(context)
            workManagerRepository.scheduleDailyCheckAlarm(true)
            Log.d("BootReceiver", "AlarmManager scheduling restored!")
        }
    }

    private fun restoreActiveNotifications(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val taskRepository = TaskRepository(TaskDatabase.getDataBase(context).taskDao())
            val dateInMillis: Long = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val plannedTasks = taskRepository.allTask.first().filter {
                !it.isCompleted &&
                it.reminderType == ReminderType.OnTime &&
                it.date != null && it.date >= dateInMillis &&
                it.time != null
            }
            for (task in plannedTasks) {
                workManagerRepository.scheduleAlarmNotification(
                    task.id,
                    task.title,
                    task.description,
                    calculateReminderTime(task.date!!, task.time!!)
                )
            }
        }
    }
}