package com.bubul.outofbed.core.data.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime
import java.util.UUID

@Entity
class WakeUpItem(
    @PrimaryKey
    val id: UUID,
    val outOfBedTime: LocalTime
)