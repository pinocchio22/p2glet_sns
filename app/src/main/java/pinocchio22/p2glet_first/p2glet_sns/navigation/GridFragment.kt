package pinocchio22.p2glet_first.p2glet_sns.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.p2glet_first.p2glet_sns.R
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_grid.view.*
import kotlinx.android.synthetic.main.item_detail.view.*
import pinocchio22.p2glet_first.p2glet_sns.MainActivity
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.ReportDTO

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-11-30
 * @desc
 */
class GridFragment : Fragment() {

    var firestore : FirebaseFirestore? = null
    var fragmentView : View? = null
    var uid : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uid = FirebaseAuth.getInstance().currentUser?.uid
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_grid,container,false)
        firestore = FirebaseFirestore.getInstance()
        fragmentView?.gridfragment_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.gridfragment_recyclerview?.layoutManager = GridLayoutManager(activity, 3)
        return fragmentView
    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var reportDTO: ArrayList<ReportDTO> = arrayListOf()
        init {
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
            firestore?.collection("report")?.orderBy("destinationUid")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    reportDTO.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ReportDTO::class.java)

                        // block user 제외
                        reportDTO.add(item!!)
                    }
                    notifyDataSetChanged()
                }
            firestore?.collection("images")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()

                    //Sometimes, This code return null of querySnapshot when it sign-out
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)

                        if (reportDTO.size == 0) {
                            contentDTOs.add(item!!)
                        }
                        for (i in 0 until reportDTO.size) {
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
            var width = resources.displayMetrics.widthPixels / 3
            var imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageView)
        }

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView) {

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageView = (holder as CustomViewHolder).imageView
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageView)
            Log.d("wwgrid",contentDTOs.toString())
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }
    }
}