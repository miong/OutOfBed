package com.bubul.outofbed.core.data.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bubul.outofbed.core.data.alarm.WakeUpItem
import com.bubul.outofbed.core.data.location.LocationItem
import com.bubul.outofbed.core.data.persistence.converters.LocalDateConverter
import com.bubul.outofbed.core.data.persistence.converters.LocalDateTimeConverter
import com.bubul.outofbed.core.data.persistence.converters.LocalTimeConverter
import com.bubul.outofbed.core.data.persistence.converters.LocationConverter
import com.bubul.outofbed.core.data.persistence.converters.RepeatsConverter
import com.bubul.outofbed.core.data.persistence.converters.UUIDConverter

@Database(
    entities = [WakeUpItem::class, LocationItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    UUIDConverter::class,
    LocalTimeConverter::class,
    LocalDateConverter::class,
    LocalDateTimeConverter::class,
    LocationConverter::class,
    RepeatsConverter::class,
)
abstract class OutOfBedDatabase : RoomDatabase() {

    abstract fun getWakeUpItemDAO(): WakeUpItemDAO

    companion object {
        private var instance: OutOfBedDatabase? = null
        private val lock = object {}
        fun getInstance(ctx: Context): OutOfBedDatabase {
            synchronized(lock) {
                if (instance == null)
                    instance = Room.databaseBuilder(
                        ctx.applicationContext,
                        OutOfBedDatabase::class.java,
                        "OutOfBed.db"
                    ).build()
            }
            return instance!!
        }
    }
}