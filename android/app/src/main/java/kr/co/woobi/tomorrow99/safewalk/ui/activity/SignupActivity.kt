package kr.co.woobi.tomorrow99.safewalk.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kr.co.woobi.tomorrow99.safewalk.R


/**
 * Created by SungBin on 2020-09-14.
 */

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


    }
}