package pinocchio22.p2glet_first.p2glet_sns.navigation.model

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-12-06
 * @desc
 */
data class FollowDTO (
        var followerCount : Int = 0,
        var followers : MutableMap<String, Boolean> = HashMap(),

        var followingCount : Int = 0,
        var followings : MutableMap<String, Boolean> = HashMap()
)