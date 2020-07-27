package kr.co.woobi.tomorrow99.safewalk

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


data class LoginOut (
    var result:String,      //"result": "success" or "fail"
    var session:String?,    //"encript session Id"
    var comment:String?     //"reason for error"
)

interface LoginService {
    @FormUrlEncoded
    @POST(value = "login.php")

    fun requestLogin (
        @Field(value = "id") id:String,
        @Field(value = "pwd") pwd: String
    ) : Call<LoginOut>
}