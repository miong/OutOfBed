package com.bubul.outofbed.ui

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import com.bubul.outofbed.BuildConfig
import com.bubul.outofbed.core.Constants
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupLogging()
        setupNotificationChannel()
        Timber.d("Application ready")
    }

    private fun setupNotificationChannel() {
        val serviceChannel = NotificationChannel(
            Constants.SERVICE_NOTIFICATION_CHANNEL_ID,
            Constants.SERVICE_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).also {
            it.lockscreenVisibility = Notification.VISIBILITY_SECRET
            it.enableLights(false)
            it.enableVibration(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(serviceChannel)
    }

    private fun setupLogging() {
        Log.i("com.bubul.outofbed", "Setup logging, debug=${BuildConfig.DEBUG}")
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Timber.DebugTree() {
                override fun isLoggable(tag: String?, priority: Int): Boolean {
                    return priority >= Log.INFO
                }
            })
            Timber.tag("com.bubul.outofbed")
        }
    }
}