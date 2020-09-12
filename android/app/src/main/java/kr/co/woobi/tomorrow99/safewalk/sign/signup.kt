package kr.co.woobi.tomorrow99.safewalk.sign

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class SignupOut (
    var result:String,      //"result": "success" or "fail"
    var comment:String?     //"reason for error"
)

interface SignUpService {
    //@FormUrlEncoded
    @POST("registerUser.php")
    @Headers("Content-Type: application/json")

    fun requestSignUP (
        @Body params: HashMap<String, String?>
    ) : Call<SignupOut>
}