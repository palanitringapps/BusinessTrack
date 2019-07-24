package com.esri.arcgisruntime.displayroute

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.esri.arcgisruntime.displayroute.databinding.FragmentTaskLayoutBinding

class TaskFragment : Fragment() {
    companion object {
        fun newInstance() = TaskFragment()
    }

    private lateinit var bind: FragmentTaskLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_task_layout, container, true)
        bind.listItem.layoutManager = LinearLayoutManager(activity)
        bind.listItem.adapter = TaskListAdapter()
        return bind.root
    }
}