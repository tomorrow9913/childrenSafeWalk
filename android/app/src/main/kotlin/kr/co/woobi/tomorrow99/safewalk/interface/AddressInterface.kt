package kr.co.woobi.tomorrow99.safewalk.`interface`

import io.reactivex.rxjava3.core.Flowable
import kr.co.woobi.tomorrow99.safewalk.model.AddressResult
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface AddressInterface {
    @GET("map-reversegeocode/v2/gc")
    @Headers(
        "X-NCP-APIGW-API-KEY-ID: 7fxx28ikcf",
        "X-NCP-APIGW-API-KEY: kolAzQiZCPl37GQlLV5R3fCbPvFY9F0oSPIQWZWm"
    )
    fun getAddress(
        @Query("coords") coordinators: String,
        @Query("sourcecrs") sourcecrs: String,
        @Query("orders") orders: String,
        @Query("output") output: String,
    ): Flowable<AddressResult>
}