package pinocchio22.p2glet_first.p2glet_sns.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.p2glet_first.p2glet_sns.R
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.AlarmDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_alarm.view.*
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_post.view.*

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-11-30
 * @desc
 */
class AlarmFragment : Fragment() {
    var firestore: FirebaseFirestore? = null
    var uid: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_alarm, container, false)

        view.alarmfragment_recyclerview.adapter = AlarmRecyclerviewAdapter()
        view.alarmfragment_recyclerview.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class AlarmRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var alarmDTOList: ArrayList<AlarmDTO> = arrayListOf()

        init {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid", uid).addSnapshotListener { value, error ->
                alarmDTOList.clear()
                if (value == null) return@addSnapshotListener

                for (snapshot in value.documents) {
                    alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {  //메모리 효율성 증가
        }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                var viewholder = (holder as CustomViewHolder).itemView

                FirebaseFirestore.getInstance().collection("profileImages").document(alarmDTOList[position].uid!!).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val url = task.result!!["image"]
                        Glide.with(viewholder.context).load(url).apply(RequestOptions().circleCrop()).into(viewholder.commentviewitem_imageview_profile)
                    }
                }

                when (alarmDTOList[position].kind) {
                    0 -> {
                        val str_0 = alarmDTOList[position].userId + getString(R.string.alarm_favorite)
                        viewholder.alarm_content.text = str_0
                    }
                    1 -> {
                        val str_0 = alarmDTOList[position].userId + " " + getString(R.string.alarm_comment) + "\" " + alarmDTOList[position].message + " \""
                        viewholder.alarm_content.text = str_0
                    }
                    2 -> {
                        val str_0 = alarmDTOList[position].userId + " " + getString(R.string.alarm_follow)
                        viewholder.alarm_content.text = str_0
                    }
                    3 -> {
                        val str_0 = alarmDTOList[position].userId + " " + getString(R.string.report_post)
                        viewholder.alarm_content.text = str_0
                    }
                    4 -> {
                        val str_0 = alarmDTOList[position].userId + " " + getString(R.string.report_User)
                        viewholder.alarm_content.text = str_0
                    }
                }
            }

            override fun getItemCount(): Int {
                return alarmDTOList.size
            }
    }
}