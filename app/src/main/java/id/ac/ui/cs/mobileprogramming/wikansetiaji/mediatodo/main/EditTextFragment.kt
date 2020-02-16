package com.example.lab4.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.R
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.model.TextEntity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.repository.MainViewModel
import kotlinx.android.synthetic.main.fragment_edit_text.*
import kotlinx.android.synthetic.main.main_fragment.text_in
import kotlinx.android.synthetic.main.main_fragment.toolbar


class EditTextFragment(var index: Int) : Fragment() {

    companion object {
        fun newInstance(index:Int) = EditTextFragment(index)
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_text, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this.activity!!).get(MainViewModel::class.java)
        val todoItem: TextEntity = viewModel.items.value!![index] as TextEntity
        toolbar.setNavigationOnClickListener {
            this.activity!!.onBackPressed()
        }
        this.text_in.setText(todoItem.text)
        this.title.text = "TODO "+(index+1).toString()+": "
        this.toolbar.title = "TODO "+(index+1).toString()
        this.text_in.requestFocus()
        if (text_in.text.isNotEmpty()){
            save.setOnClickListener {
                todoItem.text=text_in.text.toString()
                viewModel.editTextItem(todoItem)
                this.activity!!.onBackPressed()
            }
        }


    }

}
