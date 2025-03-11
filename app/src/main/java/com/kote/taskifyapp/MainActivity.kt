package com.kote.taskifyapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kote.taskifyapp.data.repository.UserPreferencesRepository
import com.kote.taskifyapp.ui.navigation.TaskifyNavGraph
import com.kote.taskifyapp.ui.settings.LocaleHelper
import com.kote.taskifyapp.ui.theme.TaskifyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val storedLanguage = runBlocking {userPreferencesRepository.languageFlow.first()}
        LocaleHelper.updateLocale(this, storedLanguage)

        enableEdgeToEdge()
        setContent {
            TaskifyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    TaskifyNavGraph(
                        navController = rememberNavController(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
