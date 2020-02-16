package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.dao.TextDao
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.TextEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.tools.Converters

@Database(entities = arrayOf(TextEntity::class), version = 2)
@TypeConverters(Converters::class)
abstract class TextDatabase : RoomDatabase() {

    abstract fun textDao(): TextDao
    companion object {
        private var INSTANCE: TextDatabase? = null

        fun getInstance(context: Context): TextDatabase? {
            if (INSTANCE == null) {
                synchronized(TextDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        TextDatabase::class.java, "textdata.db")
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}