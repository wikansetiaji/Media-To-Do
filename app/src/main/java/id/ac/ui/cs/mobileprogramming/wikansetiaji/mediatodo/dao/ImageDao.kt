package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.ImageEntity
import java.util.*

@Dao
interface ImageDao {
    @Query("SELECT * from image")
    fun getAll(): LiveData<List<ImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(textEntity: ImageEntity)

    @Delete
    fun delete(textEntity: ImageEntity)

}