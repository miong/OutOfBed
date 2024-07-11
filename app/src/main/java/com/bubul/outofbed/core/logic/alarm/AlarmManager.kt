package com.bubul.outofbed.core.logic.alarm

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.bubul.outofbed.core.Constants
import com.bubul.outofbed.core.data.alarm.Days
import com.bubul.outofbed.core.data.alarm.Repeats
import com.bubul.outofbed.core.data.alarm.WakeUpItem
import com.bubul.outofbed.core.service.LocalBroadcastReceiver
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

class AlarmManager {

    private val alarms: MutableList<WakeUpItem> = mutableListOf<WakeUpItem>()
    private var alarmPendingIntent: PendingIntent? = null

    fun createWakeUpItem(name: String, outOfBedTime: LocalTime, repeats: List<Days>): WakeUpItem {
        val now = LocalDate.now()
        val item = WakeUpItem(
            UUID.randomUUID(),
            name,
            outOfBedTime,
            Repeats(repeats),
            now,
            WakeUpItem.oneShotDate(outOfBedTime, now),
            true
        )
        alarms.add(item)
        return item
    }

    fun updateWakeUpItem(
        id: UUID,
        name: String,
        outOfBedTime: LocalTime,
        repeats: List<Days>
    ): WakeUpItem {
        val old = getAlarm(id)
        if (old != null) {
            alarms.removeIf { it.id == id }
            val newCreationDate = if (repeats.isEmpty()) LocalDate.now() else old.creationDate
            val item = WakeUpItem(
                id,
                name,
                outOfBedTime,
                Repeats(repeats),
                newCreationDate,
                WakeUpItem.oneShotDate(outOfBedTime, newCreationDate),
                true
            )
            alarms.add(item)
            return item
        } else {
            return createWakeUpItem(name, outOfBedTime, repeats)
        }
    }

    fun activateAlarm(id: UUID, active: Boolean): WakeUpItem? {
        val alarm = getAlarm(id)?.also {
            it.active = active
        }
        if (alarm != null && alarm.nextAlarm() == null && active) {
            return updateWakeUpItem(alarm.id, alarm.name, alarm.outOfBedTime, alarm.repeats.on)
        }
        return alarm
    }

    fun load(item: WakeUpItem) {
        alarms.add(item)
    }

    fun getAlarms(): List<WakeUpItem> {
        return alarms
    }

    fun removeWakeUpItem(id: UUID): WakeUpItem? {
        val item = alarms.find {
            it.id == id
        }?.also {
            alarms.remove(it)
        }
        return item
    }

    fun getAlarm(id: UUID?): WakeUpItem? {
        return alarms.find {
            it.id == id
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun setupNextAlarm(ctx: Context) {
        val manager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            manager.cancelAll()
        } catch (e: Exception) {
            Timber.e(e)
        }
        if (alarms.isEmpty()) {
            try {
                alarmPendingIntent?.let {
                    manager.cancel(it)
                    Timber.i("Unset next alarm")
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
            return
        }
        val sortedAlarms = alarms.filter { it.nextAlarm() != null }.map {
            Pair(it.nextAlarm(), it.id)
        }.sortedBy {
            it.first
        }
        if (sortedAlarms.isEmpty())
            return
        val next = sortedAlarms[0]
        alarmPendingIntent = PendingIntent.getBroadcast(
            ctx,
            206,
            Intent(ctx, LocalBroadcastReceiver::class.java).apply {
                putExtra("wakeupitem_id", next.second.toString())
                action = Constants.INTENT_SYSTEM_ALARM
            },
            PendingIntent.FLAG_IMMUTABLE.or(PendingIntent.FLAG_ONE_SHOT)
                .or(PendingIntent.FLAG_CANCEL_CURRENT)
        )
        val now = LocalDateTime.now()
        val nextTime = next.first!!
        val inSec = nextTime.atZone(ZoneId.systemDefault())
            .toEpochSecond() - now.atZone(ZoneId.systemDefault()).toEpochSecond()
        val trigger = now.plusSeconds(inSec).atZone(ZoneId.systemDefault()).toEpochSecond()
        try {
            manager.setExactAndAllowWhileIdle(
                RTC_WAKEUP,
                trigger * 1000,
                alarmPendingIntent!!,
            )
            Timber.i("Set next alarm in $inSec sec")
        } catch (e: SecurityException) {
            Timber.e(e)
        }
    }

    fun getNextAlarmTime(): LocalDateTime? {
        val sortedAlarms = alarms.filter { it.nextAlarm() != null }.sortedBy { it.nextAlarm() }
        if (sortedAlarms.isEmpty())
            return null
        return sortedAlarms[0].nextAlarm()
    }
}
