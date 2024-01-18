package com.bubul.outofbed.core.data.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bubul.outofbed.core.data.alarm.WakeUpItem

@Dao
interface WakeUpItemDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createOrUpdate(item: WakeUpItem)

    @Delete
    fun delete(item: WakeUpItem)

    @Query("SELECT * FROM WakeUpItem")
    fun getItems(): List<WakeUpItem>
}