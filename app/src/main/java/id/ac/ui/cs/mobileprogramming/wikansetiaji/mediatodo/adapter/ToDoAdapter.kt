package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.MainActivity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.R
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.AudioEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.ImageEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.TextEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.ToDoEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.main.MainFragment
import kotlinx.android.synthetic.main.image_item.view.*
import kotlinx.android.synthetic.main.audio_item.view.*
import kotlinx.android.synthetic.main.text_item.view.*
import kotlinx.android.synthetic.main.text_item.view.check
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import io.github.ponnamkarthik.richlinkpreview.ViewListener
import java.net.MalformedURLException
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class TodoAdapter(val context: Context, val fragment:MainFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items : ArrayList<ToDoEntity> = arrayListOf()

    fun setItems(items:List<ToDoEntity>){
        this.items = ArrayList(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==0){
            return TextViewHolder(LayoutInflater.from(context).inflate(R.layout.text_item, parent, false))
        }
        else if(viewType==1){
            return ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.image_item, parent, false))
        }
        else{
            return AudioViewHolder(LayoutInflater.from(context).inflate(R.layout.audio_item, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            0 -> (holder as TextViewHolder).bindView(position)
            1 -> (holder as ImageViewHolder).bindView(position)
            2 -> (holder as AudioViewHolder).bindView(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items.get(position).getType()
    }

    inner class TextViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val richLinkView = view.richLinkView
        val edit = view.edit
        val check = view.check

        fun verifyAvailableNetwork():Boolean{
            val connectivityManager=fragment.activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo=connectivityManager.activeNetworkInfo
            return  networkInfo!=null && networkInfo.isConnected
        }

        private fun findUrl():String{
            val p = Pattern.compile("[^.]+[.][^.]+")
            val scanner = Scanner(check.text.toString())
            while (scanner.hasNext()) {
                if (scanner.hasNext(p)) {
                    var possibleUrl = scanner.next(p)
                    if (!possibleUrl.contains("://")) {
                        possibleUrl = "https://$possibleUrl"
                    }

                    try {
                        return possibleUrl
                    } catch (e: MalformedURLException) {
                        continue
                    }

                } else {
                    scanner.next()
                }
            }
            return ""
        }

        fun bindView(position: Int){
            this.setIsRecyclable(false)
            val item = this@TodoAdapter.items.get(position) as TextEntity
            edit.setOnClickListener {
                fragment.onEditTextItem(position)
            }
            check.text=item.text
            check.setOnClickListener {
                check.isSelected=true
                fragment.deleteText(item)
            }
            val url = findUrl()
            if (verifyAvailableNetwork() && url.isNotEmpty()){
                richLinkView.setLink(url, object : ViewListener {
                    override fun onSuccess(status: Boolean) {

                    }
                    override fun onError(e: Exception) {

                    }
                })
                richLinkView.setDefaultClickListener(false)

                richLinkView.setClickListener { _, meta ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meta.url))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.setPackage("com.android.chrome")

                    try {
                        context.startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        intent.setPackage(null)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    inner class ImageViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val view = view.view
        val check = view.check

        fun bindView(position: Int){
            this.setIsRecyclable(false)
            val item = this@TodoAdapter.items.get(position) as ImageEntity
            view.setOnClickListener {
                fragment.onViewImageItem(position)
            }
            check.text=item.name
            check.setOnClickListener {
                check.isSelected=true
                fragment.deleteImage(item)
            }
        }
    }

    inner class AudioViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val play = view.play
        val text = view.text
        val time = view.time

        fun bindView(position: Int){
            this.setIsRecyclable(false)
            val item = this@TodoAdapter.items.get(position) as AudioEntity
            text.text = item.name
            play.setOnClickListener {
                (fragment.getActivity()!! as MainActivity).onPlayAudioOpen(position)
            }
            val mediaPlayer = MediaPlayer()

            try {
                mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().absolutePath + "/${item.name}")
                mediaPlayer.prepare()
                val duration:Int = mediaPlayer.duration
                val second:Int = (fragment.activity as MainActivity).milliToSecond(duration)
                val minute:Int = (fragment.activity as MainActivity).milliToMinute(duration)
                val hour:Int = (fragment.activity as MainActivity).milliToHour(duration)
                time.text = "${hour}:${minute}:${second}"
            } catch (e: Exception) {
                e.printStackTrace()
            }

            text.setOnClickListener {
                text.isSelected=true
                fragment.deleteAudio(item)
            }

        }
    }

}
