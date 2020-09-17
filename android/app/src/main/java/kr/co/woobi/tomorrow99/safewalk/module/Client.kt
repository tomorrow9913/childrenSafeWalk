package kr.co.woobi.tomorrow99.safewalk.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


/**
 * Created by SungBin on 2020-09-17.
*/

@Module
@InstallIn(ApplicationComponent::class)
class Client {
    private val baseUrl = "http://210.107.245.192:400/"
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okhttpClient: OkHttpClient
        get() {
            return OkHttpClient.Builder()
                .addInterceptor {
                    val request = it.request().newBuilder().addHeader(
                        "Content-Type",
                        "application/json"
                    ).build()
                    it.proceed(request)
                }
                .addInterceptor(logging)
                .build()
        }


    @Singleton
    @Provides
    fun instance() =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okhttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
}