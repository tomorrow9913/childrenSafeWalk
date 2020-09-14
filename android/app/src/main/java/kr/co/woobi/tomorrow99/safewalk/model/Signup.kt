package kr.co.woobi.tomorrow99.safewalk.model

data class Signup(
    val result: String,      // success or fail
    val comment: String?,    // null or reason for error
)