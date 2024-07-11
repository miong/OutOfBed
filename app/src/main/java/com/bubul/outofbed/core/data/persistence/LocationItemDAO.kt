package com.bubul.outofbed.core.data.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bubul.outofbed.core.data.location.LocationItem

@Dao
interface LocationItemDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createOrUpdate(item: LocationItem)

    @Delete
    fun delete(item: LocationItem)

    @Query("SELECT * FROM LocationItem")
    fun getItems(): List<LocationItem>
}