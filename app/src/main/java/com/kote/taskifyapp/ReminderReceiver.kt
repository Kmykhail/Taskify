package com.kote.taskifyapp

import android.content.Context
import android.content.Intent
import android.content.BroadcastReceiver
import android.util.Log
import com.kote.taskifyapp.data.TaskNotification

class ReminderReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val taskId = intent?.getIntExtra(TASK_ID, -1) ?: -1
        val title = intent?.getStringExtra(KEY_TITLE) ?: ""
        val description = intent?.getStringExtra(KEY_DESCRIPTION) ?: ""
        Log.d("Debug", "ReminderReceiver for task id:$taskId, title: $title, description: $description")

        TaskNotification(context).showNotification(taskId, title, description)
    }
}