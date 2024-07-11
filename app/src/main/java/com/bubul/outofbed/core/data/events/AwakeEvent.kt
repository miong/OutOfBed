package com.bubul.outofbed.core.data.events

import java.util.UUID

class AwakeEvent(stringUUID: String?) {
    val uuid: UUID = UUID.fromString(stringUUID)
}