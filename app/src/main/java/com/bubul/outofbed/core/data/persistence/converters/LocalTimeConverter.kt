package com.bubul.outofbed.core.data.persistence.converters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class LocalTimeConverter {
    @TypeConverter
    fun fromLocalTime(localTime: LocalTime): Long {
        return localTime.atDate(LocalDate.now()).toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun localTimeFromLong(localTime: Long): LocalTime {
        return LocalDateTime.ofEpochSecond(localTime, 0, ZoneOffset.UTC).toLocalTime()
    }
}

class LocalDateConverter {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @TypeConverter
    fun fromLocalDate(localDate: LocalDate): Long {
        return LocalDateTime.of(localDate, LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun localDateFromLong(localDate: Long): LocalDate {
        return LocalDateTime.ofEpochSecond(localDate, 0, ZoneOffset.UTC).toLocalDate()
    }
}

class LocalDateTimeConverter {
    @TypeConverter
    fun fromLocalDate(localDateTime: LocalDateTime): Long {
        return localDateTime.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun localDateFromLong(localDateTime: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(localDateTime, 0, ZoneOffset.UTC)
    }
}