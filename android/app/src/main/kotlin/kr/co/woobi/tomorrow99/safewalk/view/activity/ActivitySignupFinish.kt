package kr.co.woobi.tomorrow99.safewalk.view.activity

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_signup_finish.*
import kr.co.woobi.tomorrow99.safewalk.R
import org.jetbrains.anko.startActivity


/**
 * Created by SungBin on 2020-09-17.
 */

class ActivitySignupFinish : AppCompatActivity (){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_finish)

        tv_signup_done.run {
            val ssb = SpannableStringBuilder(text)
            ssb.setSpan( // 회원가입 텍스트 부분 색 변경
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)),
                0,
                4,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text = ssb
        }

        btn_go_login.setOnClickListener {
            startActivity<SigninActivity>()
        }
    }

}