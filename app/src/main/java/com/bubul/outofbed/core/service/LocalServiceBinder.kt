package com.bubul.outofbed.core.service

import android.os.Binder

class LocalServiceBinder(private val service: MainService) : Binder() {
    fun getService(): IAlarmService {
        return service
    }
}