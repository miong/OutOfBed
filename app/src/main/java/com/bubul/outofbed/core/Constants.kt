package com.bubul.outofbed.core

class Constants {
    companion object {
        private const val INTENT_BASE = "com.bubul.outofbed.intents"
        const val INTENT_START_SERVICE = "$INTENT_BASE.start_service"
        const val SERVICE_NOTIFICATION_CHANNEL_ID = "com.bubul.outofbed"
        const val SERVICE_NOTIFICATION_CHANNEL_NAME = "Out of bed"
    }
}