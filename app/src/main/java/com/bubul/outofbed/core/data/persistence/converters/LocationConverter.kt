package com.bubul.outofbed.core.data.persistence.converters

import android.location.Location
import androidx.room.TypeConverter
import com.google.gson.Gson
import timber.log.Timber

class LocationConverter {
    @TypeConverter
    fun fromLocation(value: Location?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toLocation(value: String?): Location? {
        try {
            return Gson().fromJson(value, Location::class.java)
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }
    }
}