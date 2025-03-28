package com.kote.taskifyapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kote.taskifyapp.di.AppModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Rule

@HiltAndroidTest
@UninstallModules(AppModule::class)
class BehaviorUiTests {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var composeRule = createAndroidComposeRule<MainActivity>()
}