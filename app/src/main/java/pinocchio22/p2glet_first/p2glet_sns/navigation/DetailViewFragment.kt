package pinocchio22.p2glet_first.p2glet_sns.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.p2glet_first.p2glet_sns.R
import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import kotlinx.android.synthetic.main.item_detail.*
import kotlinx.android.synthetic.main.item_detail.view.*
import pinocchio22.p2glet_first.p2glet_sns.ChatActivity
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.AlarmDTO
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.ContentDTO
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.FollowDTO
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.ReportDTO
import pinocchio22.p2glet_first.p2glet_sns.navigation.util.FcmPush
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-11-30
 * @desc
 */
class DetailViewFragment : Fragment() {
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    val time = System.currentTimeMillis()

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
    val curTime = dateFormat.format(Date(time))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)

        DetailViewRecyclerViewAdapter().notifyDataSetChanged()

        return view
    }

    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()
        var reportDTO: ArrayList<ReportDTO> = arrayListOf()

        init {
            view?.detailviewfragment_recyclerview?.removeAllViews()
            firestore?.collection("report")?.orderBy("destinationUid")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    reportDTO.clear()
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot.documents) {
                        var item = snapshot.toObject(ReportDTO::class.java)
                        reportDTO.add(item!!)
                    }
                    notifyDataSetChanged()
                }
            firestore?.collection("images")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()

                    //Sometimes, This code return null of querySnapshot when it sign-out
                    if (querySnapshot == null) return@addSnapshotListener

                    //if you report some users, hide reported user's post
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        if (reportDTO.size == 0) {
                            contentDTOs.add(item!!)
                            contentUidList.add(snapshot.id)
                        }
                        for (i in 0 until reportDTO.size) {
                            contentUidList.add(snapshot.id)
                            when {
                                item?.uid == reportDTO[i].destinationUid && reportDTO[i].report.containsKey(item?.uid) -> {
                                    //block user's post
                                }
                                else -> {
                                    contentDTOs.add(item!!)
                                }
                            }
                        }
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_detail,
                parent,
                false
            )
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {  //메모리 효율성 증가

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = (holder as CustomViewHolder).itemView

            //UserId
            viewholder.detailviewitem_profile_textview.text = contentDTOs!![position].userId

            //Image
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(
                viewholder.detailviewitem_imageview_content
            )

            //Explain of content
            viewholder.detailviewitem_explain_textview.text = contentDTOs!![position].explain

            //likes
            viewholder.detailviewitem_favoritecounter_textview.text = "Likes " + contentDTOs!![position].favoriteCount

//            //ProfileImage
//            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholder.detailviewitem_profile_image)
            firestore?.collection("profileImages")?.document(contentDTOs[position].uid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot == null) return@addSnapshotListener
                    if (documentSnapshot.data != null) {
                        var url = documentSnapshot?.data!!["image"]
//                Log.d("액티비티", requireContext().toString())
                        Glide.with(context).load(url).apply(RequestOptions().circleCrop()).into(
                            viewholder.detailviewitem_profile_image
                        )
                    }
                }

            //This code is when the button is clicked
            viewholder.detailviewitem_favorite_imageview.setOnClickListener {
                favoriteEvent(position)
            }
            //This code is when the page is loaded
            if (contentDTOs!![position].favorites.containsKey(uid)) {
                //This is like status
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)
            } else {
                //This is unlike status
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }
            //This code is when the profile image is clicked
            viewholder.detailviewitem_profile_image.setOnClickListener {
                var fragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid", contentDTOs[position].uid)
                bundle.putString("userId", contentDTOs[position].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(
                    R.id.main_content,
                    fragment
                )?.commit()
            }
            viewholder.detailviewitem_comment_imageview.setOnClickListener { v ->
                var intent = Intent(v.context, ChatActivity::class.java)
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                intent.putExtra("contentUid", contentUidList[position])
                startActivity(intent)
            }

            //Block abusive user
//            removerUser(position, holder)
        }

        fun favoriteEvent(position: Int) {
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->

                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
                if (contentDTO!!.favorites.containsKey(uid)) {
                    //when the button is clicked
                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                    contentDTO.favorites.remove(uid)
                } else {
                    //when the button is not clicked
                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                    contentDTO.favorites[uid!!] = true
                    favoriteAlarm(contentDTOs[position].uid!!)
                }
                transaction.set(tsDoc, contentDTO)
                return@runTransaction
            }
        }

        fun favoriteAlarm(destinationUid: String) {
            var alarmDTO = AlarmDTO()
            alarmDTO.destinationUid = destinationUid
            alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
            alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
            alarmDTO.kind = 0
            alarmDTO.timestamp = curTime
            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

            var message =
                FirebaseAuth.getInstance().currentUser?.email + getString(R.string.alarm_favorite)
            FcmPush.instance.sendMessage(destinationUid, "p2glet_sns", message)
        }

//        fun removerUser(position: Int, holder: RecyclerView.ViewHolder) {
//            var tsDoc = firestore?.collection("report")?.document(uid!!)
//            firestore?.runTransaction { transaction ->
//                var reportDTO = transaction.get(tsDoc!!).toObject(ReportDTO::class.java)
//                if (contentDTOs[position].uid == reportDTO?.destinationUid && reportDTO!!.report.containsKey(
//                        contentDTOs[position].uid
//                    )
//                ) {
//                    Log.d("ww1", contentDTOs.toString())
//                    refreshAdapter(contentDTOs, position)
//                    adapter?.notifyDataSetChanged()
//                }
//            }
//        }

//        fun refreshAdapter(data: MutableList<ContentDTO>, position: Int) {
//            data.removeAt(position)
//            contentDTOs2.addAll(data)
//            data.clear()
//            data.addAll(contentDTOs2)
//            adapter?.notifyItemInserted(position)
//            adapter?.notifyItemRemoved(position)
//        }
    }
}