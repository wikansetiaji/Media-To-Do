package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.dao.ImageDao
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.ImageEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.tools.Converters

@Database(entities = arrayOf(ImageEntity::class), version = 1)
@TypeConverters(Converters::class)
abstract class ImageDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDao
    companion object {
        private var INSTANCE: ImageDatabase? = null

        fun getInstance(context: Context): ImageDatabase? {
            if (INSTANCE == null) {
                synchronized(ImageDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        ImageDatabase::class.java, "imagedata.db")
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