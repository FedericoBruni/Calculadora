package com.flottapps.calculadora

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flottapps.calculadora.Calculator.Companion.prefs
import java.util.*

class SplashScreenActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (prefs.getFirstTimeConfig()) startViewPager()
        else startMainActivity()
    }

    private fun startViewPager() {
        loadLocate()
        val intent = Intent(this, ViewPagerActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loadLocate() {
        val language = prefs.getLanguageConfig()
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        prefs.saveLanguageConfig(language)
    }
}