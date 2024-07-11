package com.bubul.outofbed.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.bubul.outofbed.core.data.events.AlarmsLoadedEvent
import com.bubul.outofbed.core.data.events.CreateAlarmDoneEvent
import com.bubul.outofbed.core.data.events.CreateAlarmRequestEvent
import com.bubul.outofbed.core.data.events.ModifyAlarmRequestEvent
import com.bubul.outofbed.core.service.IAlarmService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ActivityViewModel : ViewModel() {

    enum class ActivityScreen {
        LOADING,
        ALARM_LIST,
        CREATE_ALARM,
        MODIFY_ALARM,
    }

    private val _screen = MutableStateFlow(ActivityScreen.LOADING)
    val screen = _screen.asStateFlow()

    private var service: IAlarmService? = null

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

    @Subscribe
    fun onModifyAlarmRequest(event: ModifyAlarmRequestEvent) {
        _screen.value = ActivityScreen.MODIFY_ALARM
    }

    @Subscribe
    fun onCreateAlarmDone(event: CreateAlarmDoneEvent) {
        _screen.value = ActivityScreen.ALARM_LIST
    }

    @Subscribe
    fun onAlarmLoaded(event: AlarmsLoadedEvent) {
        _screen.value = ActivityScreen.ALARM_LIST
    }

    fun linkService(service: IAlarmService?) {
        this.service = service
    }
}