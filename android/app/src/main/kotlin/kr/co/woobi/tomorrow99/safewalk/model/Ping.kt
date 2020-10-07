package kr.co.woobi.tomorrow99.safewalk.model

data class SetPingIn(
    val session: String?,
    val latitude: String,
    val longitude: String,
    val level: Double?,
    val tag: List<Int>?,
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

data class addImageOut(
    var result: String
)