package kr.co.woobi.tomorrow99.safewalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_signup.*

class signup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        var hasErr:Boolean? = null

        btn_signUp.setOnClickListener {
          if ( hasErr == null || hasErr == true ){
              val NO_PROCESS_MSG = Toast.makeText(this, "항목들을 알맞게 입력해 주세요.", Toast.LENGTH_SHORT)
              NO_PROCESS_MSG.show()
          }
          else{

          }
        }

        val SIGNUP_FIELD_CHANGE_LISTENER = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(te_name.text.toString() == "") {
                    hasErr = true
                    tv_nameErr.setText(R.string.error_empty)
                    return
                }
                else{
                    tv_nameErr.text  = ""
                    hasErr = false
                }

                if(!checkNickName(te_nick.text.toString())) {
                    hasErr = true
                    tv_nicknameErr.setText(R.string.error_nickLength)
                    return
                }
                else{
                    tv_nicknameErr.text  = ""
                    hasErr = false
                }

                if(!isEmail(te_emailID.text.toString())) {
                    hasErr = true
                    tv_emailErr.setText(R.string.error_notEmail)
                    return
                }
                else {
                    tv_emailErr.text = ""
                    hasErr = false
                }

                if (te_password.text.toString() != te_pwConfirm.text.toString()){
                    hasErr = true
                    tv_confirmErr.setText(R.string.error_not_confirm)
                    return
                }
                else {
                    tv_confirmErr.text = ""
                    hasErr = false
                }

                if(!checkPWformet(te_password.text.toString())){
                    hasErr = true
                    tv_pwErr.setText(R.string.error_not_correct_PWformat)
                    return
                }
                else {
                    tv_pwErr.text = ""
                    hasErr = false
                }

                if (!checkPhoneNumber(te_emergency.text.toString())){
                    hasErr = true
                    tv_emergencyError.setText(R.string.error_not_correct_format)
                    return
                }
                else {
                    tv_emergencyError.text = ""
                    hasErr = false
                }
            }
        }

        te_emailID.addTextChangedListener(SIGNUP_FIELD_CHANGE_LISTENER)
        te_pwConfirm.addTextChangedListener(SIGNUP_FIELD_CHANGE_LISTENER)
        te_password.addTextChangedListener(SIGNUP_FIELD_CHANGE_LISTENER)
    }
}
