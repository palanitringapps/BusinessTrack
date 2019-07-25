package com.esri.arcgisruntime.displayroute

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class TaskListAdapter : RecyclerView.Adapter<DataBindViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.adapter_task_info, parent, false)

        return DataBindViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 10;
    }

    override fun onBindViewHolder(holder: DataBindViewHolder, position: Int) {
        holder.bind(TaskModel())

    }

}