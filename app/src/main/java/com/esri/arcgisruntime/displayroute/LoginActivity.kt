package com.esri.arcgisruntime.displayroute

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.esri.arcgisruntime.displayroute.databinding.ActivityLoginLayoutBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_signin -> navigateToHome()
            R.id.txt_forgot -> forgotPassword()
        }
    }

    private lateinit var bind: ActivityLoginLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_login_layout)
        bind.btnSignin.setOnClickListener(this)
        bind.txtForgot.setOnClickListener(this)
    }

    private fun navigateToHome() {
        if (validateField()) {

            var intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("Name", bind.etUsername.text.toString())
            startActivity(intent, ActivityOptions.makeCustomAnimation(this, R.anim.enter_right_in, R.anim.exit_left_out).toBundle())
            finish()
        } else {
            Toast.makeText(this, getString(R.string.error),
                    Toast.LENGTH_SHORT).show()
        }
    }


    private fun validateField(): Boolean {
        if (TextUtils.isEmpty(bind.etUsername.text.toString())) {
            bind.etUsername.setError(getString(R.string.enter_username))
            return false
        }

        if (TextUtils.isEmpty(bind.etPassword.text.toString())) {
            bind.etPassword.setError(getString(R.string.enter_password))
            return false
        }

        return true
    }

    private fun forgotPassword() {
        Toast.makeText(this, getString(R.string.forgot_password_msg),
                Toast.LENGTH_SHORT).show()
    }
}