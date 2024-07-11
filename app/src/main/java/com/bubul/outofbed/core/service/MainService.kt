package com.bubul.outofbed.core.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.bubul.outofbed.R
import com.bubul.outofbed.core.Constants
import com.bubul.outofbed.core.data.alarm.Days
import com.bubul.outofbed.core.data.alarm.WakeUpItem
import com.bubul.outofbed.core.data.events.AlarmsLoadedEvent
import com.bubul.outofbed.core.data.events.AwakeEvent
import com.bubul.outofbed.core.data.events.SystemAlarmEvent
import com.bubul.outofbed.core.logic.alarm.AlarmManager
import com.bubul.outofbed.core.logic.awaker.AwakerManager
import com.bubul.outofbed.core.logic.persistence.PersistenceManager
import com.bubul.outofbed.core.logic.sound.SoundManager
import com.bubul.outofbed.core.logic.statistics.StatisticsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.time.LocalTime
import java.util.UUID

class MainService : Service(), IAlarmService {

    private val localBinder = LocalServiceBinder(this)

    private val persistenceManager = PersistenceManager()
    private val alarmManager = AlarmManager()
    private val statisticsManager = StatisticsManager()
    private val soundManager = SoundManager()
    private val awakerManager = AwakerManager()
    private var alarmLoaded = false

    override fun onBind(intent: Intent?): IBinder {
        return localBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        EventBus.getDefault().register(this)
        persistenceManager.init(this)
        startAsForeground()
        return START_STICKY
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun startAsForeground() {
        val nextAlarmTime = alarmManager.getNextAlarmTime()
        var contentText = "Alarm service running"
        nextAlarmTime?.let {
            contentText = "Next alarm at $nextAlarmTime"
        }
        val builder =
            NotificationCompat.Builder(this, Constants.SERVICE_NOTIFICATION_CHANNEL_ID).also {
                it.setContentTitle("Out of bed wont let you down buddy")
                it.setContentText(contentText)
                it.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                it.setSmallIcon(R.drawable.ic_launcher_foreground)
                it.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.app_icon))
                it.setPriority(NotificationCompat.PRIORITY_HIGH)
                it.setWhen(0)
                it.setOnlyAlertOnce(true)
                it.setOngoing(true)
                it.setContentIntent(
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, MainService::class.java),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
        startForeground(200, builder.build())
    }

    private fun updateNextAlarm() {
        alarmManager.setupNextAlarm(this)
        updateNotification()
    }

    private fun updateNotification() {
        val nextAlarmTime = alarmManager.getNextAlarmTime()
        var contentText = "Alarm service running"
        nextAlarmTime?.let {
            contentText = "Next alarm at $nextAlarmTime"
        }
        val builder =
            NotificationCompat.Builder(this, Constants.SERVICE_NOTIFICATION_CHANNEL_ID).also {
                it.setContentTitle("Out of bed wont let you down buddy")
                it.setContentText(contentText)
                it.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                it.setSmallIcon(R.drawable.ic_launcher_foreground)
                it.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.app_icon))
                it.setPriority(NotificationCompat.PRIORITY_HIGH)
                it.setWhen(0)
                it.setOnlyAlertOnce(true)
                it.setOngoing(true)
                it.setContentIntent(
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, MainService::class.java),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
        val notificationService =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationService.notify(200, builder.build())
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onSystemAlarmEvent(event: SystemAlarmEvent) {
        Timber.d("System alarm received for ${event.uuid}")
        alarmManager.getAlarm(event.uuid)?.let {
            statisticsManager.measurementStart(it)
            awakerManager.launch(event.ctx, it)
        }
        updateNextAlarm()
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onAwakeEvent(event: AwakeEvent) {
        Timber.d("Awake event received")
        alarmManager.getAlarm(event.uuid)?.let {
            statisticsManager.measurementStop(it)
        }
        updateNextAlarm()
    }

    override fun loadAlarms() {
        if (alarmLoaded) {
            EventBus.getDefault().post(AlarmsLoadedEvent())
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            persistenceManager.getDBAccess().getWakeUpItemDAO().getItems().forEach {
                alarmManager.load(it)
            }
            updateNextAlarm()
            EventBus.getDefault().post(AlarmsLoadedEvent())
            alarmLoaded = true
        }
    }

    override fun getAlarms(): List<WakeUpItem> {
        return alarmManager.getAlarms()
    }

    override fun getAlarm(id: UUID): WakeUpItem? {
        return alarmManager.getAlarm(id)
    }

    override fun addAlarm(name: String, outOfBedTime: LocalTime, repeats: List<Days>): WakeUpItem {
        val alarm = alarmManager.createWakeUpItem(name, outOfBedTime, repeats)
        persistenceManager.getDBAccess().getWakeUpItemDAO().createOrUpdate(alarm)
        updateNextAlarm()
        return alarm
    }

    override fun modifyAlarm(
        alarmUID: UUID,
        name: String,
        outOfBedTime: LocalTime,
        repeats: List<Days>
    ): WakeUpItem {
        val alarm = alarmManager.updateWakeUpItem(alarmUID, name, outOfBedTime, repeats)
        persistenceManager.getDBAccess().getWakeUpItemDAO().createOrUpdate(alarm)
        updateNextAlarm()
        return alarm
    }

    override fun removeAlarm(id: UUID) {
        alarmManager.removeWakeUpItem(id)?.let {
            CoroutineScope(Dispatchers.IO).launch {
                persistenceManager.getDBAccess().getWakeUpItemDAO().delete(it)
                updateNextAlarm()
            }
        }
    }

    override fun activateAlarm(id: UUID, active: Boolean) {
        alarmManager.activateAlarm(id, active)?.let {
            CoroutineScope(Dispatchers.IO).launch {
                persistenceManager.getDBAccess().getWakeUpItemDAO().createOrUpdate(it)
                updateNextAlarm()
            }
        }
    }

    override fun tmp_Awake(id: UUID) {
        onAwakeEvent(AwakeEvent(id.toString()))
    }
}