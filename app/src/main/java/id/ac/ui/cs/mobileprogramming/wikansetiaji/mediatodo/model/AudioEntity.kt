package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "audio")
data class AudioEntity(
    @ColumnInfo(name = "name")var name: String,
    @ColumnInfo(name="timestamp")var timestamp: Date,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0
) :ToDoEntity{
    override fun getType(): Int {
        return 2
    }

    override fun getTimeStamp(): Date {
        return timestamp
    }
}