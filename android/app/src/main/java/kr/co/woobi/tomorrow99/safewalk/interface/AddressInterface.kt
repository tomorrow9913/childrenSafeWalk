package kr.co.woobi.tomorrow99.safewalk.`interface`

import io.reactivex.rxjava3.core.Flowable
import kr.co.woobi.tomorrow99.safewalk.R
import kr.co.woobi.tomorrow99.safewalk.model.AddressResult
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface AddressInterface {

    @GET("map-reversegeocode/v2/gc")
    @Headers(
        "X-NCP-APIGW-API-KEY-ID: ${R.string.naver_map_key_id}",
        "X-NCP-APIGW-API-KEY: ${R.string.naver_map_key}"
    )
    fun getAddress(
        @Query("coords") coordinators: String,
        @Query("sourcecrs") sourcecrs: String, // epsg:4326
        @Query("orders") orders: String, // legalcode
        @Query("output") output: String // json
    ): Flowable<AddressResult>
}