package com.example.calculadora

import android.app.Application
import com.google.android.gms.ads.*


class Calculator : Application() {

    companion object {
        lateinit var prefs: Prefs
    }

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        prefs = Prefs(applicationContext)
    }
}