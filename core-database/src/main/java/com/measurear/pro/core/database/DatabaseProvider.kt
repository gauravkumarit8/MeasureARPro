package com.measurear.pro.core.database

import android.content.Context
import androidx.room.Room

/**
 * Simple singleton accessor — no DI framework wired yet (see MeasureARProApp
 * TODO). Promote to Hilt-injected once more than one or two call sites need
 * the database, rather than adding a DI dependency for a single accessor.
 */
object DatabaseProvider {
    @Volatile private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "measurear_pro.db"
            ).build().also { instance = it }
        }
}
