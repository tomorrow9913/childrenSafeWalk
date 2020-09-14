package kr.co.woobi.tomorrow99.safewalk.`interface`

import io.reactivex.rxjava3.core.Flowable
import kr.co.woobi.tomorrow99.safewalk.model.PingInfo
import kr.co.woobi.tomorrow99.safewalk.model.RouteTarget
import retrofit2.http.Body
import retrofit2.http.POST

interface PingInfoInterface {
    @POST("loadPing.php")
    fun getPingData(
        @Body params: PingInfo
    ): Flowable<RouteTarget>
}