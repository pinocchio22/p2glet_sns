package pinocchio22.p2glet_first.p2glet_sns.navigation.model

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
                      var count : Int = 0,
                      var report : MutableMap<String, Boolean> = HashMap(),
                      var favoriteCount : Int = 0,
                      var favorites : MutableMap<String, Boolean> = HashMap()){

    data class Comment(var uid : String? = null,
                       var userId : String? = null,
                       var comment : String? = null,
                       var timestamp: String? = null)
}