package com.example.calculadora

import android.content.Context

class Prefs(context: Context) {
    private val sharedName = "UserConfig"
    private val vibrationConfig = "VibrationConfig"
    private val soundConfig = "SoundConfig"
    private val languageConfigs = "languageConfigs"
    private val readingConfigs = "ReadingConfig"
    private val darkModeConfig = "DarkModeConfig"
    private val storage = context.getSharedPreferences(sharedName, 0)

    fun saveVibrationConfig(config: Boolean){
        storage.edit().putBoolean(vibrationConfig, config).apply()
    }

    fun saveSoundConfig(config: Boolean){
        storage.edit().putBoolean(soundConfig, config).apply()
    }

    fun saveLanguageConfig(config: String){
        storage.edit().putString(languageConfigs, config).apply()
    }

    fun saveReadingConfig(config: Boolean) {
        storage.edit().putBoolean(readingConfigs, config).apply()
    }

    fun saveDarkModeConfig(config: Boolean) {
        storage.edit().putBoolean(darkModeConfig, config).apply()
    }

    fun getVibrationConfig() : Boolean {
        return storage.getBoolean(vibrationConfig, false)
    }

    fun getSoundConfig() : Boolean {
        return storage.getBoolean(soundConfig, false)
    }

    fun getLanguageConfig() : String {
        return storage.getString(languageConfigs, "en")!!
    }

    fun getReadingConfig() : Boolean {
        return storage.getBoolean(readingConfigs, false)
    }

    fun getDarkModeConfig() : Boolean {
        return storage.getBoolean(darkModeConfig, true)
    }
}