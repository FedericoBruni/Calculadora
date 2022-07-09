package com.example.calculadora

import android.content.Context

class Prefs(context: Context) {
    val SHARED_NAME = "UserConfig"
    val VIBRATION_CONFIG = "VibrationConfig"
    val SOUND_CONFIG = "SoundConfig"
    val storage = context.getSharedPreferences(SHARED_NAME, 0)

    fun saveVibrationConfig(config: Boolean){
        storage.edit().putBoolean(VIBRATION_CONFIG, config).apply()
    }

    fun saveSoundConfig(config: Boolean){
        storage.edit().putBoolean(SOUND_CONFIG, config).apply()
    }

    fun getVibrationConfig() : Boolean {
        return storage.getBoolean(VIBRATION_CONFIG, false)
    }

    fun getSoundConfig() : Boolean {
        return storage.getBoolean(SOUND_CONFIG, false)
    }
}