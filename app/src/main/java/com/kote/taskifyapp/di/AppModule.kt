package com.kote.taskifyapp.di

import android.app.Application
import com.kote.taskifyapp.data.TaskDao
import com.kote.taskifyapp.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(
        app: Application
    ) : TaskDatabase {
        return TaskDatabase.gerDataBase(app.applicationContext)
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: TaskDatabase) : TaskDao {
        return database.taskDao()
    }
}