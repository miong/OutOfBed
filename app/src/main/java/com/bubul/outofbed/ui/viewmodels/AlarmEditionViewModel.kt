package com.bubul.outofbed.ui.viewmodels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.lifecycle.ViewModel
import com.bubul.outofbed.core.data.alarm.Days
import com.bubul.outofbed.core.data.events.CreateAlarmDoneEvent
import com.bubul.outofbed.core.data.events.CreateAlarmRequestEvent
import com.bubul.outofbed.core.data.events.ModifyAlarmRequestEvent
import com.bubul.outofbed.core.service.IAlarmService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.time.LocalTime
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
class AlarmEditionViewModel : ViewModel() {

    private var service: IAlarmService? = null
    var timeState = MutableStateFlow(TimePickerState(0, 0, true))
    private var _canSave = MutableStateFlow(false)
    var canSave = _canSave.asStateFlow()
    private var _nameState = MutableStateFlow("")
    var nameState = _nameState.asStateFlow()
    private var _repeatsState = MutableStateFlow(listOf<Days>())
    var repeatsState = _repeatsState.asStateFlow()

    private var alarmUID: UUID? = null

    fun registerBus() {
        EventBus.getDefault().register(this)
    }

    fun unregisterBus() {
        EventBus.getDefault().unregister(this)
    }

    private fun reset() {
        alarmUID = null
        val now = LocalTime.now()
        timeState.update {
            TimePickerState(
                now.hour,
                now.minute,
                true
            )
        }
        _nameState.update { "" }
        _repeatsState.update { listOf<Days>() }
        _canSave.update { false }
    }

    private fun setup(id: UUID) {
        alarmUID = id
        service?.let {
            it.getAlarm(id)?.let { data ->
                timeState.update {
                    TimePickerState(
                        data.outOfBedTime.hour,
                        data.outOfBedTime.minute,
                        true
                    )
                }
                _nameState.update { data.name }
                _repeatsState.update { data.repeats.on }
                _canSave.update { true }
            }
        }
    }

    @Subscribe
    fun onCreateAlarmRequest(event: CreateAlarmRequestEvent) {
        reset()
    }

    @Subscribe
    fun onModifyAlarmRequest(event: ModifyAlarmRequestEvent) {
        setup(event.id)
    }

    fun onSave() {
        CoroutineScope(Dispatchers.IO).launch {
            service?.let {
                if (alarmUID != null) {
                    Timber.i("Send modify request to the service")
                    it.modifyAlarm(
                        alarmUID!!,
                        _nameState.value,
                        LocalTime.of(timeState.value.hour, timeState.value.minute),
                        repeatsState.value
                    )
                } else {
                    Timber.i("Send creation request to the service")
                    it.addAlarm(
                        _nameState.value,
                        LocalTime.of(timeState.value.hour, timeState.value.minute),
                        repeatsState.value
                    )
                }
            }
            EventBus.getDefault().post(CreateAlarmDoneEvent())
        }
    }

    fun onCancel() {
        EventBus.getDefault().post(CreateAlarmDoneEvent())
    }

    fun linkService(service: IAlarmService?) {
        this.service = service
    }

    fun updateName(value: String) {
        _nameState.update { value }
        _canSave.update { value.isNotEmpty() && value.isNotBlank() }
    }

    fun toggleRepeats(day: Days) {
        _repeatsState.update {
            if (it.contains(day)) {
                mutableListOf<Days>().also { l ->
                    l.addAll(it.filter { d -> d != day })
                }
            } else {
                mutableListOf<Days>().also { l ->
                    l.addAll(it)
                    l.add(day)
                    l.sort()
                }
            }
        }
    }

}