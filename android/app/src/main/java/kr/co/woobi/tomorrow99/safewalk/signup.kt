package kr.co.woobi.tomorrow99.safewalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_signup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
                //todo 입력정보 서버로 전송
                val SERVE_HOST = "http://210.107.245.192:400/"
                var retrofit = Retrofit.Builder()
                    .baseUrl(SERVE_HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var signup = retrofit.create(SignUpService::class.java)

                val NAME = te_name.text.toString()
                val NICK = te_nick.text.toString()
                val ID = te_emailID.text.toString()
                val PASS = te_password.text.toString()
                var phone:String

                when(te_emergency.text.toString()){
                    "" -> phone = "null"
                    else -> phone = te_emergency.text.toString()
                }
                
                signup.requestSignUP(ID, PASS, ID, phone, NICK, NAME ).enqueue(object : Callback<SignupOut> {
                    override fun onFailure(call: Call<SignupOut>, t: Throwable) {
                        tv_emergencyError.setText(R.string.error_network)
                    }

                    override fun onResponse(call: Call<SignupOut>, response: Response<SignupOut>) {
                        var responseData = response.body() //responseData?.session 사용시 null 일 수도 있음

                            val dialog = AlertDialog.Builder(this@signup)
                            dialog.setTitle("알람")
                            if(responseData?.result == "success"){
                                dialog.setMessage("result=${responseData?.result}")
                            }
                            else
                                dialog.setMessage("result=${responseData?.result}")

                            dialog.show()
                      }
              })
          }
        }
        //todo 비상연락망 비우는 것도 가능하도록 할 유효성 검사 수정
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

        // 필드 이벤트 체크
        te_name.addTextChangedListener(SIGNUP_FIELD_CHANGE_LISTENER)
        te_nick.addTextChangedListener(SIGNUP_FIELD_CHANGE_LISTENER)
        te_emailID.addTextChangedListener(SIGNUP_FIELD_CHANGE_LISTENER)
        te_password.addTextChangedListener(SIGNUP_FIELD_CHANGE_LISTENER)
        te_pwConfirm.addTextChangedListener(SIGNUP_FIELD_CHANGE_LISTENER)
        te_emergency.addTextChangedListener(SIGNUP_FIELD_CHANGE_LISTENER)
        
        //todo 개인 정보 제공 동의 화면(필수 확인하도록) 띄우기
    }
}


data class SignupOut (
    var result:String      //"result": "success" or "fail"
)

interface SignUpService {
    @FormUrlEncoded
    @POST(value = "registerUser.php")

    fun requestSignUP (
        @Field(value = "id") id:String,
        @Field(value = "pwd") pwd:String,
        @Field(value = "email") email:String,
        @Field(value = "callNum") callNum:String?,
        @Field(value = "nickname") nickname:String,
        @Field(value = "name") name:String
    ) : Call<SignupOut>
}