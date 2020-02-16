package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders

import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.R
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.ImageEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.repository.MainViewModel
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.tools.ImageSaver
import kotlinx.android.synthetic.main.fragment_view_image.*
import kotlinx.android.synthetic.main.main_fragment.toolbar

class ViewImageFragment(var index: Int) : Fragment() {

    companion object {
        fun newInstance(index:Int) = ViewImageFragment(index)
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_image, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this.activity!!).get(MainViewModel::class.java)
        val todoItem: ImageEntity = viewModel.items.value!![index] as ImageEntity
        toolbar.setNavigationOnClickListener {
            this.activity!!.onBackPressed()
        }
        this.toolbar.title = todoItem.name
        val imageSaver = ImageSaver(activity!!)
        var bitmap = imageSaver.
            setFileName(todoItem.name).
            setDirectoryName("images").
            load()
        this.image.setImageBitmap(bitmap)
    }

}