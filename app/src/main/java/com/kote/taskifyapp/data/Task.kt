package com.kote.taskifyapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority(val value: Int) {
    NoPriority(0),
    Low(1),
    Medium(2),
    High(3)
}

enum class ReminderType(val value: Int) {
    None(0),
    OnTime(1)
}

enum class SortType(val value: Int) {
    Title(0),
    Date(1),
    Priority(2)
}

@Entity(tableName = "tasks")
data class Task (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String? = null,
    val description: String? = null,
    val date: Long? = null,
    val time: Int? = null,
    val reminderType: ReminderType = ReminderType.OnTime,
    val deletionTime: Long? = null,
    val priority: Priority = Priority.NoPriority,
    val tag: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val isCompleted: Boolean = false,
    val isCreated: Boolean = false
)