package com.kote.taskifyapp.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.jvm.Throws


@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var database: TaskDatabase
    private lateinit var taskDao: TaskDao

    private val task1 = Task(
        id = 1,
        title = "Test1",
        description = "Description for Test1 task",
        date = 123L,
        priority = Priority.Low,
    )

    private val task2 = Task(
        id = 2,
        title = "Test2",
        description = "Description for Test2 task",
        date = 456L,
        priority = Priority.Low,
    )

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()

        database = Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        taskDao =  database.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    @Throws(IOException::class)
    fun daoInsert_insertTaskIntoDb() = runBlocking {
        addTaskOnce()
        val allTasks = taskDao.getAllTasksAsc().first()
        assertEquals(allTasks[0], task1)
    }

    @Test
    @Throws(IOException::class)
    fun daoGetTask_returnsAllTaskFromDb() = runBlocking {
        addTwoTasks()
        val allTasks = taskDao.getAllTasksAsc().first()
        assertEquals(allTasks[0], task1)
        assertEquals(allTasks[1], task2)
    }

    @Test
    @Throws(IOException::class)
    fun daoGetTaskById_returnsOneTaskFromDb() = runBlocking {
        addTaskOnce()
        val task = taskDao.getTaskById(1)
        assertEquals(task!!.id, 1)
    }


    @Test
    @Throws(IOException::class)
    fun daoUpdate_updateTasksInDb() = runBlocking {
        val task1Descr = "New description for Test1 task"
        val task2Descr = "New description for Test2 task"

        addTwoTasks()
        taskDao.updateTask(task1.copy(description = task1Descr))
        taskDao.updateTask(task2.copy(description = task2Descr))
        val allTasks = taskDao.getAllTasksAsc().first()

        assertEquals(allTasks[0].description, task1Descr)
        assertEquals(allTasks[1].description, task2Descr)
    }

    @Test
    @Throws
    fun daoDelete_deleteTasksFromDb() = runBlocking {
        addTwoTasks()

        taskDao.deleteTask(task1)
        taskDao.deleteTask(task2)

        assertTrue(taskDao.getAllTasksAsc().first().isEmpty())
    }

    private suspend fun addTaskOnce() {
        taskDao.insertTask(task1)
    }

    private suspend fun addTwoTasks() {
        taskDao.insertTask(task1)
        taskDao.insertTask(task2)
    }
}