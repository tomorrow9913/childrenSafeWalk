package kr.co.woobi.tomorrow99.safewalk.ui.activity

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_signin.*
import kr.co.woobi.tomorrow99.safewalk.R

class SigninActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        tv_title.run {
            val ssb = SpannableStringBuilder(text)
            ssb.setSpan( // 우리아이 부분 텍스트 축소
                RelativeSizeSpan(0.7f),
                0,
                4,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ssb.setSpan( // 안전 걸음 앱 볼드처리
                StyleSpan(Typeface.BOLD),
                4,
                text.lastIndex + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text = ssb
        }
    }
}