package com.bubul.outofbed.core.data.location

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
class LocationItem(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val location: Location
)