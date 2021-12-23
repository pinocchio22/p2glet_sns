package com.example.p2glet_sns


import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.p2glet_sns.navigation.UserFragment
import com.example.p2glet_sns.navigation.model.AlarmDTO
import com.example.p2glet_sns.navigation.model.ContentDTO
import com.example.p2glet_sns.navigation.util.FcmPush
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_post.*
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
import java.util.*
import kotlin.collections.ArrayList


/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-12-21
 * @desc
 */
//class PostActivity : AppCompatActivity() {
//
//    var uid = FirebaseAuth.getInstance().currentUser?.uid
//    var firestore : FirebaseFirestore? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_post)
//
//        firestore = FirebaseFirestore.getInstance()
//
//        post_recyclerview.adapter = PostRecyclerViewAdapter()
//        post_recyclerview.layoutManager = LinearLayoutManager(this)
//    }
//
//    inner class PostRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//     var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
//        init {
//            firestore?.collection("images")?.orderBy("timestamp")?.whereEqualTo("uid", uid)?.addSnapshotListener { querySnapshot, firebaseFirestore ->
//                //Somtimes, This code return null of querySnapshot when it signout
//                contentDTOs.clear()
//                if (querySnapshot == null) return@addSnapshotListener
//
//                //Get data
//                for (snapshot in querySnapshot.documents) {
//                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
//                }
//                notifyDataSetChanged()
//            }
//        }
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            var width = resources.displayMetrics.widthPixels / 3
//            var imageView = ImageView(parent.context)
//            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
//            return CustomViewHolder(imageView)
//        }
//
//        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView) {
//
//        }
//
//        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            var imageView = (holder as CustomViewHolder).imageView
//            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageView)
//
//        }
//
//        override fun getItemCount(): Int {
//            return contentDTOs.size
//        }
//    }
//
//}
class PostActivity : AppCompatActivity() {

    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var documentId : MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        uid = intent.getStringExtra("userId")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        firestore = FirebaseFirestore.getInstance()

        post_recyclerview.adapter = PostRecyclerViewAdapter()
        post_recyclerview.layoutManager = LinearLayoutManager(this)
    }

    inner class PostRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
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
            Log.d("유알엘", (documentId.toString()))
            Log.d("포지션",position.toString())
//            Log.d("document", getDoc().toString())
//            getDoc(position)

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
            //This code is when the profile image is clicked
//            viewholder.detailviewitem_profile_image.setOnClickListener {
////                var fragment = UserFragment()
////                var bundle = Bundle()
////                bundle.putString("destinationUid", contentDTOs[position].uid)
////                bundle.putString("userId", contentDTOs[position].userId)
////                fragment.arguments = bundle
////                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content, fragment)?.commit()
//                var intent = Intent(this@PostActivity, UserFragment::class.java)
//                intent.putExtra("destinationUid", contentDTOs[position].uid)
//                intent.putExtra("userId", contentDTOs[position].userId)
//                startActivityForResult(intent, 201)
//            }
            viewholder.detailviewitem_comment_imageview.setOnClickListener { v ->
                var intent = Intent(v.context, ChatActivity2::class.java)
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                intent.putExtra("contentUid", contentUidList[position])
                startActivity(intent)
            }
            viewholder.toolbar_btn_back.setOnClickListener {
                onBackPressed()
            }
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
            firestore?.collection("images")?.document(contentUidList[position])?.collection("comment")?.document()?.delete()?.addOnSuccessListener {}
            firestore?.collection("images")?.document(contentUidList[position])?.delete()?.addOnSuccessListener {
//                    Log.d("삭제 성공", contentDTOs[position].imageUrl.toString())
                intent = Intent(this@PostActivity, PostActivity::class.java)
                startActivity(intent)
            }?.addOnFailureListener {
//                    Log.d("삭제 실패", contentDTOs[position].imageUrl.toString())
            }
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
            alarmDTO.timestamp = System.currentTimeMillis()
            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

            var message = FirebaseAuth.getInstance().currentUser?.email + getString(R.string.alarm_favorite)
            FcmPush.instance.sendMessage(destinationUid, "p2glet_sns", message)
        }
    }

//    fun getDoc(position: Int) {
//        firestore?.collection("images")?.get()?.addOnSuccessListener { task ->
//            for (document in task) {
//                documentId[position] = document.id
//                Log.d("도큐", documentId[position])
//            }
//        }
//    }
}