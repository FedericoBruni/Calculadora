package com.example.calculadora

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(5000)
        super.onCreate(savedInstanceState)
//        Timer().schedule(object : TimerTask() {
//            override fun run() {
//                startActivity(Intent(applicationContext, MainActivity::class.java))
//            }
//        }, 5000)
        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }
}