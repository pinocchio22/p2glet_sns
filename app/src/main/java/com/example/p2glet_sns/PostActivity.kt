package com.example.p2glet_sns


import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.p2glet_sns.navigation.UserFragment
import com.example.p2glet_sns.navigation.model.AlarmDTO
import com.example.p2glet_sns.navigation.model.ContentDTO
import com.example.p2glet_sns.navigation.util.FcmPush
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
import kotlinx.android.synthetic.main.item_post.view.*

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

    var firestore : FirebaseFirestore? = null
    var uid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        uid = intent.getStringExtra("userId")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        Log.d("유저", uid.toString())

        firestore = FirebaseFirestore.getInstance()

        post_recyclerview.adapter = PostRecyclerViewAdapter()
        post_recyclerview.layoutManager = LinearLayoutManager(this)
    }

    inner class PostRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        init {

            firestore?.collection("images")?.orderBy("timestamp")?.whereEqualTo("uid", uid)?.addSnapshotListener { querySnapshot, firebaseFirestore ->
                contentDTOs.clear()
                contentUidList.clear()

                //Sometimes, This code return null of querySnapshot when it sign-out
                if (querySnapshot == null) return@addSnapshotListener

                for(snapshot in querySnapshot!!.documents) {
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
            if (contentDTOs!![position].favorites.containsKey(uid)){
                //This is like status
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)
            }else {
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
        }
        fun favoriteEvent(position: Int) {
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction{ transaction ->

                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if (contentDTO!!.favorites.containsKey(uid)){
                    //when the button is clicked
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)
                }else {
                    //when the button is not clicked
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1
                    contentDTO?.favorites[uid!!] = true
                    favoriteAlarm(contentDTOs[position].uid!!)
                }
                transaction.set(tsDoc, contentDTO)
            }
        }

        fun favoriteAlarm (destinationUid : String) {
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
}
