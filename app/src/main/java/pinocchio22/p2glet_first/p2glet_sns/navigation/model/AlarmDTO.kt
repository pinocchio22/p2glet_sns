package pinocchio22.p2glet_first.p2glet_sns.navigation.model

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-12-08
 * @desc
 */
data class AlarmDTO (
        var destinationUid : String? = null,
        var userId : String? = null,
        var uid : String? = null,

        //0 : like alarm
        //1 : comment alarm
        //2 : follow alarm
        var kind : Int? = null,
        var message : String? = null,
        var timestamp : String? = null
)