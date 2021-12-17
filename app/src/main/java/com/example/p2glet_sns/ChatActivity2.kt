package com.example.p2glet_sns

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.p2glet_sns.navigation.model.ChatDTO2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat2.*
import kotlinx.android.synthetic.main.activity_chat2.view.*
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.item_chat2.view.*
import kotlinx.android.synthetic.main.item_comment.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-12-16
 * @desc
 */
class ChatActivity2 : AppCompatActivity() {
    var contentUid : String? = null
    var destinationUid : String? = null
    var firestore : FirebaseFirestore? = null
    var comments : ArrayList<ChatDTO2> = arrayListOf()
    var uid : String? = null

//    init {
//        firestore?.collection("images")?.addSnapshotListener { querySnapshot, firebaseFirestore ->
//            //Somtimes, This code return null of querySnapshot when it signout
//            if (querySnapshot == null) return@addSnapshotListener
//
//            //Get data
//            for (snapshot in querySnapshot.documents) {
//                chatDTO2.add(snapshot.toObject(ChatDTO2::class.java)!!)
//            }
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
        val curTime = dateFormat.format(Date(time))
        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat2)
        contentUid = intent.getStringExtra("contentUid")
        destinationUid = intent.getStringExtra("destinationUid")

        messageActivity_recyclerview.adapter = CommentRecyclerviewAdapter()
        messageActivity_recyclerview.layoutManager = LinearLayoutManager(this)

        messageActivity_ImageView?.setOnClickListener {
            var comment = ChatDTO2()
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.message = messageActivity_editText.text.toString()
            comment.timestamp = curTime

            FirebaseFirestore.getInstance().collection("images").document(contentUid!!).collection("comments").document().set(comment)
//            commentMessage(destinationUid!!,messageActivity_editText.text.toString())
            messageActivity_editText.setText("")
        }
    }

//    fun commentMessage(destinationUid : String, message : String) {
//        var chatDTO2 = ChatDTO2()
//        chatDTO2.destinationUid = destinationUid
//        chatDTO2.userId = FirebaseAuth.getInstance().currentUser?.email
//        chatDTO2.kind = 1
//        chatDTO2.uid = FirebaseAuth.getInstance().currentUser?.uid
//        chatDTO2.timestamp = System.currentTimeMillis()
//        chatDTO2.message = message
//        FirebaseFirestore.getInstance().collection("message").document().set(chatDTO2)
//
//        var msg = FirebaseAuth.getInstance().currentUser?.email + " " + getString(R.string.alarm_comment) + " of " + message
//        FcmPush.instance.sendMessage(destinationUid, "p2glet_sns", msg)
//    }
    inner class CommentRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        init {
            FirebaseFirestore.getInstance()
                    .collection("images")
                    .document(contentUid!!)
                    .collection("comments")
                    .orderBy("timestamp")
                    .addSnapshotListener { value, error ->
                        comments.clear()
                        if (value == null) return@addSnapshotListener
                        for (snapshot in value.documents!!)
                        {
                            comments.add(snapshot.toObject(ChatDTO2::class.java)!!)
                        }
                        notifyDataSetChanged()
                    }
            notifyDataSetChanged()
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat2,parent,false)
            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            Log.d("시작id", comments[position].userId.toString())
            var view = holder.itemView

            view.messageItem_textView_message.textSize = 20F
            view.messageItem_textView_message.text = comments[position].message
            view.messageItem_textview_name.text = comments[position].userId
            view.messageItem_textView_time.text = comments[position].timestamp
//            view.messageActivity_textView_topName.text = comments[position].userId

            FirebaseFirestore.getInstance().collection("profileImages").document(comments[position].uid!!).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var url = task.result!!["image"]
                    Glide.with(holder.itemView.context).load(url).apply(RequestOptions().circleCrop()).into(view.messageItem_imageview_profile)
                }
            }
//            Log.d("기준 id", comments[position].uid.toString())
//            Log.d("비교 id 1", contentUid.toString())
//            Log.d("비교 id 2", destinationUid.toString())
            if (comments[position].uid.equals(uid)){ //본인 채팅
                view.messageItem_textView_message.setBackgroundResource(R.drawable.rightbubble)
                view.messageItem_textView_message.visibility = View.VISIBLE
                view.messageItem_textview_name.visibility = View.VISIBLE
                view.messageItem_linearlayout_main.gravity = Gravity.RIGHT
            }else { //상대방 채팅
                view.messageItem_layout_destination.visibility = View.VISIBLE
                view.messageItem_textview_name.visibility = View.VISIBLE
                view.messageItem_textView_message.setBackgroundResource(R.drawable.leftbubble)
                view.messageItem_linearlayout_main.gravity = Gravity.LEFT
            }
        }

        override fun getItemCount(): Int {
            return comments.size
        }

    }
}