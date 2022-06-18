package com.example.calculadora

import android.app.Application
import com.google.android.gms.ads.*


class Calculadora : Application() {


    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)

    }
}