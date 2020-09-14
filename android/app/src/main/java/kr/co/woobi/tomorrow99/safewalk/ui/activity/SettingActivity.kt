package kr.co.woobi.tomorrow99.safewalk.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_setting.*
import kr.co.woobi.tomorrow99.safewalk.R

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        //todo 서버의 데이터 TextView에 표시

        val INTENT_INFO = intent
        val SESSION = INTENT_INFO.getStringExtra("session")
        val NICK = INTENT_INFO.getStringExtra("nickname")
        val NAME = INTENT_INFO.getStringExtra("name")
        val MAIL = INTENT_INFO.getStringExtra("email")
        val PHONE: String? = INTENT_INFO.getStringExtra("callnum")

        tv_nick.text = NICK
        tv_name2.text = NAME
        tv_ID2.text = MAIL
        tv_call2.text = PHONE

        val dialog = AlertDialog.Builder(this@SettingActivity)
        dialog.setTitle("알람")
        dialog.setMessage("Session=${SESSION}\nNick=$NICK\nName=${NAME}\nMail=$MAIL\nPhone=${PHONE}")
        //dialog.show()
    }
}
