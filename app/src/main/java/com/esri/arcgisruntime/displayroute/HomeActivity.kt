package com.esri.arcgisruntime.displayroute

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.esri.arcgisruntime.displayroute.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import android.content.Intent


class HomeActivity : AppCompatActivity() {

    private lateinit var bind: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_home)
        init()
        val receive = intent
        val name = receive!!.getStringExtra("Name")
        val uri = Uri.parse(getString(R.string.photo))
        bind.ivProfile.setImageURI(uri)
        bind.tvName.text = "Hello, " + name
    }

    private fun init() {
        bind.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        TaskFragment.newInstance()
                    }
                    else -> {
                        TrackGoogleFragment()
                        //TrackFragment.newInstance()
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
                else -> "Active"
            }
        }.attach()
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
        //super.onBackPressed()

    }
}