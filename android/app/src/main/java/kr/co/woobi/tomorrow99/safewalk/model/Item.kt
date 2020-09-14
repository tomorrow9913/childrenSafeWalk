package kr.co.woobi.tomorrow99.safewalk.model

data class Item(
    var id: Int,
    var location: HashMap<String, Double>,
    var level: Double,
    var tag: List<Int>,
    var useful: HashMap<String, Double>,
)