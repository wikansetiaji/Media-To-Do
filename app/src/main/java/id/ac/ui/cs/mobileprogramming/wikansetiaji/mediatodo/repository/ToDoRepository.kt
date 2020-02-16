package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.repository

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.dao.AudioDao
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.dao.ImageDao
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.dao.TextDao
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.database.AudioDatabase
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.database.ImageDatabase
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.database.TextDatabase
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.AudioEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.ImageEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.TextEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.ToDoEntity


const val SHRD_PRF_KEY = "alarm-item"
const val CHANNEL_ID = "notif"
class ToDoRepository(application: Context) {
    private var textDao: TextDao = TextDatabase.getInstance(application)!!.textDao()
    private var audioDao:AudioDao = AudioDatabase.getInstance(application)!!.audioDao()
    private var imageDao:ImageDao = ImageDatabase.getInstance(application)!!.imageDao()
    private val sharedPref: SharedPreferences = application.getSharedPreferences(SHRD_PRF_KEY, 0)

    fun getAllItem(): LiveData<List<ToDoEntity>> {
        val textItems = textDao.getAll() as LiveData<List<ToDoEntity>>
        val audioItems = audioDao.getAll() as LiveData<List<ToDoEntity>>
        val imageItems = imageDao.getAll() as LiveData<List<ToDoEntity>>
        val itemLiveData = MediatorLiveData<List<ToDoEntity>>()
        itemLiveData.addSource(textItems) {
            itemLiveData.value = combineLiveDataLists(textItems, audioItems, imageItems)
        }
        itemLiveData.addSource(audioItems) {
            itemLiveData.value = combineLiveDataLists(textItems, audioItems, imageItems)
        }
        itemLiveData.addSource(imageItems) {
            itemLiveData.value = combineLiveDataLists(textItems, audioItems, imageItems)
        }
        return itemLiveData
    }

    fun combineLiveDataLists(text: LiveData<List<ToDoEntity>>, audio: LiveData<List<ToDoEntity>>, image: LiveData<List<ToDoEntity>>):List<ToDoEntity>{
        val temp:ArrayList<ToDoEntity> = arrayListOf()
        if (text.value!=null){
            temp.addAll(text.value!!)
        }
        if (audio.value!=null){
            temp.addAll(audio.value!!)
        }
        if (image.value!=null){
            temp.addAll(image.value!!)
        }
        return temp.sortedWith(compareBy{ it.getTimeStamp() })
    }

    fun addItemToSharedPref(){
        val editor = sharedPref.edit()
        editor.putInt(SHRD_PRF_KEY, sharedPref.getInt(SHRD_PRF_KEY,0)+1)
        editor.apply()
    }

    fun removeItemFromSharedPref(){
        val editor = sharedPref.edit()
        editor.putInt(SHRD_PRF_KEY, sharedPref.getInt(SHRD_PRF_KEY,1)-1)
        editor.apply()
    }

    fun insertText(text: TextEntity) {
        insertTextAsyncTask(textDao).execute(text)
        addItemToSharedPref()
    }

    fun updateText(text: TextEntity){
        updateTextAsyncTask(textDao).execute(text)
    }

    fun deleteText(text: TextEntity) {
        deleteTextAsyncTask(textDao).execute(text)
        removeItemFromSharedPref()
    }

    fun insertAudio(audio: AudioEntity) {
        insertAudioAsyncTask(audioDao).execute(audio)
        addItemToSharedPref()
    }

    fun deleteAudio(audio: AudioEntity) {
        deleteAudioAsyncTask(audioDao).execute(audio)
        removeItemFromSharedPref()
    }

    fun insertImage(video: ImageEntity) {
        insertImageAsyncTask(imageDao).execute(video)
        addItemToSharedPref()
    }

    fun deleteImage(video: ImageEntity) {
        deleteImageAsyncTask(imageDao).execute(video)
        removeItemFromSharedPref()
    }

    private class updateTextAsyncTask internal constructor(private val mAsyncTaskDao: TextDao) :
        AsyncTask<TextEntity, Void, Void>() {

        override fun doInBackground(vararg params: TextEntity): Void? {
            mAsyncTaskDao.update(params[0].id,params[0].text,params[0].timestamp)
            return null
        }
    }

    private class insertTextAsyncTask internal constructor(private val mAsyncTaskDao: TextDao) :
        AsyncTask<TextEntity, Void, Void>() {

        override fun doInBackground(vararg params: TextEntity): Void? {
            mAsyncTaskDao.insert(params[0])
            return null
        }
    }

    private class deleteTextAsyncTask internal constructor(private val mAsyncTaskDao: TextDao) :
        AsyncTask<TextEntity, Void, Void>() {

        override fun doInBackground(vararg params: TextEntity): Void? {
            mAsyncTaskDao.delete(params[0])
            return null
        }
    }

    private class insertAudioAsyncTask internal constructor(private val mAsyncTaskDao: AudioDao) :
        AsyncTask<AudioEntity, Void, Void>() {

        override fun doInBackground(vararg params: AudioEntity): Void? {
            mAsyncTaskDao.insert(params[0])
            return null
        }
    }

    private class deleteAudioAsyncTask internal constructor(private val mAsyncTaskDao: AudioDao) :
        AsyncTask<AudioEntity, Void, Void>() {

        override fun doInBackground(vararg params: AudioEntity): Void? {
            mAsyncTaskDao.delete(params[0])
            return null
        }
    }

    private class insertImageAsyncTask internal constructor(private val mAsyncTaskDao: ImageDao) :
        AsyncTask<ImageEntity, Void, Void>() {

        override fun doInBackground(vararg params: ImageEntity): Void? {
            mAsyncTaskDao.insert(params[0])
            return null
        }
    }

    private class deleteImageAsyncTask internal constructor(private val mAsyncTaskDao: ImageDao) :
        AsyncTask<ImageEntity, Void, Void>() {

        override fun doInBackground(vararg params: ImageEntity): Void? {
            mAsyncTaskDao.delete(params[0])
            return null
        }
    }

}