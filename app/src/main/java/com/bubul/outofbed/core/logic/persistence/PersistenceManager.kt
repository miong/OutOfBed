package com.bubul.outofbed.core.logic.persistence

import android.content.Context
import com.bubul.outofbed.core.data.persistence.OutOfBedDatabase

class PersistenceManager {
    private lateinit var database: OutOfBedDatabase

    fun init(ctx: Context) {
        database = OutOfBedDatabase.getInstance(ctx)
    }

    fun getDBAccess(): OutOfBedDatabase {
        return database
    }
}