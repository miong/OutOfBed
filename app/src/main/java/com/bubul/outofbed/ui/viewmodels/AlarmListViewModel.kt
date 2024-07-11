package com.bubul.outofbed.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.bubul.outofbed.core.data.alarm.WakeUpItem
import com.bubul.outofbed.core.data.events.AlarmsLoadedEvent
import com.bubul.outofbed.core.data.events.CreateAlarmDoneEvent
import com.bubul.outofbed.core.data.events.CreateAlarmRequestEvent
import com.bubul.outofbed.core.data.events.ModifyAlarmRequestEvent
import com.bubul.outofbed.core.service.IAlarmService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.UUID

class AlarmListViewModel : ViewModel() {
    private val _alarms = MutableStateFlow(listOf<UUID>())
    val alarms = _alarms.asStateFlow()
    private val alarmsObj = mutableMapOf<UUID, MutableStateFlow<WakeUpItem>>()
    private var serviceAccess: IAlarmService? = null

    fun registerBus() {
        EventBus.getDefault().register(this)
    }

    fun unregisterBus() {
        EventBus.getDefault().unregister(this)
    }

    fun linkService(service: IAlarmService?) {
        serviceAccess = service
    }

    fun switchToAlarmCreation() {
        EventBus.getDefault().post(CreateAlarmRequestEvent())
    }

    fun switchToAlarmModification(id: UUID) {
        EventBus.getDefault().post(ModifyAlarmRequestEvent(id))
    }

    fun getAlarm(id: UUID): StateFlow<WakeUpItem>? {
        return alarmsObj[id]?.asStateFlow()
    }

    fun activateAlarm(id: UUID, active: Boolean) {
        serviceAccess?.let { service ->
            service.activateAlarm(id, active)
            updateFlows()
        }
    }

    fun deleteAlarm(id: UUID) {
        serviceAccess?.let { service ->
            service.removeAlarm(id)
            updateFlows()
        }
    }

    @Subscribe
    fun onCreateAlarmDone(event: CreateAlarmDoneEvent) {
        updateFlows()
    }

    @Subscribe
    fun onAlarmsLoaded(event: AlarmsLoadedEvent) {
        updateFlows()
    }

    private fun updateFlows() {
        serviceAccess?.let { service ->
            val list = mutableListOf<UUID>()
            service.getAlarms().sortedBy { it.name }.forEach { alarm ->
                list.add(alarm.id)
                if (alarmsObj.contains(alarm.id)) {
                    alarmsObj[alarm.id]?.update { alarm.clone() }
                } else {
                    alarmsObj[alarm.id] = MutableStateFlow(alarm.clone())
                }
            }
            _alarms.update { list }
        }
    }
}
