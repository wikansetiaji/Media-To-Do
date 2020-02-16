package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.repository.MainViewModelFactory
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.main.MainFragment
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.repository.MainViewModel
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.main.PlayAudioFragment


class MainActivity : AppCompatActivity() {

    init {
        System.loadLibrary("native-lib")
    }

    external fun milliToSecond(millis: Int): Int
    external fun milliToMinute(millis: Int): Int
    external fun milliToHour(millis: Int): Int

    val REQUEST_IMAGE_CAPTURE = 1

    val mainFragment:MainFragment = MainFragment.newInstance()

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        viewModel = ViewModelProviders.of(this, MainViewModelFactory(application)).get(MainViewModel::class.java)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, mainFragment)
                .commitNow()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras.get("data") as Bitmap
            mainFragment.onImageResult(imageBitmap)
        }
    }

    override fun onBackPressed() {
        if (mainFragment.isVisible){
            if (!mainFragment.onBackPressed()){
                super.onBackPressed()
            }
        }
        else{
            super.onBackPressed()
        }
    }

    fun onPlayAudioOpen(index:Int){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val dialogFragment = PlayAudioFragment(index)
        dialogFragment.show(fragmentTransaction, "dialog")
    }

}
