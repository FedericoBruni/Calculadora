package com.example.calculadora

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

private const val COUNTER_TIME = 2L

private const val LOG_TAG = "SplashActivity"

class SplashScreenActivity : AppCompatActivity() {

    private var secondsRemaining: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startMainActivity()
    }

    fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}