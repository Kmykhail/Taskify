package com.kote.taskifyapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.kote.taskifyapp.ui.home.GroupTasksType
import com.kote.taskifyapp.ui.settings.TaskViewType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor (
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val GROUP_TASK_TYPE = intPreferencesKey("group_task_type")
        val TASK_VIEW_TYPE = intPreferencesKey("task_view_type")
    }

    suspend fun saveTaskViewType(type: TaskViewType) {
        dataStore.edit { preference ->
            preference[TASK_VIEW_TYPE] = type.ordinal
        }
    }

    suspend fun saveGroupTaskType(type: GroupTasksType) {
        dataStore.edit { preference ->
            preference[GROUP_TASK_TYPE] = type.ordinal
        }
    }

    val taskViewTypeFlow: Flow<TaskViewType> = dataStore.data
        .map { preference ->
            val type = preference[TASK_VIEW_TYPE] ?: TaskViewType.DefaultView.ordinal
            TaskViewType.fromInt(type)
        }
        .distinctUntilChanged()


    val groupTaskTypeFlow: Flow<GroupTasksType> = dataStore.data
        .map { preferences ->
            val type = preferences[GROUP_TASK_TYPE] ?: GroupTasksType.ALL.ordinal
            GroupTasksType.fromInt(type)
        }
        .distinctUntilChanged()

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}