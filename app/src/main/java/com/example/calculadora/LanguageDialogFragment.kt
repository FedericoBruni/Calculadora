package com.example.calculadora

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.DialogFragment
import java.util.*

class LanguageDialogFragment : DialogFragment() {

    private lateinit var baseContext: Context
    private lateinit var activityMain: Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.language_dialog_fragment, container, false)
        return rootView
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            //val width = ViewGroup.LayoutParams.MATCH_PARENT
            //val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //dialog.getWindow()?.setLayout(width, height)
            setListeners(dialog)
        }
    }
    fun act(activity: Activity){
        activityMain = activity
    }
    override fun onAttach(context: Context) {
        baseContext = context
        super.onAttach(context)
    }
    fun on(context: Context) {
        baseContext = context
    }
    private fun setListeners(dialog: Dialog) {
        val langSpanish = dialog.findViewById<RadioButton>(R.id.lang_spanish)
        val listLanguages = listOf("English", "Spanish")
        langSpanish.setOnClickListener{
            //Guardar idioma.
            dialog.dismiss()
            setLocate("es")

        }
    }

    @Suppress("DEPRECATION")
    private fun setLocate(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        recreate(activityMain)

    }

}