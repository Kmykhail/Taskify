package com.kote.taskifyapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    @Query("SELECT * FROM tasks WHERE reminderType = 1 and isCompleted = 0 and date IS NOT NULL and date < :currentDate")
    suspend fun getOutdatedTasks(currentDate: Long) : List<Task>

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasksDesc() : Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY date ASC")
    fun getAllTasksAsc(): Flow<List<Task>>

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id IN (:taskIds)")
    suspend fun deleteSpecificTasks(taskIds: Set<Int>)
}