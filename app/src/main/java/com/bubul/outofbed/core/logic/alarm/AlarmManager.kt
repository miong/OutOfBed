package com.bubul.outofbed.core.logic.alarm

import com.bubul.outofbed.core.data.alarm.WakeUpItem
import java.time.LocalTime
import java.util.UUID

class AlarmManager {

    private val alarms: MutableList<WakeUpItem> = mutableListOf<WakeUpItem>()

    fun createWakeUpItem(outOfBedTime: LocalTime): WakeUpItem {
        val item = WakeUpItem(UUID.randomUUID(), outOfBedTime)
        alarms.add(item)
        return item
    }

    fun load(item: WakeUpItem) {
        alarms.add(item)
    }

    fun getAlarms(): List<WakeUpItem> {
        return alarms
    }
}