package com.bubul.outofbed.core.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.bubul.outofbed.R
import com.bubul.outofbed.core.Constants
import com.bubul.outofbed.core.data.alarm.WakeUpItem
import com.bubul.outofbed.core.data.events.SystemAlarmEvent
import com.bubul.outofbed.core.logic.alarm.AlarmManager
import com.bubul.outofbed.core.logic.persistence.PersistenceManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class MainService : Service() {

    private val localBinder = LocalServiceBinder(this)

    private val persistenceManager = PersistenceManager()
    private val alarmManager = AlarmManager()

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
        val builder =
            NotificationCompat.Builder(this, Constants.SERVICE_NOTIFICATION_CHANNEL_ID).also {
                it.setContentTitle("Out of bed wont let you down buddy")
                it.setContentText("Alarm service running")
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

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onSystemAlarmEvent(event: SystemAlarmEvent) {
        Timber.d("System alarm received")
    }

    fun preloadAlarms() {
        persistenceManager.getDBAccess().getWakeUpItemDAO().getItems().forEach {
            alarmManager.load(it)
        }
    }

    fun getAlarms(): List<WakeUpItem> {
        return alarmManager.getAlarms()
    }
}