package com.kote.taskifyapp.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kote.taskifyapp.CLEANUP
import com.kote.taskifyapp.DELAY_FOR_DELETE
import com.kote.taskifyapp.KEY_DESCRIPTION
import com.kote.taskifyapp.KEY_TITLE
import com.kote.taskifyapp.TASK_ID
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

    // Notification start
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
            }
            val pendingIntent = PendingIntent.getBroadcast(context, id, intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, duration, pendingIntent)
            Log.d("Debug", "Alarm set for task $id at $duration")
        }
    }

    fun cancelAlarmNotification(taskId: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
        Log.d("Debug", "Cancel alarm notification for task id: $taskId")
    }
    // Notification end

    // Cleanup start
    fun scheduleCompletedTask(id: Int) {
        Log.d("Debug",
            "WorkManager, `completed task` delay:$DELAY_FOR_DELETE, deletionTime:${System.currentTimeMillis() + DELAY_FOR_DELETE}")

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

        val workInfos = workManager.getWorkInfosForUniqueWork(id.toString() + CLEANUP).get()
        workInfos.forEach { workInfo ->
            Log.d("Debug", "WorkManager, `completed task` state: ${workInfo.state}")
        }
    }

    fun cancelCompletedTask(id: Int) {
        workManager.cancelUniqueWork(id.toString() + CLEANUP)
    }
    // Cleanup end
}


