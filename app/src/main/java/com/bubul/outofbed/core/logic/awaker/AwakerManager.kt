package com.bubul.outofbed.core.logic.awaker

import android.content.Context
import android.content.Intent
import com.bubul.outofbed.core.data.alarm.WakeUpItem
import com.bubul.outofbed.ui.activities.AwakerActivity
import timber.log.Timber

class AwakerManager {
    fun launch(ctx: Context, it: WakeUpItem) {
        Timber.i("Request Awaker activity to start")
        ctx.startActivity(Intent(ctx, AwakerActivity::class.java).apply {
            putExtra("wakeupitem_id", it.id.toString())
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}