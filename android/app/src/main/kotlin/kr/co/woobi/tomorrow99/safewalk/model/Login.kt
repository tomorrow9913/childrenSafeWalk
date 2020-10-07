package kr.co.woobi.tomorrow99.safewalk.model

data class Login(
    val result: String,
    val comment: String?,
    val session: String,
    val nickname: String,
    val name: String,
    val email: String,
    val callNum: String?,
)