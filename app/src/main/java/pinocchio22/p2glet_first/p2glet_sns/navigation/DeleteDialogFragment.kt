package pinocchio22.p2glet_first.p2glet_sns.navigation

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.IllegalStateException

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-12-29
 * @desc
 */


class DeleteDialogFragment : DialogFragment() {
    var firestore: FirebaseFirestore? = null
    var uid: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        return activity?.let {
            val mBuilder = AlertDialog.Builder(it)
            mBuilder.setMessage("모든 알림이 삭제됩니다")
                    .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        deletePost()
                    })
                    .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        dismiss()
                    })
            mBuilder.create()
        } ?: throw IllegalStateException("Activity is null")
    }

    fun deletePost(){
        firestore?.collection("alarm")?.document(uid!!)?.delete()
                ?.addOnSuccessListener {
                }
                ?.addOnFailureListener {
        }
    }
}