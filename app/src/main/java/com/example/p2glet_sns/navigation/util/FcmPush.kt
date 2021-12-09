package com.example.p2glet_sns.navigation.util

import com.example.p2glet_sns.navigation.model.PushDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-12-09
 * @desc
 */
class FcmPush {

    var JSON = MediaType.parse("application/json; charset=utf-8")
    var url = "https://fcm.googleapis.com/fcm/send"
//    var serverKey = "AIzaSyAW2-esfc8y8rLKetWHXvVvNLfvC_EtX1o"
    var serverKey = "AAAAcSMHTL0:APA91bEQdcROu-yw7DMnnL5Q8A8PtEqnA_R2U5Bag28xCMnkKg4t2VcrPWP5FgEMse484Z4_PSP8AlbFcbw1d7aoZNWcEYsmALV0lIOtY9Kj6m0a7OBzGi08-o6234o6qZyts0gXMYHG"
    var gson : Gson? = null
    var okHttpClient : OkHttpClient? = null

    companion object{
        var instance = FcmPush()
    }

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage (destinationUid : String, title : String, message : String) {
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener {
            task ->
            if (task.isSuccessful) {
                var token = task?.result?.get("pushToken").toString()
                var pushDTO = PushDTO()

                pushDTO.to = token
                pushDTO.notification.titile = title
                pushDTO.notification.body = message

                var body = RequestBody.create(JSON,gson?.toJson(pushDTO))
                var request = Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "key="+serverKey)
                    .url(url)
                    .post(body)
                    .build()

                okHttpClient?.newCall(request)?.enqueue(object : Callback{
                    override fun onFailure(call: Call?, e: IOException?) {
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        println(response?.body()?.string())
                    }

                })
            }
        }
    }
}