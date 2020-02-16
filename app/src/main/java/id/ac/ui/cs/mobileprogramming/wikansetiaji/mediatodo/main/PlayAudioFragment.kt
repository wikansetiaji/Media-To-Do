package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.main

import android.content.DialogInterface
import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.MainActivity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.R
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.AudioEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.repository.MainViewModel
import kotlinx.android.synthetic.main.fragment_play_audio.*
import kotlinx.android.synthetic.main.fragment_play_audio.time

class PlayAudioFragment(val index:Int) : DialogFragment() {

    companion object {
        fun newInstance(index:Int) = PlayAudioFragment(index)
    }

    private lateinit var viewModel: MainViewModel
    lateinit var  mediaPlayer:MediaPlayer
    var duration:Long = 0
    private var startTime:Long = 0L
    private var timerHandler: Handler = Handler()
    var timeInMilliseconds:Long = 0L
    var timeSwapBuff:Long = 0L
    var timeChange:Long = 0L

    private var updateTimeDown:Runnable = object: Runnable{
        override fun run(){
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime

            timeChange = timeSwapBuff + timeInMilliseconds

            val showTime = duration-timeChange

            val second = (activity as MainActivity).milliToSecond(showTime.toInt())
            val minute = (activity as MainActivity).milliToMinute(showTime.toInt())
            val hour = (activity as MainActivity).milliToHour(showTime.toInt())

            time.text = "${hour}:${minute}:${second}"

            timerHandler.postDelayed(this, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_play_audio, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this.activity!!).get(MainViewModel::class.java)
        val item = viewModel.items.value!![index] as AudioEntity
        name.text=item.name
        mediaPlayer = MediaPlayer()

        try {
            mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().absolutePath + "/${item.name}")
            mediaPlayer.prepare()
            duration = mediaPlayer.duration.toLong()
            pause.setOnClickListener {
                onPauseAudio()
            }
            onPlayAudio()
            mediaPlayer.setOnCompletionListener {
                timeSwapBuff += timeInMilliseconds
                timerHandler.removeCallbacks(updateTimeDown)
                startTime = 0L
                timeInMilliseconds = 0L
                timeSwapBuff = 0L
                timeChange = 0L
                val second = (activity as MainActivity).milliToSecond(duration.toInt())
                val minute = (activity as MainActivity).milliToMinute(duration.toInt())
                val hour = (activity as MainActivity).milliToHour(duration.toInt())

                time.text = "${hour}:${minute}:${second}"
                pause.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        onStopAudio()
        super.onDismiss(dialog)
    }

    fun onPlayAudio(){
        mediaPlayer.start()
        pause.setImageResource(R.drawable.ic_pause_black_24dp)
        startTime = SystemClock.uptimeMillis();
        timerHandler.postDelayed(updateTimeDown, 0)
    }

    fun onPauseAudio(){
        if (mediaPlayer.isPlaying){
            mediaPlayer.pause()
            pause.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            timeSwapBuff += timeInMilliseconds
            timerHandler.removeCallbacks(updateTimeDown)
        }
        else{
            onPlayAudio()
        }
    }

    fun onStopAudio(){
        mediaPlayer.stop()
        timeSwapBuff += timeInMilliseconds
        timerHandler.removeCallbacks(updateTimeDown)
        mediaPlayer.release()
    }

}
