package com.bubul.outofbed.core.data.persistence.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class LocalTimeConverter {
    @TypeConverter
    fun fromLocalDateTime(localTime: LocalTime): Long {
        return localTime.atDate(LocalDate.now()).toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun localDateTimeFromLong(localDateTime: Long): LocalTime {
        return LocalDateTime.ofEpochSecond(localDateTime, 0, ZoneOffset.UTC).toLocalTime()
    }
}