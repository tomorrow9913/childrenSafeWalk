package kr.co.woobi.tomorrow99.safewalk.model

data class Login(
    val result: String,       // success or fail
    val comment: String?,     // null or reason for error
    val session: String,      // encrypt session id
    val nickname: String,     // nickname@고유번호
    val name: String,         // name
    val email: String,        // email
    val phoneNumber: String?, // null or 긴급 연락처
)