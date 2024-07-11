package com.bubul.outofbed.core.data.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@Entity
class WakeUpItem(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val outOfBedTime: LocalTime,
    val repeats: Repeats,
    val creationDate: LocalDate,
    val oneShotDate: LocalDateTime?,
    var active: Boolean,
) {

    fun nextAlarm(): LocalDateTime? {
        if (!active)
            return null
        val now = LocalDateTime.now()
        if (repeats.on.isEmpty()) {
            if (oneShotDate != null) {
                if (oneShotDate.isAfter(now))
                    return oneShotDate
                return null
            }
            return null
        }
        val day = Days.fromDayOfWeek(now.dayOfWeek)
        if (repeats.on.contains(day)) {
            if (outOfBedTime.isAfter(now.toLocalTime()))
                return LocalDateTime.of(LocalDate.now(), outOfBedTime)
        }
        val next = repeats.getNextDay(day)!!
        var nbDays = next.ordinal - day.ordinal
        if (nbDays < 0)
            nbDays += 7
        return LocalDateTime.of(now.plusDays(nbDays.toLong()).toLocalDate(), outOfBedTime)
    }

    fun clone(): WakeUpItem {
        return WakeUpItem(id, name, outOfBedTime, repeats, creationDate, oneShotDate, active)
    }

    companion object {
        fun oneShotDate(
            outOfBedTime: LocalTime,
            creationDate: LocalDate
        ): LocalDateTime? {
            val now = LocalDateTime.now()
            val sameDay = LocalDateTime.of(creationDate, outOfBedTime)
            val tomorrowDay = LocalDateTime.of(creationDate.plusDays(1), outOfBedTime)
            return if (sameDay.isAfter(now)) {
                sameDay
            } else if (tomorrowDay.isAfter(now)) {
                tomorrowDay
            } else {
                null
            }
        }
    }
}