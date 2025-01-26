package com.kote.taskifyapp.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kote.taskifyapp.data.ReminderType

class Converter {
    @TypeConverter
    fun fromReminderType(value: Int): ReminderType = ReminderType.values().first { it.value == value }

    @TypeConverter
    fun reminderTypeToInt(reminderType: ReminderType): Int = reminderType.value

    @TypeConverter
    fun fromTagList(tags: List<String>): String = Gson().toJson(tags)

    @TypeConverter
    fun toTagList(tags: String): List<String> {
        val listType = object: TypeToken<List<String>>() {}.type
        return Gson().fromJson(tags, listType)
    }
}