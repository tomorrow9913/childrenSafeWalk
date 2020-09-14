package kr.co.woobi.tomorrow99.safewalk.model

data class User(
    var session: String,    // encrypt session id
    var nickname: String,   // nickname@고유번호
    var name: String,       // name
    var email: String,      // email
    var callNum: String?,  // null or 긴급 연락처
)