package kr.co.woobi.tomorrow99.safewalk.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kr.co.woobi.tomorrow99.safewalk.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)


        btn_login.setOnClickListener {
            val ID = te_id.text.toString()
            val PW =
                sha256(te_password.text.toString())

            val SERVE_HOST: String = "http://210.107.245.192:400/"
            var retrofit = Retrofit.Builder()
                .baseUrl(SERVE_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            var loginService = retrofit.create(LoginService::class.java)

            var body = HashMap<String, String>()

            body.put("id", ID)
            body.put("pwd", PW)

            loginService.login(body).enqueue(object : Callback<LoginOut> {
                override fun onFailure(call: Call<LoginOut>, t: Throwable) {
                    tv_emergencyError.setText(R.string.error_network)
                }

                override fun onResponse(call: Call<LoginOut>, response: Response<LoginOut>) {
                    try {
                        val RESPONSE_DATA = response.body() //responseData?.session 사용시 null 일 수도 있음

                        val dialog = AlertDialog.Builder(this@MainActivity)
                        dialog.setTitle("알람")
                        if (RESPONSE_DATA.result == "success") {
                            //메인 화면 이동
                            val MAP_PAGE = Intent(this@MainActivity, mainmapPage::class.java)

                            MAP_PAGE.putExtra("session", RESPONSE_DATA.session)
                            MAP_PAGE.putExtra("nickname", RESPONSE_DATA.nickname)
                            MAP_PAGE.putExtra("name", RESPONSE_DATA.name)
                            MAP_PAGE.putExtra("email", RESPONSE_DATA.email)
                            MAP_PAGE.putExtra("callnum", RESPONSE_DATA.callNum)

                            MAP_PAGE.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            MAP_PAGE.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            setResult(Activity.RESULT_OK, MAP_PAGE)
                            finish()
                        } else {
                            dialog.setMessage("result=${RESPONSE_DATA.result}&comment=${RESPONSE_DATA.comment}")
                            dialog.show()
                        }
                    } catch (e: Exception) {
                        Log.d("에러로그", "$e")
                    }
                }
            })
        }

        val LOGIN_FIELD_CHANGE_LISTENER = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (te_id.text.toString() == "") tv_emergencyError.setText(R.string.error_emptyID)
                else if (!isEmail(te_id.text.toString())) tv_emergencyError.setText(
                    R.string.signup_not_email
                )
                else if (te_password.text.toString() == "") tv_emergencyError.setText(
                    R.string.error_emptyPW
                )
                else tv_emergencyError.text = ""
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        }

        te_password.addTextChangedListener(LOGIN_FIELD_CHANGE_LISTENER)
        te_id.addTextChangedListener(LOGIN_FIELD_CHANGE_LISTENER)

        tv_signUpLink.setOnClickListener {
            val INTENT = Intent(this, SignupActivity::class.java)
            startActivity(INTENT)
        }
    }
}
