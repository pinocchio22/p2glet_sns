package com.example.p2glet_sns.navigation

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-12-29
 * @desc
 */

private const val NOTI_MESSAGE_KEY = "noti"
class DeleteDialogFragment : DialogFragment() {
    companion object {
        const val TAG = "TestDialog"
        @JvmStatic
        fun newInstance(notiMessage : String) =
                DeleteDialogFragment().apply {
                    arguments = Bundle().apply { putString(NOTI_MESSAGE_KEY, notiMessage) }
                }
    }
    private var notiMessage : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { notiMessage = it.getString(NOTI_MESSAGE_KEY) }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val mBuilder = AlertDialog.Builder(it)
            mBuilder.setMessage(notiMessage)
                    .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialogInterface, i ->

                    })
                    .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        dismiss()
                    })
            mBuilder.create()
        } ?: throw IllegalStateException("Activity is null")
    }
}
