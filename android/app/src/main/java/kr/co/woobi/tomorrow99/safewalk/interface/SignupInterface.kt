package kr.co.woobi.tomorrow99.safewalk.`interface`

import io.reactivex.rxjava3.core.Flowable
import kr.co.woobi.tomorrow99.safewalk.model.Signup
import retrofit2.http.Body
import retrofit2.http.POST

interface SignupInterface {
    @POST("registerUser.php")
    fun signup(
        @Body params: HashMap<String, String>
    ): Flowable<Signup>
}