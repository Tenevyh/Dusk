package com.tenevyh.android.dusk.ui.login

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.tenevyh.android.dusk.R

class LoginErrorDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.login_error_msg)
            .setPositiveButton(R.string.retry, DialogInterface.OnClickListener{
                dialog, id ->
            })
            .setNegativeButton(R.string.exit, DialogInterface.OnClickListener{
                dialog, id ->
            })
        return builder.create()
    }
}