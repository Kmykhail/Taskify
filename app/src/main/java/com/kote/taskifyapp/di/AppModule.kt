package com.kote.taskifyapp.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kote.taskifyapp.data.TaskDao
import com.kote.taskifyapp.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "preferences"
)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(app: Application) : TaskDatabase {
        return TaskDatabase.getDataBase(app.applicationContext)
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: TaskDatabase) : TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideReminderManagerContext(app: Application) : Context {
        return app.applicationContext
    }

    @Provides
    @Singleton
    fun provideUserDataStorePreferences(
        @ApplicationContext appCtx: Context
    ): DataStore<Preferences> {
        return appCtx.dataStore
    }
}