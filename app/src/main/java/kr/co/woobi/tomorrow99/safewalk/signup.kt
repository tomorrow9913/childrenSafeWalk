package kr.co.woobi.tomorrow99.safewalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_signup.*

class signup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        var hasErr:Boolean = true

        btn_signUp.setOnClickListener {
          if ( hasErr ){
              val NO_PROCESS_MSG = Toast.makeText(this, "항목들을 알맞게 입력해 주세요.", Toast.LENGTH_SHORT)
              NO_PROCESS_MSG.show()
          }
          else{

          }
        }
    }
}
