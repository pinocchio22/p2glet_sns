package pinocchio22.p2glet_first.p2glet_sns.navigation.model

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