package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.TextEntity
import java.util.*

@Dao
interface TextDao {
    @Query("SELECT * from texts")
    fun getAll(): LiveData<List<TextEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(textEntity: TextEntity)

    @Delete
    fun delete(textEntity: TextEntity)

    @Query("UPDATE texts SET text =:text, timestamp =:timestamp WHERE id =:id")
    fun update(id: Long, text: String, timestamp: Date)

}