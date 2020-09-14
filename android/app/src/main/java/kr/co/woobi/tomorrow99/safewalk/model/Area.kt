package kr.co.woobi.tomorrow99.safewalk.model

data class Area(
    val name: String,
    val coordinators: HashMap<String, Center>,
)