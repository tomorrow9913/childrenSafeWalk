package kr.co.woobi.tomorrow99.safewalk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_login.setOnClickListener {
            val ID = te_id.text.toString()
            val PW = sha256(te_password.text.toString())

            Log.d("로그인", "로그인 진입")
            //todo 서버 데이터 전송
            val SERVE_HOST:String = "http://210.107.245.192:400/"
            var retrofit = Retrofit.Builder()
                .baseUrl(SERVE_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            Log.d("로그인", "로그인 진입1")
            var loginService = retrofit.create(LoginService::class.java)

            Log.d("로그인", "로그인 진입2")
            var body = HashMap<String, String>()

            body.put("id", ID)
            body.put("pwd", PW)
            Log.d("로그인", "로그인 진입3")

            loginService.requestLogin(body).enqueue(object : Callback<LoginOut>{
                override fun onFailure(call: Call<LoginOut>, t: Throwable) {
                    tv_emergencyError.setText(R.string.error_network)
                }

                override fun onResponse(call: Call<LoginOut>, response: Response<LoginOut>) {
                    Log.d("로그인", "로그인 진입4")
                    try {
                        Log.d("로그인", "로그인 진입5")
                        val RESPONSE_DATA = response.body() //responseData?.session 사용시 null 일 수도 있음

                        val dialog = AlertDialog.Builder(this@MainActivity)
                        dialog.setTitle("알람")
                        if(RESPONSE_DATA?.result == "success"){
                            //todo 메인 화면 이동 setContentView(R.layout.)로 뒤로가기 없도록 구현할 예정
                            dialog.setMessage("result=${RESPONSE_DATA?.result}&session=${RESPONSE_DATA?.session}")
                            dialog.show()
                        }
                        else {
                            dialog.setMessage("result=${RESPONSE_DATA?.result}&comment=${RESPONSE_DATA?.comment}")
                            dialog.show()
                        }
                    }
                    catch (e:Exception){
                        Log.d("에러로그","$e")
                    }
                }
            })
        }

        val LOGIN_FIELD_CHANGE_LISTENER = object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (te_id.text.toString() == "") tv_emergencyError.setText(R.string.error_emptyID)
                else if(!isEmail(te_id.text.toString())) tv_emergencyError.setText(R.string.error_notEmail)
                else if (te_password.text.toString() == "") tv_emergencyError.setText(R.string.error_emptyPW)
                else tv_emergencyError.text = ""
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        }

        te_password.addTextChangedListener(LOGIN_FIELD_CHANGE_LISTENER)
        te_id.addTextChangedListener(LOGIN_FIELD_CHANGE_LISTENER)

        tv_signUpLink.setOnClickListener{
            val INTENT = Intent(this, signup::class.java)
            startActivity(INTENT)
        }
    }
}

/****************************************
 * Name:            sha256
 * description:     sha256 해싱
 *
 * Author: Jeong MinGye
 * Create: 20.07.26
 * Update:
 *
 * //출처: https://lonepine.tistory.com/entry/Kotlin-Sha256 [Lonepine's blog]
 **************************************/
fun sha256(param: String): String {
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

/****************************************
 * Name:            checkPWformet
 * description:     PW 형식 확인
 *
 * Author: Jeong MinGye
 * Create: 20.07.27.
 * Update:
 **************************************/
fun checkPWformet(content: String): Boolean {
    //todo 비밀번호 유효성 검사 버그 고칠것
    if(content.length < 5 || content.length > 16) return false
    val reg1 = Regex("^.*(?=^.{8,15}\$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%]).*$")
    if(!content.matches(reg1)) return false

    return true
}

/****************************************
 * Name:            isEmail
 * description:     email 인지 확인
 *
 * Author: Jeong MinGye
 * Create: 20.07.27.
 * Update:
 **************************************/
fun isEmail(content: String): Boolean {
    val reg = Regex("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$")
    if(!content.matches(reg)) return false

    return true
}

/****************************************
 * Name:            checkPhoneNumber
 * description:     email 인지 확인
 *
 * Author: Jeong MinGye
 * Create: 20.07.27.
 * Update:
 **************************************/
fun checkPhoneNumber(content: String): Boolean {
    val reg = Regex("01[016789][0-9]{7,8}$")
    if(!content.matches(reg)) return false
    return true
}

/****************************************
 * Name:            checkPhoneNumber
 * description:     email 인지 확인
 *
 * Author: Jeong MinGye
 * Create: 20.07.27.
 * Update:
 **************************************/
fun checkNickName(content: String): Boolean {
    val reg = Regex("([ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{4,8})$")
    if(!content.matches(reg)) return false
    return true
}
