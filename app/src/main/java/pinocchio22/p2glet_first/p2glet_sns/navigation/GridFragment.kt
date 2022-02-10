package pinocchio22.p2glet_first.p2glet_sns.navigation

import android.os.Bundle
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
        init {
            firestore?.collection("images")?.addSnapshotListener { querySnapshot, firebaseFirestore ->
                //Somtimes, This code return null of querySnapshot when it signout
                if (querySnapshot == null) return@addSnapshotListener

                //Get data
                for (snapshot in querySnapshot.documents) {
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
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
            removerUser(position, holder)
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        fun removerUser(position: Int, holder: RecyclerView.ViewHolder) {
            var tsDoc = firestore?.collection("report")?.document(uid!!)
            firestore?.runTransaction { transaction ->
                var reportDTO = transaction.get(tsDoc!!).toObject(ReportDTO::class.java)
                if (contentDTOs[position].uid == reportDTO?.destinationUid && reportDTO!!.report.containsKey(
                        contentDTOs[position].uid
                    )) {
                    //hide blocked users
                    holder.itemView.detailviewitem_main.visibility = View.GONE
                    notifyDataSetChanged()
                }else {
                    holder.itemView.detailviewitem_main.visibility = View.VISIBLE
                    notifyDataSetChanged()
                }
            }
        }

    }
}