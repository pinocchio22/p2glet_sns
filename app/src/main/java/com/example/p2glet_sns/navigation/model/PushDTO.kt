package com.example.p2glet_sns.navigation.model

import android.app.Notification

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-12-09
 * @desc
 */
data class PushDTO (
        var to : String? = null,
        var notification : Notification = Notification()
){
    data class Notification(
        var body : String? = null,
        var title : String? = null
    )
}