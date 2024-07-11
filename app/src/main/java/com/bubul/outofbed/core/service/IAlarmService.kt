package com.bubul.outofbed.core.service

import com.bubul.outofbed.core.data.alarm.Days
import com.bubul.outofbed.core.data.alarm.WakeUpItem
import java.time.LocalTime
import java.util.UUID

interface IAlarmService {

    fun getAlarms(): List<WakeUpItem>

    fun addAlarm(name: String, outOfBedTime: LocalTime, repeats: List<Days>): WakeUpItem
    fun modifyAlarm(
        alarmUID: UUID,
        name: String,
        outOfBedTime: LocalTime,
        repeats: List<Days>
    ): WakeUpItem

    fun activateAlarm(id: UUID, active: Boolean)

    fun removeAlarm(id: UUID)
    fun getAlarm(id: UUID): WakeUpItem?
    fun loadAlarms()
    fun tmp_Awake(id: UUID)
}