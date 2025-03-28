package com.kote.taskifyapp.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kote.taskifyapp.util.ACTIVE_NOTIFICATION_ALARM
import com.kote.taskifyapp.util.CLEANUP
import com.kote.taskifyapp.util.DELAY_FOR_DELETE
import com.kote.taskifyapp.util.KEY_DESCRIPTION
import com.kote.taskifyapp.util.KEY_TITLE
import com.kote.taskifyapp.util.OVERDUE_NOTIFICATION_ALARM
import com.kote.taskifyapp.util.TASK_ID
import com.kote.taskifyapp.ReminderReceiver
import com.kote.taskifyapp.data.worker.CompletedTaskWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Alarm notification start
    fun scheduleAlarmNotification(
        id: Int,
        title: String?,
        description: String?,
        duration: Long
    ) {
        if (duration - System.currentTimeMillis() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.d("Debug", "Manifest.permission.SCHEDULE_EXACT_ALARM is Denied")
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                }
            }

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra(TASK_ID, id)
                putExtra(KEY_TITLE, title)
                putExtra(KEY_DESCRIPTION, description)
                action = ACTIVE_NOTIFICATION_ALARM
            }
            val pendingIntent = PendingIntent.getBroadcast(context, id, intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                duration,
                pendingIntent
            )
            Log.d("Debug", "Alarm set for task:$id, title:$title at $duration")
        }
    }

    fun cancelAlarmNotification(id: Int, title: String?) {
        val intent = Intent(context, ReminderReceiver::class.java).apply { action = ACTIVE_NOTIFICATION_ALARM }
        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
        Log.d("Debug", "Cancel alarm notification for task id:$id, title:$title")
    }
    // Alarm notification end

    // Daily check overdue tasks start
    fun scheduleDailyCheckAlarm(manualReschedule: Boolean = false) {
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                action = OVERDUE_NOTIFICATION_ALARM
            }
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 10) // 10:00
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            Log.d("Debug", "Daily checking for overdue tasks is scheduled")
    }

    fun cancelDailyCheckAlarm() {
        val intent = Intent(context, ReminderReceiver::class.java).apply { action = OVERDUE_NOTIFICATION_ALARM }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        pendingIntent?.let {
            alarmManager.cancel(pendingIntent)
        }
        Log.d("Debug", "Cancel daily check")
    }

    fun isDailyCheckAlarmSet(): Boolean {
        val intent = Intent(context, ReminderReceiver::class.java).apply { action = OVERDUE_NOTIFICATION_ALARM }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        return pendingIntent != null
    }
    // Daily check overdue tasks end

    // Cleanup start
    fun scheduleCompletedTask(id: Int) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val cleanupRequest = OneTimeWorkRequestBuilder<CompletedTaskWorker>()
            .setConstraints(constraints)
            .setInputData(Data.Builder().putString(TASK_ID, id.toString()).build())
            .setInitialDelay(DELAY_FOR_DELETE, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniqueWork(
            id.toString() + CLEANUP,
            ExistingWorkPolicy.REPLACE,
            cleanupRequest
        )
        Log.d("Debug", "Scheduled deletion for task id: $id, time: ${System.currentTimeMillis() + DELAY_FOR_DELETE}")
    }

    fun cancelCompletedTask(id: Int) {
        workManager.cancelUniqueWork(id.toString() + CLEANUP)
        Log.d("Debug", "Cancel deletion for task id: $id")
    }
    // Cleanup end
}


