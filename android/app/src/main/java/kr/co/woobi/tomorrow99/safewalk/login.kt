package kr.co.woobi.tomorrow99.safewalk

import retrofit2.Call
import retrofit2.http.*

data class LoginOut (
    var result:String,      //"result": "success" or "fail"
    var comment:String? ,   //"reason for error"
    var session:String?,    //"encript session Id"
    var nickname:String?,   //"nickname@고유번호
    var name:String?,       //"name"
    var email:String?,      //"email",
    var callNum: String?    // null or "긴급 연락처"
)

data class UserInfo(
    var session:String?,    //"encript session Id"
    var nickname:String?,   //"nickname@고유번호
    var name:String?,       //"name"
    var email:String?,      //"email",
    var callNum: String?    // null or "긴급 연락처"
)

interface LoginService {
    //@FormUrlEncoded
    @POST(value = "login.php")
    @Headers("Content-Type: application/json")

    fun requestLogin (
        @Body params: HashMap<String, String>
    ) : Call<LoginOut>
}