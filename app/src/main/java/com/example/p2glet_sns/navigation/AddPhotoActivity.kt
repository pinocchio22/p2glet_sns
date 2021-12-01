package com.example.p2glet_sns.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.p2glet_sns.R
import com.google.firebase.storage.FirebaseStorage

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)
    }
}