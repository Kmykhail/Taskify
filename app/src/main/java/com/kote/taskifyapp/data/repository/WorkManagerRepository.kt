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
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.kote.taskifyapp.CLEANUP
import com.kote.taskifyapp.DAILY_TASK_CHECK
import com.kote.taskifyapp.DELAY_FOR_DELETE
import com.kote.taskifyapp.KEY_DESCRIPTION
import com.kote.taskifyapp.KEY_TITLE
import com.kote.taskifyapp.TASK_ID
import com.kote.taskifyapp.ReminderReceiver
import com.kote.taskifyapp.data.worker.CompletedTaskWorker
import com.kote.taskifyapp.data.worker.TaskCheckWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalTime
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
            }
            val pendingIntent = PendingIntent.getBroadcast(context, id, intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, duration, pendingIntent)
            Log.d("Debug", "Alarm set for task $id at $duration")
        }
    }

    fun cancelAlarmNotification(id: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
        Log.d("Debug", "Cancel alarm notification for task id: $id")
    }
    // Alarm notification end

    // Daily check outdated tasks start
    fun scheduleDailyCheck() {
        val workInfos = workManager.getWorkInfosForUniqueWork(DAILY_TASK_CHECK).get()
        val isAlreadyScheduled = workInfos.any {
            it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING
        }

        if (!isAlreadyScheduled) {
            val now = LocalTime.now()
            val targetTime = LocalTime.of(11, 0)
            val delay = Duration.between(now, targetTime).seconds.let {
                if (it < 0) it + TimeUnit.DAYS.toSeconds(1) else it
            }

            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresDeviceIdle(false)
                .build()

            val dailyCheckRequest = PeriodicWorkRequestBuilder<TaskCheckWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build()

            workManager.enqueueUniquePeriodicWork(
                DAILY_TASK_CHECK,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyCheckRequest
            )
            Log.d("Debug", "Daily checking for outdated tasks is scheduled")
        } else {
            Log.d("Debug", "Daily checking for outdated tasks is in progress")
        }
    }
    // Daily check outdated tasks end

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


