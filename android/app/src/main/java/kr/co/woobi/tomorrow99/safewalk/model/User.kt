package kr.co.woobi.tomorrow99.safewalk.model

data class User(
    var session: String,
    var nickname: String,
    var name: String,
    var email: String,
    var callNum: String?,
)