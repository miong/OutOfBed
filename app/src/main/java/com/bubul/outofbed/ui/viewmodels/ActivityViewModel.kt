package com.bubul.outofbed.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.bubul.outofbed.core.data.events.CreateAlarmRequestEvent
import com.bubul.outofbed.core.service.MainService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber

class ActivityViewModel : ViewModel() {

    enum class ActivityScreen {
        LOADING,
        ALARM_LIST,
        CREATE_ALARM,
    }

    private val _screen = MutableStateFlow(ActivityScreen.LOADING)
    val screen = _screen.asStateFlow()

    fun registerBus() {
        EventBus.getDefault().register(this)
    }

    fun unregisterBus() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onCreateAlarmRequest(event: CreateAlarmRequestEvent) {
        _screen.value = ActivityScreen.CREATE_ALARM
    }

    fun loadAlarms(service: MainService?) {
        service!!.preloadAlarms()
        Timber.d("Alarm list loaded")
        _screen.value = ActivityScreen.ALARM_LIST
    }
}