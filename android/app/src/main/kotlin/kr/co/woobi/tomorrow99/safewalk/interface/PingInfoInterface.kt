package kr.co.woobi.tomorrow99.safewalk.`interface`

import io.reactivex.rxjava3.core.Flowable
import kr.co.woobi.tomorrow99.safewalk.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface PingInfoInterface {
    @POST("loadPing.php")
    fun getPingData(
        @Body params: GetPingIn,
    ): Flowable<RouteTarget>

    @POST("addPing.php")
    fun addPing(
        @Body params: SetPingIn
    ): Flowable<SetPingOut>
}