package com.flottapps.calculadora

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class PrivacyPolicyDialogFragment: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.privacy_policty_dialog_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setListeners(dialog)
        }
    }

    private fun setListeners(dialog: Dialog) {
        val acceptButton = dialog.findViewById<Button>(R.id.accept_pp_btn)
        acceptButton.setOnClickListener{
            dialog.dismiss()
        }
        val googlePlayPP = dialog.findViewById<TextView>(R.id.tv_google_play_PP)
        googlePlayPP.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://policies.google.com/privacy")))
        }

        val googleAnalyticsPP = dialog.findViewById<TextView>(R.id.tv_google_analytics_PP)
        googleAnalyticsPP.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://support.google.com/analytics/answer/6004245?hl=en")))
        }

        val googleAdmobPP = dialog.findViewById<TextView>(R.id.tv_google_admob_PP)
        googleAdmobPP.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://support.google.com/admob/answer/6128543?hl=en")))
        }

        val firebasePP = dialog.findViewById<TextView>(R.id.tv_firebase_PP)
        firebasePP.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://firebase.google.com/support/privacy/?hl=es-419")))
        }
    }
}