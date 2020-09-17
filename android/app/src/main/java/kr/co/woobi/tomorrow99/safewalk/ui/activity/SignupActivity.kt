package kr.co.woobi.tomorrow99.safewalk.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sungbin.sungbintool.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_signup.*
import kr.co.woobi.tomorrow99.safewalk.R
import kr.co.woobi.tomorrow99.safewalk.`interface`.SignupInterface
import kr.co.woobi.tomorrow99.safewalk.tool.isEmail
import kr.co.woobi.tomorrow99.safewalk.tool.isNickName
import kr.co.woobi.tomorrow99.safewalk.tool.isPassword
import kr.co.woobi.tomorrow99.safewalk.tool.isPhoneNumber
import kr.co.woobi.tomorrow99.safewalk.tool.util.EncryptUtil
import kr.co.woobi.tomorrow99.safewalk.ui.dialog.LoadingDialog
import org.jetbrains.anko.startActivity
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named


/**
 * Created by SungBin on 2020-09-14.
 */

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    @Named("server")
    @Inject
    lateinit var client: Retrofit

    private val loadingDialog by lazy {
        LoadingDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        cb_agreement_info.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                btn_signup.apply {
                    alpha = 1f
                    setOnClickListener {
                        if (checkFieldFormat()) {
                            val name = tiet_name.text.toString()
                            val nickname = tiet_nickname.text.toString()
                            val email = tiet_email.text.toString()
                            val password = tiet_password.text.toString()
                            /*var emergencyCall: String? = tiet_emergency_call.text.toString()
                            if (emergencyCall!!.isBlank()) emergencyCall = null*/

                            val params = hashMapOf<String, String>(
                                "pwd" to EncryptUtil.sha256(password),
                                "email" to email,
                                "id" to email,
                                "nickname" to nickname,
                                "callNum" to "112",
                                "name" to name,
                            )

                            client.create(SignupInterface::class.java).run {
                                Logger.w("시작!")
                                loadingDialog.show()
                                signup(params)
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ response ->
                                        Logger.w(response)
                                    }, { throwable ->
                                        Logger.w(throwable)
                                    }, {
                                        loadingDialog.close()
                                        finish()
                                        startActivity<ActivitySignupFinish>()
                                    })
                            }
                        }
                    }
                }
            } else {
                btn_signup.apply {
                    alpha = 0.5f
                    setOnClickListener { }
                }
            }
        }
    }

    private fun checkFieldFormat(): Boolean {
        val name = tiet_name.text.toString()
        val nickname = tiet_nickname.text.toString()
        val email = tiet_email.text.toString()
        val password = tiet_password.text.toString()
        val confirmPassword = tiet_confirm_password.text.toString()
        val emergencyCall = tiet_emergency_call.text.toString()

        til_name.error = null
        til_nickname.error = null
        til_email.error = null
        til_password.error = null
        til_confirm_password.error = null
        til_emergency_call.error = null

        return if (name.length < 2) {
            til_name.error = getString(R.string.signup_wrong_name_length)
            false
        } else if (!nickname.isNickName()) {
            til_nickname.error = getString(R.string.signup_nickname_length)
            false
        } else if (!email.isEmail()) {
            til_email.error = getString(R.string.signup_not_email)
            false
        } else if (!password.isPassword()) {
            til_password.error = getString(R.string.signup_password_format)
            false
        } else if (password != confirmPassword) {
            til_confirm_password.error = getString(R.string.signup_not_match_password)
            false
        } else if (!emergencyCall.isPhoneNumber() && emergencyCall.isNotBlank()) {
            til_emergency_call.error = getString(R.string.signup_not_phonenumber)
            false
        } else true
    }
}