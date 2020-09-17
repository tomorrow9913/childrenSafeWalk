package kr.co.woobi.tomorrow99.safewalk

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


/**
 * Created by SungBin on 2020-09-14.
 */

@HiltAndroidApp
class SafeWalk : Application() {

    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            // todo: 예상치 못한 오류 처리 -> 릴리즈 할 때 밑 코드 주석 지우기
            // ExceptionUtil.except(Exception(throwable), applicationContext)
        }
    }

}