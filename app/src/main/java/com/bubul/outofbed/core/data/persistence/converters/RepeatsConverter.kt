package com.bubul.outofbed.core.data.persistence.converters

import androidx.room.TypeConverter
import com.bubul.outofbed.core.data.alarm.Repeats
import com.google.gson.Gson
import timber.log.Timber

class RepeatsConverter {
    @TypeConverter
    fun fromRepeats(value: Repeats?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toRepeats(value: String?): Repeats? {
        try {
            return Gson().fromJson(value, Repeats::class.java)
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }
    }
}