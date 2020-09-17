package kr.co.woobi.tomorrow99.safewalk.model

data class AddressResult(
    var error: HashMap<String, String>,
    var status: HashMap<String, String>,
    var results: List<Address>?,
)