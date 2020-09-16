package kr.co.woobi.tomorrow99.safewalk.model

data class Ping(
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