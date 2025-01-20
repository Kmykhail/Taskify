package com.kote.taskifyapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority {
    High, Medium, Low, NoPriority
}

@Entity(tableName = "tasks")
data class Task (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String? = null,
    val description: String? = null,
    val date: Long? = null,
    val time: Int? = null,
    val priority: Priority = Priority.NoPriority,
    val tag: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val isCompleted: Boolean = false
)