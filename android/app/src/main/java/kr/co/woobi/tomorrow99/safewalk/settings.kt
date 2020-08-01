package kr.co.woobi.tomorrow99.safewalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.jar.Attributes

class settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //todo 서버의 데이터 TextView에 표시

        val INTENT_INFO = getIntent()
        val SESSION = INTENT_INFO.getStringExtra("session")
        val NICK = INTENT_INFO.getStringExtra("nickname")
        val NAME = INTENT_INFO.getStringExtra("name")
        val MAIL = INTENT_INFO.getStringExtra("email")
        val PHONE:String? = INTENT_INFO.getStringExtra("callnum")

        tv_nick.text = NICK
        tv_name2.text = NAME
        tv_ID2.text = MAIL
        tv_call2.text = PHONE
    }
}
