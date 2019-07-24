package com.esri.arcgisruntime.displayroute

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.esri.arcgisruntime.displayroute.databinding.AdapterTaskInfoBinding


class DataBindViewHolder(private val bind: ViewDataBinding) : RecyclerView.ViewHolder(bind.root) {
    private lateinit var binder: AdapterTaskInfoBinding
    fun bind(data: Any) {
        bind.setVariable(BR.model, data)
        if (data is TaskModel) {
            if (bind is AdapterTaskInfoBinding) {
                binder = bind
            }
        }
        bind.executePendingBindings()
    }
}