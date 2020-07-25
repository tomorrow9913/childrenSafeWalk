package kr.co.woobi.tomorrow99.safechildewalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_LogIn.setOnClickListener{
            var ID:String = input_ID.toString()
            var PW:String = input_PW.toString()

            //PW Sha256 해싱
            PW = encoding(PW)

            //TODO 서버 데이터 전달


            //TODO 로그인 실패시 처리
            tv_error.text = PW //"아이디 혹은 비밀번호가 일치 하지 않습니다."
        }


        tv_signUpLink.setOnClickListener{
            //TODO (회원 가입 창 띄워야 함)
        }

        tv_searchLink.setOnClickListener{
            //TODO (ID/PW 찾기 창 띄우기)
        }
    }

}

fun encoding(param: String): String {
    val HEX_CHARS = "0123456789ABCDEF"
    val bytes = MessageDigest
        .getInstance("SHA-256")
        .digest(param.toByteArray())
    val result = StringBuilder(bytes.size * 2)
    bytes.forEach {
        val i = it.toInt()
        result.append(HEX_CHARS[i shr 4 and 0x0f])
        result.append(HEX_CHARS[i and 0x0f])
    }
    return result.toString()
}


