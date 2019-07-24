package com.esri.arcgisruntime.displayroute

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.esri.arcgisruntime.displayroute.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeActivity : AppCompatActivity() {

    private lateinit var bind: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_home)
        init()
    }

    private fun init() {
        bind.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        TaskFragment.newInstance()
                    }
                    else -> {
                        TrackFragment.newInstance()
                    }
                }
            }

            override fun getItemCount(): Int {
                return 2
            }
        }

        TabLayoutMediator(bind.tabLayout, bind.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Tasks"
                else -> "Active Task"
            }
        }.attach()
    }
}