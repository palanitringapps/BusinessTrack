package com.esri.arcgisruntime.displayroute

import androidx.multidex.MultiDexApplication

import com.facebook.drawee.backends.pipeline.Fresco

class MainApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
    }
}
