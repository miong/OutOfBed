package com.bubul.outofbed.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.bubul.outofbed.core.data.alarm.WakeUpItem
import com.bubul.outofbed.core.data.events.CreateAlarmRequestEvent
import com.bubul.outofbed.core.service.MainService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.greenrobot.eventbus.EventBus

class AlarmListViewModel : ViewModel() {
    private val _alarms = MutableStateFlow(listOf<WakeUpItem>())
    val alarms = _alarms.asStateFlow()

    fun loadAlarms(service: MainService?) {
        if (service != null) {
            _alarms.value = service.getAlarms()
        }
    }

    fun switchToAlarmCreation() {
        EventBus.getDefault().post(CreateAlarmRequestEvent())
    }

}