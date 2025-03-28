package com.kote.taskifyapp.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.kote.taskifyapp.MainActivity
import com.kote.taskifyapp.R
import com.kote.taskifyapp.di.AppModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class HomeScreenTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun dummyTest() {
        // Appears navigation bar(Home, Calendar, Settings)
        composeRule.onNodeWithContentDescription(
            composeRule.activity.getString(R.string.content_desc_home_btn)
        ).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(
            composeRule.activity.getString(R.string.content_desc_home_btn)
        ).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(
            composeRule.activity.getString(R.string.content_desc_settings_btn)
        ).assertIsDisplayed()
    }

    @Test
    fun createTask() {
        composeRule.onNodeWithContentDescription("Add task").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("Task layout").assertExists()
    }
}