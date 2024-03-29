package pinocchio22.p2glet_first.p2glet_sns.navigation

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.p2glet_first.p2glet_sns.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import kotlinx.android.synthetic.main.item_post.view.*
import pinocchio22.p2glet_first.p2glet_sns.LoginActivity
import pinocchio22.p2glet_first.p2glet_sns.MainActivity
import pinocchio22.p2glet_first.p2glet_sns.PostActivity
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.AlarmDTO
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.ContentDTO
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.FollowDTO
import pinocchio22.p2glet_first.p2glet_sns.navigation.model.ReportDTO
import pinocchio22.p2glet_first.p2glet_sns.navigation.util.FcmPush
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-11-30
 * @desc
 */
class UserFragment : Fragment() {

    var fragmentView: View? = null
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserUid: String? = null

    val time = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
    val curTime = dateFormat.format(Date(time))

    companion object {
        var PICK_PROFILE_FROM_ALBUM = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = LayoutInflater.from(activity).inflate(
            R.layout.fragment_user,
            container,
            false
        )
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid

        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton("확인") { dialogInterface, i ->
                    reportUser(uid!!)
                    val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.beginTransaction().remove(this@UserFragment).commit()
                    fragmentManager.popBackStack()

                }
                setNegativeButton("취소") { dialogInterface, i ->
                }
            }
            builder.create()
        }


        if (uid == currentUserUid) {
            //MyPage
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.signout)
            fragmentView?.account_btn_follow_signout?.setOnClickListener {
                activity?.finish()
                startActivity(Intent(activity, LoginActivity::class.java))
                auth?.signOut()

            }
            fragmentView?.account_iv_profile?.setOnClickListener {
                var photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
            }
        } else {
            //OtherUserPage
            fragmentView?.account_iv_profile?.setOnClickListener {
                // 프로필 확대
                Toast.makeText(context, "상대방 프로필", Toast.LENGTH_SHORT).show()
            }
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)
            var mainactivity = (activity as MainActivity)
            mainactivity.toolbar_username?.text = arguments?.getString("userId")
            mainactivity.toolbar_btn_back?.setOnClickListener {
                mainactivity.bottom_navigation.selectedItemId = R.id.action_home
            }
            mainactivity.toolbar_title_image?.visibility = View.GONE
            mainactivity.toolbar_username?.visibility = View.VISIBLE
            mainactivity.toolbar_btn_back?.visibility = View.VISIBLE
            mainactivity.user_report?.visibility = View.VISIBLE
            mainactivity.user_report?.setOnClickListener {
                //This is report button
                alertDialog?.setMessage("확인을 누르시면 사용자가 신고됩니다.")
                alertDialog?.show()
            }
            fragmentView?.account_btn_follow_signout?.setOnClickListener {
                requestFollow()
            }
        }

        fragmentView?.account_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.account_recyclerview?.layoutManager = GridLayoutManager(activity, 3)
        UserFragmentRecyclerViewAdapter().notifyDataSetChanged()

        getProfileImage()
        getFollowerAndFollowing()
        return fragmentView
    }

    fun getFollowerAndFollowing() {
        firestore?.collection("users")?.document(uid!!)?.addSnapshotListener { value, error ->
            if (value == null) return@addSnapshotListener
            var followDTO = value.toObject(FollowDTO::class.java)
            if (followDTO?.followingCount != null) {
                fragmentView?.account_tv_following_count?.text = followDTO?.followingCount?.toString()
            }
            if (followDTO?.followerCount != null) {
                fragmentView?.account_tv_follower_count?.text = followDTO?.followerCount?.toString()
                if (followDTO?.followers?.containsKey(currentUserUid)) {
                    fragmentView?.account_btn_follow_signout?.text = requireActivity().getString(R.string.follow_cancel)
                    fragmentView?.account_btn_follow_signout?.background?.setColorFilter(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.colorLightGray
                        ), PorterDuff.Mode.MULTIPLY
                    )
                } else {
                    if (context == null) {
                    } else {
                        if (uid != currentUserUid) {
                            fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)
                            fragmentView?.account_btn_follow_signout?.background?.colorFilter = null
                        }
                    }
                }
            }
        }
    }

    fun requestFollow() {
        //Save data to my account
        var tsDocFollowing = firestore?.collection("users")?.document(currentUserUid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO!!.followingCount = 1
                followDTO!!.followers[uid!!] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction
            }

            if (followDTO.followings.containsKey(uid)) {
                //It remove following third person when a third person follow me
                followDTO.followingCount = followDTO?.followingCount - 1
                followDTO.followers?.remove(uid)
            } else {
                //It add following third person when a third person do not follow me
                followDTO.followingCount = followDTO?.followingCount + 1
                followDTO.followers[uid!!] = true
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }
        //Save data to third person
        var tsDocFollower = firestore?.collection("users")?.document(uid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true
                followerAlarm(uid!!)
                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }
            if (followDTO!!.followers.containsKey(currentUserUid)) {
                //It cancel my follower when I follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid)
            } else {
                //It add my follower when I don't follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid!!] = true
                followerAlarm(uid!!)
            }
            transaction.set(tsDocFollower, followDTO!!)
            return@runTransaction
        }
    }

    fun followerAlarm(destinationUid: String) {
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = auth?.currentUser?.email
        alarmDTO.uid = auth?.currentUser?.uid
        alarmDTO.kind = 2
        alarmDTO.timestamp = curTime
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message = auth?.currentUser?.email + getString(R.string.alarm_follow)
        FcmPush.instance.sendMessage(destinationUid, "p2glet_sns", message)
    }

    fun getProfileImage() {
        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot == null) return@addSnapshotListener
            if (documentSnapshot.data != null) {
                var url = documentSnapshot?.data!!["image"]
                Glide.with(activity).load(url).apply(RequestOptions().circleCrop()).into(
                    fragmentView?.account_iv_profile
                )
            }
        }
    }

    fun reportUser(destinationUid: String) {
        //report count
        reportAlarm(uid!!)
        var tsDoc = firestore?.collection("report")?.document(currentUserUid!!)
        firestore?.runTransaction { transaction ->
            var reportDTO = transaction.get(tsDoc!!).toObject(ReportDTO::class.java)
            if (reportDTO == null) {
                reportDTO = ReportDTO()
                reportDTO.count = reportDTO.count + 1
                reportDTO.userId = FirebaseAuth.getInstance().currentUser?.email
                reportDTO.report[uid!!] = true
                reportDTO.destinationUid = destinationUid

                transaction.set(tsDoc, reportDTO)
                return@runTransaction
            }
            if (reportDTO!!.report.containsKey(uid)) {
                reportDTO.count = reportDTO.count + 1
                if (reportDTO.count == 3) {
                    // 신고 3회가 쌓이면 관리자에 의하여 삭제
                }
            } else {
                //when the button is not clicked
                reportDTO.count = reportDTO.count + 1
                reportDTO.report[uid!!] = true
            }
            transaction.set(tsDoc, reportDTO)
            return@runTransaction
        }
    }

    fun reportAlarm(destinationUid: String) {
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.kind = 4
        alarmDTO.timestamp = curTime
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message = FirebaseAuth.getInstance().currentUser?.email + getString(R.string.report_post)
        FcmPush.instance.sendMessage(destinationUid, "p2glet_sns", message)
    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("images")?.whereEqualTo("uid", uid)?.addSnapshotListener { querySnapshot, firebaseFirestore ->
                //Somtimes, This code return null of querySnapshot when it signout
                if (querySnapshot == null) return@addSnapshotListener
                //Get data
                for (snapshot in querySnapshot.documents) {
                    contentDTOs.add(snapshot?.toObject(ContentDTO::class.java)!!)
                }
                fragmentView?.account_tv_post_count?.text = contentDTOs.size.toString()
                notifyDataSetChanged()
            }
            Log.d("wwuser", contentDTOs.toString())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3
            var imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            imageView.setBackgroundResource(R.drawable.item_border)
            return CustomViewHolder(imageView)
        }


        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView) {
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageView = (holder as CustomViewHolder).imageView
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(
                RequestOptions().centerCrop()
            ).into(imageView)
            holder.imageView.setOnClickListener {
                var intent = Intent(context, PostActivity::class.java)
                intent.putExtra("userId", uid)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }
    }
}