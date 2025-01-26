package com.kote.taskifyapp.data

import com.kote.taskifyapp.data.repository.TaskRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import javax.inject.Inject
import kotlin.jvm.Throws

@HiltAndroidTest
class TaskRepositoryHiltTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var taskRepository: TaskRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

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

    @Test
    @Throws(IOException::class)
    fun testInsertTask() = runBlocking {
        taskRepository.insertTask(task1)

        val fetchedTask = taskRepository.getTaskById(task1.id)
        assertTrue(fetchedTask != null)
        assertEquals(fetchedTask!!.id, task1.id)
    }

    @Test
    @Throws(IOException::class)
    fun testGetAllTask() = runBlocking {
        addTwoTasks()

        val fetchedTasks = taskRepository.getAllTasksAsc().first()
        assertEquals(fetchedTasks.size, 2)
        assertEquals(fetchedTasks[0].id, task1.id)
        assertEquals(fetchedTasks[1].id, task2.id)
    }

    @Test
    @Throws
    fun testUpdateTask() = runBlocking {
        val task1Descr = "New description for Test1 task"
        val task2Descr = "New description for Test2 task"

        addTwoTasks()
        taskRepository.updateTask(task1.copy(description = task1Descr))
        taskRepository.updateTask(task2.copy(description = task2Descr))
        val fetchedTasks = taskRepository.getAllTasksAsc().first()

        assertEquals(fetchedTasks[0].description, task1Descr)
        assertEquals(fetchedTasks[1].description, task2Descr)
    }

    @Test
    @Throws
    fun testDeleteTask() = runBlocking {
        addTwoTasks()

        taskRepository.deleteTask(task1)
        taskRepository.deleteTask(task2)

        assertTrue(taskRepository.getAllTasksAsc().first().isEmpty())
    }

    private suspend fun addTwoTasks() {
        taskRepository.insertTask(task1)
        taskRepository.insertTask(task2)
    }
}