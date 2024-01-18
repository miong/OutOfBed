package com.bubul.outofbed.core.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bubul.outofbed.core.Constants
import com.bubul.outofbed.core.data.events.SystemAlarmEvent
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class LocalBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("Broadcast receiver got an intent")
        if (intent?.action.equals(Constants.INTENT_START_SERVICE) or intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Timber.i("Starting foreground service")
            context!!.startForegroundService(Intent(context, MainService::class.java).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } else {
            // we received an alarm
            EventBus.getDefault().post(SystemAlarmEvent(intent!!.getStringExtra("id")))
        }
    }
}