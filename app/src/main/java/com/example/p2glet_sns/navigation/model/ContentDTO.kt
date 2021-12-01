package com.example.p2glet_sns.navigation.model

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-12-01
 * @desc
 */
data class ContentDTO(var explain : String? = null,
                      var imageUrl : String? = null,
                      var uid : String? = null,
                      var userId : String? = null,
                      var timestamp : Long? = null,
                      var favoriteCount : Int? = null,
                      var favorites : Map<String, Boolean> = HashMap()){

    data class Comment(var uid : String? = null,
                       var userId : String? = null,
                       var comment : String? = null,
                       var timestamp: Long? = null,)
}