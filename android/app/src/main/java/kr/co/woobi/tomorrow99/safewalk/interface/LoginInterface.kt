package kr.co.woobi.tomorrow99.safewalk.`interface`

import io.reactivex.rxjava3.core.Flowable
import kr.co.woobi.tomorrow99.safewalk.model.Login
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginInterface {
    @POST("login.php")
    fun login(
        @Body params: HashMap<String, String>
    ): Flowable<Login>
}