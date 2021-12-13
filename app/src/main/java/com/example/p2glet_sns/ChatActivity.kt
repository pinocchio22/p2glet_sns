//package com.example.p2glet_sns
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.util.Log
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.RequestOptions
//import com.example.p2glet_sns.navigation.model.ChatDTO
//import com.google.firebase.auth.FirebaseAuth
//import kotlinx.android.synthetic.main.activity_chat.*
//import java.text.SimpleDateFormat
//import java.util.*
//import kotlin.collections.ArrayList
//
///**
// * @author CHOI
// * @email vviian.2@gmail.com
// * @created 2021-12-09
// * @desc
// */
//class ChatActivity : AppCompatActivity() {
//
////    private val fireDatabase = FirebaseDatabase.getInstance().reference
//    private var chatRoomUid : String? = null
//    private var destinationUid : String? = null
//    private var uid : String? = null
//    private var recyclerView : RecyclerView? = null
//
//    @SuppressLint("SimpleDateFormat")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_chat)
//        val imageView = findViewById<ImageView>(R.id.messageActivity_ImageView)
//        val editText = findViewById<TextView>(R.id.messageActivity_editText)
//
//        //메세지를 보낸 시간
//        val time = System.currentTimeMillis()
//        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
//        val curTime = dateFormat.format(Date(time)).toString()
//
//        destinationUid = intent.getStringExtra("destinationUid")
//        uid = FirebaseAuth.getInstance().currentUser?.uid
//        recyclerView = findViewById(R.id.messageActivity_recyclerview)
//
//        imageView.setOnClickListener {
//            Log.d("클릭 시 dest", "$destinationUid")
//            val chatModel = ChatDTO()
//            chatModel.users.put(uid.toString(), true)
//            chatModel.users.put(destinationUid!!, true)
//
//            val comment = ChatDTO.Comment(uid, editText.text.toString(), curTime)
//            if(chatRoomUid == null){
//                imageView.isEnabled = false
//                fireDatabase.child("chatrooms").push().setValue(chatModel).addOnSuccessListener {
//                    //채팅방 생성
//                    checkChatRoom()
//                    //메세지 보내기
//                    Handler().postDelayed({
//                        println(chatRoomUid)
//                        fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
//                        messageActivity_editText.text = null
//                    }, 1000L)
//                    Log.d("chatUidNull dest", "$destinationUid")
//                }
//            }else{
//                fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
//                messageActivity_editText.text = null
//                Log.d("chatUidNotNull dest", "$destinationUid")
//            }
//        }
//        checkChatRoom()
//    }
//
//    private fun checkChatRoom(){
//        fireDatabase.child("chatrooms").orderByChild("users/$uid").equalTo(true)
//            .addListenerForSingleValueEvent(object : ValueEventListener{
//                override fun onCancelled(error: DatabaseError) {
//                }
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (item in snapshot.children){
//                        println(item)
//                        val chatModel = item.getValue<ChatModel>()
//                        if(chatModel?.users!!.containsKey(destinationUid)){
//                            chatRoomUid = item.key
//                            messageActivity_ImageView.isEnabled = true
//                            recyclerView?.layoutManager = LinearLayoutManager(this@MessageActivity)
//                            recyclerView?.adapter = RecyclerViewAdapter()
//                        }
//                    }
//                }
//            })
//    }
//
//
//    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder>() {
//
//        private val comments = ArrayList<ChatDTO.Comment>()
//        private var friend : Friend? = null
//        init{
//            fireDatabase.child("users").child(destinationUid.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
//                override fun onCancelled(error: DatabaseError) {
//                }
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    friend = snapshot.getValue<Friend>()
//                    messageActivity_textView_topName.text = friend?.name
////                    getMessageList()
//                }
//            })
//        }
//
////        fun getMessageList(){
////            fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").addValueEventListener(object : ValueEventListener{
////                override fun onCancelled(error: DatabaseError) {
////                }
////                override fun onDataChange(snapshot: DataSnapshot) {
////                    comments.clear()
////                    for(data in snapshot.children){
////                        val item = data.getValue<Comment>()
////                        comments.add(item!!)
////                        println(comments)
////                    }
////                    notifyDataSetChanged()
////                    //메세지를 보낼 시 화면을 맨 밑으로 내림
////                    recyclerView?.scrollToPosition(comments.size - 1)
////                }
////            })
////        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
//            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
//
//            return MessageViewHolder(view)
//        }
//        @SuppressLint("RtlHardcoded")
//        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
//            holder.textView_message.textSize = 20F
//            holder.textView_message.text = comments[position].message
//            holder.textView_time.text = comments[position].time
//            if(comments[position].uid.equals(uid)){ // 본인 채팅
////                holder.textView_message.setBackgroundResource(R.drawable.rightbubble)
//                holder.textView_name.visibility = View.INVISIBLE
//                holder.layout_destination.visibility = View.INVISIBLE
//                holder.layout_main.gravity = Gravity.RIGHT
//            }else{ // 상대방 채팅
//                Glide.with(holder.itemView.context)
//                    .load(friend?.profileImageUrl)
//                    .apply(RequestOptions().circleCrop())
//                    .into(holder.imageView_profile)
//                holder.textView_name.text = friend?.name
//                holder.layout_destination.visibility = View.VISIBLE
//                holder.textView_name.visibility = View.VISIBLE
////                holder.textView_message.setBackgroundResource(R.drawable.leftbubble)
//                holder.layout_main.gravity = Gravity.LEFT
//            }
//        }
//
//        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            val textView_message: TextView = view.findViewById(R.id.messageItem_textView_message)
//            val textView_name: TextView = view.findViewById(R.id.messageItem_textview_name)
//            val imageView_profile: ImageView = view.findViewById(R.id.messageItem_imageview_profile)
//            val layout_destination: LinearLayout = view.findViewById(R.id.messageItem_layout_destination)
//            val layout_main: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_main)
//            val textView_time : TextView = view.findViewById(R.id.messageItem_textView_time)
//        }
//
//        override fun getItemCount(): Int {
//            return comments.size
//        }
//
//    }
//}

package com.example.p2glet_sns

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.p2glet_sns.R
import com.example.p2glet_sns.navigation.model.AlarmDTO
import com.example.p2glet_sns.navigation.model.ChatDTO
import com.example.p2glet_sns.navigation.model.ContentDTO
import com.example.p2glet_sns.navigation.util.FcmPush
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.item_chat.*
import kotlinx.android.synthetic.main.item_comment.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    val fireDatabase = FirebaseDatabase.getInstance().reference
    var chatRoomUid : String? = null
    var contentUid : String? = null
    var destinationUid : String? = null
    private var uid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        contentUid = intent.getStringExtra("contentUid")
        destinationUid = intent.getStringExtra("destinationUid")

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()

        messageActivity_ImageView.setOnClickListener {
            Log.d("클릭 시 dest", "$destinationUid")
            val chatModel = ChatDTO()
            chatModel.users.put(contentUid!!, true)
            chatModel.users.put(destinationUid!!, true)

            val comment = ChatDTO.Comment(contentUid, editText.toString(), curTime)
            if(chatRoomUid == null){
                messageActivity_ImageView.isEnabled = false
                fireDatabase.child("chatrooms").push().setValue(chatModel).addOnSuccessListener {
                    //채팅방 생성
                    checkChatRoom()
                    //메세지 보내기
                    Handler().postDelayed({
                        println(chatRoomUid)
                        fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                        messageActivity_editText.text = null
                    }, 1000L)
                    Log.d("chatUidNull dest", "$destinationUid")
                }
            }else{
                fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                messageActivity_editText.text = null
                Log.d("chatUidNotNull dest", "$destinationUid")
            }
        }
    }

    private fun checkChatRoom() {
        fireDatabase.child("chatrooms").orderByChild("users/$uid").equalTo(true)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (item in snapshot.children) {
                            println(item)
                            val chatModel = item.getValue<ChatDTO>()
                            if (chatModel?.users!!.containsKey(destinationUid)) {
                                chatRoomUid = item.key
                                messageActivity_ImageView.isEnabled = true
                                messageActivity_recyclerview?.layoutManager = LinearLayoutManager(this@ChatActivity)
                                messageActivity_recyclerview?.adapter = CommentRecyclerviewAdapter()
                            }
                        }
                    }
                })
    }

//    fun commentAlarm(destinationUid : String, message : String) {
//        var alarmDTO = AlarmDTO()
//        alarmDTO.destinationUid = destinationUid
//        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
//        alarmDTO.kind = 1
//        alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
//        alarmDTO.timestamp = System.currentTimeMillis()
//        alarmDTO.message = message
//        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
//
//        var msg = FirebaseAuth.getInstance().currentUser?.email + " " + getString(R.string.alarm_comment) + " of " + message
//        FcmPush.instance.sendMessage(destinationUid, "p2glet_sns", msg)
//    }
    inner class CommentRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var comments : ArrayList<ChatDTO.Comment> = arrayListOf()
//        init {
//            FirebaseFirestore.getInstance()
//                    .collection("images")
//                    .document(contentUid!!)
//                    .collection("comments")
//                    .orderBy("timestamp")
//                    .addSnapshotListener { value, error ->
//                        comments.clear()
//                        if (value == null) return@addSnapshotListener
//                        for (snapshot in value.documents!!)
//                        {
//                            comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
//                        }
//                        notifyDataSetChanged()
//                    }
//        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false)
            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

//        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            var view = holder.itemView
//            view.commentviewitem_textview_comment.text = comments[position].comment
//            view.commentviewitem_textview_profile.text = comments[position].userId
//
//            FirebaseFirestore.getInstance().collection("profileImages").document(comments[position].uid!!).get().addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    var url = task.result!!["image"]
//                    Glide.with(holder.itemView.context).load(url).apply(RequestOptions().circleCrop()).into(view.commentviewitem_imageview_profile)
//                }
//            }
//        }
        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            messageItem_textView_message.textSize = 20F
            messageItem_textView_message.text = comments[position].message
            messageItem_textView_time.text = comments[position].time
            if(comments[position].uid.equals(uid)){ // 본인 채팅
//                holder.textView_message.setBackgroundResource(R.drawable.rightbubble)
                messageItem_textview_name.visibility = View.INVISIBLE
                messageItem_layout_destination.visibility = View.INVISIBLE
                messageItem_linearlayout_main.gravity = Gravity.RIGHT
            }else{ // 상대방 채팅
                Glide.with(holder.itemView.context)
                    .load(friend?.profileImageUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(messageItem_imageview_profile)
                messageItem_textview_name.text = friend?.name
                messageItem_layout_destination.visibility = View.VISIBLE
                messageItem_textview_name.visibility = View.VISIBLE
//                holder.textView_message.setBackgroundResource(R.drawable.leftbubble)
                messageItem_linearlayout_main.gravity = Gravity.LEFT
            }
        }
//    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            val textView_message: TextView = view.findViewById(R.id.messageItem_textView_message)
//            val textView_name: TextView = view.findViewById(R.id.messageItem_textview_name)
//            val imageView_profile: ImageView = view.findViewById(R.id.messageItem_imageview_profile)
//            val layout_destination: LinearLayout = view.findViewById(R.id.messageItem_layout_destination)
//            val layout_main: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_main)
//            val textView_time : TextView = view.findViewById(R.id.messageItem_textView_time)
//        }

        override fun getItemCount(): Int {
            return comments.size
        }

    }
}