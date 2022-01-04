package com.example.p2glet_sns

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.p2glet_sns.navigation.model.ChatDTO
import com.example.p2glet_sns.navigation.util.FcmPush
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.view.*
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.item_chat.view.*
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
class ChatActivity : AppCompatActivity() {
    var contentUid : String? = null
    var destinationUid : String? = null
    var firestore : FirebaseFirestore? = null
    var comments : ArrayList<ChatDTO> = arrayListOf()
    var uid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
        val curTime = dateFormat.format(Date(time))
        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        contentUid = intent.getStringExtra("contentUid")
        destinationUid = intent.getStringExtra("destinationUid")

        messageActivity_recyclerview.adapter = CommentRecyclerviewAdapter()
        messageActivity_recyclerview.layoutManager = LinearLayoutManager(this)

        messageActivity_ImageView?.setOnClickListener {
            var comment = ChatDTO()
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.message = messageActivity_editText.text.toString()
            comment.timestamp = curTime

            FirebaseFirestore.getInstance().collection("images").document(contentUid!!).collection("comments").document().set(comment)
            commentMessage(destinationUid!!,messageActivity_editText.text.toString())
            messageActivity_editText.setText("")
        }
    }

    fun commentMessage(destinationUid : String, message : String) {
        var chatDTO = ChatDTO()
        chatDTO.destinationUid = destinationUid
        chatDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        chatDTO.kind = 1
        chatDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
        chatDTO.timestamp = System.currentTimeMillis().toString()
        chatDTO.message = message
        FirebaseFirestore.getInstance().collection("alarms").document().set(chatDTO)

        var msg = FirebaseAuth.getInstance().currentUser?.email + " " + getString(R.string.alarm_comment) + "\" " + message + " \""
        FcmPush.instance.sendMessage(destinationUid, "p2glet_sns", msg)
    }
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
                        for (snapshot in value.documents)
                        {
                            comments.add(snapshot.toObject(ChatDTO::class.java)!!)
                        }
                        notifyDataSetChanged()
                    }
            notifyDataSetChanged()
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat,parent,false)
            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        @SuppressLint("RtlHardcoded")   // RtlHardcoded = 검사중요도
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            Log.d("시작id", comments[position].userId.toString())
            var view = holder.itemView

            view.messageItem_textView_message.textSize = 20F
            view.messageItem_textView_message.text = comments[position].message
            view.messageItem_textview_name.text = comments[position].userId
            view.messageItem_textView_time.text = comments[position].timestamp
//            view.messageActivity_textView_topName.text = destinationUid

            FirebaseFirestore.getInstance().collection("profileImages").document(comments[position].uid!!).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var url = task.result!!["image"]
                    Glide.with(holder.itemView.context).load(url).apply(RequestOptions().circleCrop()).into(view.messageItem_imageview_profile)
                }
            }
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