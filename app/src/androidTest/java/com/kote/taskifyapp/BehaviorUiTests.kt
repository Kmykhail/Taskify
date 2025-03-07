package com.kote.taskifyapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.kote.taskifyapp.di.AppModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class BehaviorUiTests {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var composeRule = createAndroidComposeRule<MainActivity>()

    private val taskTitle1 = "Test title"
    private val taskDescription1 = "Test description"

    private val taskTitle2 = "New test title"
    private val taskDescription2 = "New test description"

    @Test
    fun createTask() {
        composeRule.onNodeWithContentDescription("Add task").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("Task layout").assertExists()
        composeRule.onNodeWithContentDescription("Task title").performTextInput(taskTitle1)
        composeRule.onNodeWithContentDescription("Task description").performTextInput(taskDescription1)
        composeRule.onNodeWithContentDescription("Create/update task").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(taskTitle1).assertIsDisplayed()
    }

    @Test
    fun updateTask() {
        createTask()
        composeRule.onNodeWithText(taskTitle1).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("Task title").performTextReplacement(taskTitle2)
        composeRule.onNodeWithContentDescription("Task description").performTextReplacement(taskDescription2)
        composeRule.onNodeWithContentDescription("Create/update task").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(taskTitle2).assertIsDisplayed()
    }

    @Test
    fun taskCompletion() {
        createTask()
        composeRule.onNodeWithContentDescription("Checkbox").performClick()
        composeRule.waitForIdle()

        composeRule.onAllNodesWithText("Completed").onFirst().performScrollTo().assertIsDisplayed()
    }
}