package com.example.calculadora

import android.content.Context

class Prefs(context: Context) {
    val SHARED_NAME = "UserConfig"
    val VIBRATION_CONFIG = "VibrationConfig"
    val SOUND_CONFIG = "SoundConfig"
    val LANGUAGE_CONFIG = "LanguageConfig"
    val READING_CONFIG = "ReadingConfig"
    val storage = context.getSharedPreferences(SHARED_NAME, 0)

    fun saveVibrationConfig(config: Boolean){
        storage.edit().putBoolean(VIBRATION_CONFIG, config).apply()
    }

    fun saveSoundConfig(config: Boolean){
        storage.edit().putBoolean(SOUND_CONFIG, config).apply()
    }

    fun saveLanguageConfig(config: String){
        storage.edit().putString(LANGUAGE_CONFIG, config).apply()
    }

    fun saveReadingConfig(config: Boolean) {
        storage.edit().putBoolean(READING_CONFIG, config).apply()
    }

    fun getVibrationConfig() : Boolean {
        return storage.getBoolean(VIBRATION_CONFIG, false)
    }

    fun getSoundConfig() : Boolean {
        return storage.getBoolean(SOUND_CONFIG, false)
    }

    fun getLanguageConfig() : String {
        return storage.getString(LANGUAGE_CONFIG, "en")!!
    }

    fun getReadingConfig() : Boolean {
        return storage.getBoolean(READING_CONFIG, false)
    }
}