package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.AudioEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.ImageEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.TextEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.ToDoEntity

class MainViewModel(application: Context) : ViewModel() {
    var mRepository:ToDoRepository = ToDoRepository(application)
    val items: LiveData<List<ToDoEntity>> = mRepository.getAllItem()

    fun getDataItems(): LiveData<List<ToDoEntity>> {
        return items
    }

    fun addTextItem(textEntity: TextEntity){
        mRepository.insertText(textEntity)
    }

    fun deleteTextItem(textEntity: TextEntity){
        mRepository.deleteText(textEntity)
    }

    fun editTextItem(textEntity: TextEntity){
        mRepository.updateText(textEntity)
    }

    fun addAudioItem(audioEntity: AudioEntity){
        mRepository.insertAudio(audioEntity)
    }

    fun deleteAudioItem(audioEntity: AudioEntity){
        mRepository.deleteAudio(audioEntity)
    }

    fun addImageItem(imageEntity: ImageEntity){
        mRepository.insertImage(imageEntity)
    }

    fun deleteImageItem(imageEntity: ImageEntity){
        mRepository.deleteImage(imageEntity)
    }
}
