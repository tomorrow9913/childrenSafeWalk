package kr.co.woobi.tomorrow99.safewalk.`interface`

import io.reactivex.rxjava3.core.Flowable
import kr.co.woobi.tomorrow99.safewalk.model.AddressResult
import kr.co.woobi.tomorrow99.safewalk.model.addImageOut
import okhttp3.Call
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageInterface {
    @Multipart
    @POST("addImagePing.php.php")
    fun postImage(
        @Part imageFile : MultipartBody.Part
    ): Flowable<addImageOut>
}