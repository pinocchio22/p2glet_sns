//package com.example.p2glet_sns
//
//import android.os.Bundle
//import android.os.PersistableBundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.LinearLayoutCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.RequestOptions
//import com.example.p2glet_sns.navigation.UserFragment
//import com.example.p2glet_sns.navigation.model.ContentDTO
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.android.synthetic.main.activity_post.*
//import kotlinx.android.synthetic.main.fragment_user.*
//import kotlinx.android.synthetic.main.fragment_user.view.*
//
///**
// * @author CHOI
// * @email vviian.2@gmail.com
// * @created 2021-12-21
// * @desc
// */
//class PostActivity : AppCompatActivity() {
//
//    var firestore : FirebaseFirestore? = null
//
//    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
//        firestore = FirebaseFirestore.getInstance()
//
//        super.onCreate(savedInstanceState, persistentState)
//        setContentView(R.layout.activity_main)
//
//        post_recyclerview.adapter = PostRecyclerViewAdapter()
//        post_recyclerview.layoutManager = LinearLayoutManager(this)
//    }
//
//    inner class PostRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//     var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
////        var uid = intent.getStringExtra("destinationUid")
//        init {
////            Log.d("널", uid.toString())
//            firestore?.collection("images")?.addSnapshotListener { querySnapshot, firebaseFirestore ->
//                //Somtimes, This code return null of querySnapshot when it signout
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
//            var imageView = (holder as UserFragment.UserFragmentRecyclerViewAdapter.CustomViewHolder).imageView
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