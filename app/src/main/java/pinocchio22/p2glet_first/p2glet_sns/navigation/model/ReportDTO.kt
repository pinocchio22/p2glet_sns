package pinocchio22.p2glet_first.p2glet_sns.navigation.model

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2022-01-14
 * @desc
 */
data class ReportDTO (
    var destinationUid : String? = null,
    var userId : String? = null,
    var uid : String? = null,
    var count : Int = 0,
    var report : MutableMap<String, Boolean> = HashMap())