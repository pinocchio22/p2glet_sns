package pinocchio22.p2glet_first.p2glet_sns


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.p2glet_first.p2glet_sns.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import kotlinx.android.synthetic.main.item_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.detailviewitem_comment_imageview
import kotlinx.android.synthetic.main.item_detail.view.detailviewitem_explain_textview
import kotlinx.android.synthetic.main.item_detail.view.detailviewitem_favorite_imageview
import kotlinx.android.synthetic.main.item_detail.view.detailviewitem_favoritecounter_textview
import kotlinx.android.synthetic.main.item_detail.view.detailviewitem_imageview_content
import kotlinx.android.synthetic.main.item_post.*
import kotlinx.android.synthetic.main.item_post.view.*
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.AlarmDTO
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.ContentDTO
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.ReportDTO
import pinocchio22.p2glet_first.p2glet_sns.navigation.util.FcmPush
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-12-21
 * @desc
 */
class PostActivity : AppCompatActivity() {

    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var contentDTOs : ArrayList<ContentDTO> = arrayListOf()

    val time = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
    val curTime = dateFormat.format(Date(time))

    override fun onCreate(savedInstanceState: Bundle?) {

        uid = intent.getStringExtra("userId")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        firestore = FirebaseFirestore.getInstance()

        post_recyclerview.adapter = PostRecyclerViewAdapter()
        post_recyclerview.layoutManager = LinearLayoutManager(this)
    }

    inner class PostRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentUidList: ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("images")?.orderBy("timestamp")?.whereEqualTo("uid", uid)?.addSnapshotListener { querySnapshot, firebaseFirestore ->
                contentDTOs.clear()
                contentUidList.clear()

                //Sometimes, This code return null of querySnapshot when it sign-out
                if (querySnapshot == null) return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {  //메모리 효율성 증가

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var viewholder = (holder as CustomViewHolder).itemView

            if (uid == FirebaseAuth.getInstance().currentUser?.uid) {
                viewholder.toolbar_delete.visibility = View.VISIBLE
            }else{
                viewholder.toolbar_delete.visibility = View.GONE
            }

            //UserId
            viewholder.toolbar_username.text = contentDTOs!![position].userId

            //Image
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholder.detailviewitem_imageview_content)

            //Explain of content
            viewholder.detailviewitem_explain_textview.text = contentDTOs!![position].explain

            //likes
            viewholder.detailviewitem_favoritecounter_textview.text = "Likes" + contentDTOs!![position].favoriteCount

//            //ProfileImage
//            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholder.detailviewitem_profile_image)

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

            //This is chat
            viewholder.detailviewitem_comment_imageview.setOnClickListener { v ->
                var intent = Intent(v.context, ChatActivity::class.java)
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                intent.putExtra("contentUid", contentUidList[position])
                startActivity(intent)
            }

            //This is back button
            viewholder.toolbar_btn_back.setOnClickListener {
                onBackPressed()
            }

            //This is report button
            viewholder.toolbar_report.visibility = View.VISIBLE
            viewholder.toolbar_report.setOnClickListener {
                var builder = AlertDialog.Builder(this@PostActivity)
                builder.setTitle("신고 하시겠습니까?")
                builder.setMessage("확인 버튼을 누르면 해당 게시물이 신고되고 삭제됩니다.")

                var listener = object  : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when(p1) {
                            DialogInterface.BUTTON_POSITIVE ->
                                reportPost(position, contentDTOs[position].uid!!)
                            DialogInterface.BUTTON_NEGATIVE ->
                                finish()
                        }
                    }
                }
                builder.setPositiveButton("확인", listener)
                builder.setNegativeButton("취소", listener)
                builder.show()
            }

            //This is delete button
            viewholder.toolbar_delete.setOnClickListener {
                var builder = AlertDialog.Builder(this@PostActivity)
                builder.setTitle("삭제 하시겠습니까?")
                builder.setMessage("확인 버튼을 누르면 해당 게시물이 삭제됩니다.")

                var listener = object  : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when(p1) {
                            DialogInterface.BUTTON_POSITIVE ->
                                deletePost(position)
                            DialogInterface.BUTTON_NEGATIVE ->
                                finish()
                        }
                    }
                }
                builder.setPositiveButton("확인", listener)
                builder.setNegativeButton("취소", listener)
                builder.show()
            }
        }

        fun deletePost(position: Int){
            firestore?.collection("images")?.document(contentUidList[position])?.collection("comment")?.document()?.delete()?.addOnSuccessListener {
                notifyDataSetChanged()
            }?.addOnFailureListener {}
            firestore?.collection("images")?.document(contentUidList[position])?.delete()?.addOnSuccessListener {
                finish()
                notifyDataSetChanged()
            }?.addOnFailureListener {}
        }
        fun reportPost(position: Int, destinationUid: String) {
            // report alarm
            var reportDTO = ReportDTO()
            reportDTO.destinationUid = destinationUid
            reportDTO.userId = FirebaseAuth.getInstance().currentUser?.email
            reportDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
            reportDTO.count = reportDTO.count?.plus(1)
            FirebaseFirestore.getInstance().collection("report").document().set(reportDTO)

            var message = FirebaseAuth.getInstance().currentUser?.email + getString(R.string.report_post)
            FcmPush.instance.sendMessage(destinationUid, "p2glet_sns", message)

            //delete post
            firestore?.collection("images")?.document(contentUidList[position])?.collection("comment")?.document()?.delete()?.addOnSuccessListener (object : OnSuccessListener<Void?> {
                override fun onSuccess(p0: Void) {
                    val intent = Intent(this@PostActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            })?.addOnFailureListener {}
            firestore?.collection("images")?.document(contentUidList[position])?.delete()?.addOnSuccessListener {

                notifyDataSetChanged()
            }?.addOnFailureListener {}
        }

        fun favoriteEvent(position: Int) {
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->

                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
                Log.d("콘텐", contentDTO.toString())

                if (contentDTO!!.favorites.containsKey(uid)) {
                    //when the button is clicked
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)
                } else {
                    //when the button is not clicked
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1
                    contentDTO?.favorites[uid!!] = true
                    favoriteAlarm(contentDTOs[position].uid!!)
                }
                transaction.set(tsDoc, contentDTO)
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

            var message = FirebaseAuth.getInstance().currentUser?.email + getString(R.string.alarm_favorite)
            FcmPush.instance.sendMessage(destinationUid, "p2glet_sns", message)
        }
    }

//    private fun deleteItem(position: Int) {
//        mStorage.getReference().child("userImages").child("uid/").child(contentslist.get(position).photoName).delete()
//                .addOnSuccessListener(object : OnSuccessListener<Void?> {
//                    fun onSuccess(aVoid: Void?) {
//                        // removeValue 말고 setValue(null)도 삭제가능
//                        mDatabase.getReference().child("contents").child("content").child(uidLists.get(position)).removeValue()
//                                .addOnSuccessListener(object : OnSuccessListener<Void?> {
//                                    fun onSuccess(aVoid: Void?) {
//                                        Toast.makeText(this@Today_MainActivity, "삭제 완료", Toast.LENGTH_LONG).show()
//                                    }
//                                }).addOnFailureListener(OnFailureListener
//                                // DB에서 Fail날경우는 거의 없음..
//                                {
//                                    // fail ui
//                                })
//                    }
//                }).addOnFailureListener(OnFailureListener { Toast.makeText(this@Today_MainActivity, "삭제 실패", Toast.LENGTH_LONG).show() })
//    }
}