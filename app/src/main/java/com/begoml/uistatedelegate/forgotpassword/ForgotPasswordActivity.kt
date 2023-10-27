package com.begoml.uistatedelegate.forgotpassword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.begoml.uistatedelegate.R

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportFragmentManager.beginTransaction().apply {
            add(R.id.appContainerView, ForgotPasswordFragment())
            commit()
        }
    }
}
