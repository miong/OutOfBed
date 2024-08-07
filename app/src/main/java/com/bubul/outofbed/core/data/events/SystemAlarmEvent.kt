package com.bubul.outofbed.core.data.events

import android.content.Context
import java.util.UUID

class SystemAlarmEvent(stringUUID: String?, val ctx: Context) {
    val uuid: UUID = UUID.fromString(stringUUID)
}