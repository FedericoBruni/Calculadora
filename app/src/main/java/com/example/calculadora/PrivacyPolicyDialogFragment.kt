package com.example.calculadora

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class PrivacyPolicyDialogFragment: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.privacy_policty_dialog_fragment, container, false)
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
            val acceptButton = dialog.findViewById<Button>(R.id.accept_pp_btn)
            acceptButton.setOnClickListener{
                dialog.dismiss()
            }
        }
    }
}