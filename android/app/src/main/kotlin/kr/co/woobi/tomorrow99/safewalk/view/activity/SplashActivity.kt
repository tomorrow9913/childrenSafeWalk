package kr.co.woobi.tomorrow99.safewalk.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import java.lang.Exception


class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Thread.sleep(1000);
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            return;
        }
    }
}