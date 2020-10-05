package kr.co.woobi.tomorrow99.safewalk.model

data class SetPingIn(
    var id: Int?,
    val session: String?,
    val radius: String,
    val latitude: String,
    val longitude: String,
    val level: Double?,
    val tag: List<Int>?,
    var result: String?,
    var comment: String?,
)

data class SetPingOut(
    val result: String?,
    val id:Int?,
    val comment: String?
)

data class GetPingIn(
    var radius: String,
    var latitude: String,
    var longitude: String
)