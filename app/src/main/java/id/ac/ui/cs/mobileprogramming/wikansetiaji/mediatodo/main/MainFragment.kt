package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.main

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaRecorder
import android.os.*
import androidx.lifecycle.ViewModelProviders
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab4.ui.main.EditTextFragment
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.MainActivity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.R
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.adapter.TodoAdapter
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.AudioEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.ImageEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.TextEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.ToDoEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.receiver.AlarmReceiver
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.repository.MainViewModel
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.repository.SHRD_PRF_KEY
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.tools.ImageSaver
import kotlinx.android.synthetic.main.main_fragment.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var sharedPref: SharedPreferences
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: TodoAdapter
    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false

    var timeStamp:String=""

    private var startTime:Long = 0L
    private var timerHandler: Handler = Handler()
    var timeInMilliseconds:Long = 0L
    var timeSwapBuff:Long = 0L
    var timeChange:Long = 0L

    private var updateTimeUp:Runnable = object: Runnable{
        override fun run(){
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime

            timeChange = timeSwapBuff + timeInMilliseconds

            val second = (activity as MainActivity).milliToSecond(timeChange.toInt())
            val minute = (activity as MainActivity).milliToMinute(timeChange.toInt())
            val hour = (activity as MainActivity).milliToHour(timeChange.toInt())

            audio_time.text = "${hour}:${minute}:${second}"

            timerHandler.postDelayed(this, 0)
        }
    }

    private fun startRecording() {
        timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        output = Environment.getExternalStorageDirectory().absolutePath + "/Audio ${timeStamp}.mp3"
        mediaRecorder = MediaRecorder()

        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            startTime = SystemClock.uptimeMillis();
            timerHandler.postDelayed(updateTimeUp, 0)
            state = true
            audio_record.visibility=View.INVISIBLE
            audio_pause.visibility=View.VISIBLE
            Toast.makeText(activity, "Recording started!", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun resetRecording() {
        mediaRecorder=null
        audio_time.text="0:0:0"
        audio_record.visibility=View.VISIBLE
        audio_pause.visibility=View.INVISIBLE
        audio_pause.text="pause"
        startTime = 0L
        timerHandler = Handler()
        timeInMilliseconds = 0L
        timeSwapBuff = 0L
        timeChange = 0L
    }


    private fun stopRecording(){
        if(state){
            mediaRecorder?.stop()
            timeSwapBuff += timeInMilliseconds
            timerHandler.removeCallbacks(updateTimeUp)
            mediaRecorder?.release()
            state = false
            viewModel.addAudioItem(AudioEntity("Audio ${timeStamp}.mp3",Calendar.getInstance().time))
            Toast.makeText(activity, "Recording saved", Toast.LENGTH_SHORT).show()
            resetRecording()
            activity!!.onBackPressed()
        }else{
            Toast.makeText(activity, "You are not recording right now!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {
        if(state) {
            if(!recordingStopped){
                Toast.makeText(this.activity,"Stopped!", Toast.LENGTH_SHORT).show()
                mediaRecorder?.pause()
                timeSwapBuff += timeInMilliseconds;
                timerHandler.removeCallbacks(updateTimeUp);
                recordingStopped = true
                audio_pause.text = "Resume"
            }else{
                resumeRecording()
            }
        }
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {
        Toast.makeText(this.activity,"Resume!", Toast.LENGTH_SHORT).show()
        mediaRecorder?.resume()
        startTime = SystemClock.uptimeMillis();
        timerHandler.postDelayed(updateTimeUp, 0)
        audio_pause.text = "Pause"
        recordingStopped = false
    }

    val REQUEST_IMAGE_CAPTURE = 1

    fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                activity!!.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    fun onImageResult(imageBitmap:Bitmap){
        val imageSaver = ImageSaver(activity!!)
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        imageSaver.
            setFileName("Image ${timeStamp}.png").
            setDirectoryName("images").
            save(imageBitmap)
        addImage("Image ${timeStamp}.png")
    }

    fun addText(text:String){
        viewModel.addTextItem(TextEntity(text,Calendar.getInstance().time))
    }

    fun addImage(name:String){
        viewModel.addImageItem(ImageEntity(name,Calendar.getInstance().time))
    }

    fun deleteText(text:TextEntity){
        viewModel.deleteTextItem(text)
    }

    fun deleteImage(image:ImageEntity){
        viewModel.deleteImageItem(image)
    }

    fun deleteAudio(audio:AudioEntity){
        viewModel.deleteAudioItem(audio)
    }

    fun onEditTextItem(index:Int) {
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.container, EditTextFragment.newInstance(index))
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun onViewImageItem(index:Int) {
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.container, ViewImageFragment.newInstance(index))
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun fabOpen(){
        fab_open.hide()
        fab_close.show()
        fab_audio.show()
        fab_text.show()
        fab_image.show()
    }

    fun fabClose(){
        fab_open.show()
        fab_close.hide()
        fab_audio.hide()
        fab_text.hide()
        fab_image.hide()
    }

    fun addTextClose(){
        text_input.visibility=View.GONE
        fab.visibility=View.VISIBLE
    }

    fun recordAudioClose(){
        audio_input.visibility=View.GONE
        fab.visibility=View.VISIBLE
    }

    fun onBackPressed():Boolean{
        if (fab.visibility==View.GONE){
            text_input.visibility=View.GONE
            audio_input.visibility=View.GONE
            fab.visibility=View.VISIBLE
            return true
        }
        return false
    }

    private fun setupAlarmManager(){
        val alarmIntent =  Intent(activity, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, 0)
        val manager =  activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var interval:Long = 1000 * 60
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        manager.cancel(pendingIntent)
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            interval, pendingIntent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupAlarmManager()
        sharedPref = activity!!.getSharedPreferences(SHRD_PRF_KEY, 0)
        viewModel = ViewModelProviders.of(this.activity!!).get(MainViewModel::class.java)

        recycler_view.layoutManager = LinearLayoutManager(this.activity!!.applicationContext)
        adapter = TodoAdapter(this.activity!!.applicationContext,this)
        viewModel.getDataItems().observe(this, object : Observer<List<ToDoEntity>> {
            override fun onChanged(@Nullable words: List<ToDoEntity>) {
                if (words.isEmpty()){
                    empty_recycler.visibility=View.VISIBLE
                }
                else{
                    empty_recycler.visibility=View.INVISIBLE
                }
                adapter.setItems(words)
            }
        })
        recycler_view.adapter = adapter

        fab_open.setOnClickListener {
            fabOpen()
        }

        fab_close.setOnClickListener {
            fabClose()
        }

        fab_image.setOnClickListener {
            fabClose()
            dispatchTakePictureIntent()
        }

        fab_text.setOnClickListener {
            fabClose()
            fab.visibility = View.GONE
            text_input.visibility = View.VISIBLE
        }

        fab_audio.setOnClickListener {
            fabClose()
            fab.visibility = View.GONE
            audio_input.visibility = View.VISIBLE
        }

        close_text.setOnClickListener {
            addTextClose()
        }

        close_audio.setOnClickListener {
            recordAudioClose()
        }

        audio_record.setOnClickListener {
            if (ContextCompat.checkSelfPermission(activity!!,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(activity!!, permissions,0)
            } else {
                startRecording()
            }
        }

        audio_pause.setOnClickListener {
            pauseRecording()
        }

        audio_stop.setOnClickListener {
            stopRecording()
        }

        add_text.setOnClickListener {
            text_in.isFocusableInTouchMode = false
            text_in.setFocusable(false)
            text_in.isFocusableInTouchMode = true
            text_in.setFocusable(true)
            if (!text_in.text.isEmpty()){
                addText(text_in.text.toString())
                text_in.text.clear()
                addTextClose()
            }
        }


    }

}
