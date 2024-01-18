package com.bubul.outofbed.core.data.events

import java.util.UUID

class SystemAlarmEvent(stringUUID: String?) {
    val uuid = UUID.fromString(stringUUID)
}