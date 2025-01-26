package com.kote.taskifyapp.data

import androidx.room.Entity
import androidx.room.Ignore
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
    val isCompleted: Boolean = false,
    val isCreated: Boolean = false
) {
    constructor(
        id: Int,
        title: String?,
        description: String?,
        date: Long?,
        time: Int?,
        priority: Priority,
        tag: List<String>,
        isFavorite: Boolean,
        isCreated: Boolean
    ) : this(id, title, description, date, time, priority, tag, isFavorite, isCreated, false)
}