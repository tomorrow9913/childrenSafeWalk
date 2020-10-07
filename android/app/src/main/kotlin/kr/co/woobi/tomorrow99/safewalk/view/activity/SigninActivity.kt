package kr.co.woobi.tomorrow99.safewalk.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sungbin.sungbintool.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_signin.*
import kr.co.woobi.tomorrow99.safewalk.R
import kr.co.woobi.tomorrow99.safewalk.`interface`.ImageInterface
import kr.co.woobi.tomorrow99.safewalk.`interface`.LoginInterface
import kr.co.woobi.tomorrow99.safewalk.tool.util.EncryptUtil.sha256
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SigninActivity : AppCompatActivity(){
    @Named("server")
    @Inject
    lateinit var server: Retrofit

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

        tv_signup.setOnClickListener{
            val intent = Intent(this@SigninActivity, SignupActivity::class.java)
            startActivity(intent)
        }

        btn_signin.setOnClickListener {
            var loginData = HashMap<String, String>()
            loginData.put("id", tiet_email.text.toString())
            loginData.put("pwd", sha256(tiet_password.text.toString()))

            server.create(LoginInterface::class.java).run {
                login(loginData)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        if (response.result == "success") {
                            val MAP_PAGE = Intent(this@SigninActivity, MapActivity::class.java)

                            MAP_PAGE.putExtra("session", response.session)
                            MAP_PAGE.putExtra("nickname", response.nickname)
                            MAP_PAGE.putExtra("name", response.name)
                            MAP_PAGE.putExtra("email", response.email)
                            MAP_PAGE.putExtra("callnum", response.callNum)

                            MAP_PAGE.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            MAP_PAGE.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            setResult(Activity.RESULT_OK, MAP_PAGE)
                            finish()
                        }
                        else {
                            Toast.makeText(
                                this@SigninActivity,
                                "${response.comment}.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, { throwable ->
                        Logger.w(throwable)
                        Toast.makeText(
                            this@SigninActivity,
                            "${throwable.message}.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }, {
                    })
            }
        }
    }
}