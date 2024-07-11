package com.bubul.outofbed.core.data.alarm

import java.time.DayOfWeek

enum class Days {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday;

    fun toDayOfWeek(): DayOfWeek {
        return DayOfWeek.of(this.ordinal + 1)
    }

    companion object {
        fun fromDayOfWeek(d: DayOfWeek): Days {
            return when (d) {
                DayOfWeek.MONDAY -> Monday
                DayOfWeek.TUESDAY -> Tuesday
                DayOfWeek.WEDNESDAY -> Wednesday
                DayOfWeek.THURSDAY -> Thursday
                DayOfWeek.FRIDAY -> Friday
                DayOfWeek.SATURDAY -> Saturday
                DayOfWeek.SUNDAY -> Sunday
            }
        }
    }
}

class Repeats(var on: List<Days>) {
    fun getNextDay(d: Days): Days? {
        if (on.isEmpty())
            return null
        for (tentative in on.sorted()) {
            if (d.ordinal < tentative.ordinal)
                return tentative
        }
        return on.sorted()[0]
    }
}