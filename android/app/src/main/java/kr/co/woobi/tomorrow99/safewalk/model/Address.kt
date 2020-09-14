package kr.co.woobi.tomorrow99.safewalk.model

data class Address(
    val name: String,
    val code: HashMap<String, String>,
    val region: HashMap<String, Area>,
)