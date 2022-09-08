package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityLoginBinding
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val usernamestream = RxTextView.textChanges(binding.etEmail)
            .skipInitialValue()
            .map { username ->
                username.isEmpty()
            }
        usernamestream.subscribe{
            showTextMinimalAlert(it,"Email/username")
        }
        val passwordstream = RxTextView.textChanges(binding.etPassword)
            .skipInitialValue()
            .map { password ->
                password.isEmpty()
            }
        passwordstream.subscribe{
            showTextMinimalAlert(it,"password")
        }
        val invalidFieldsStream = Observable.combineLatest(
            usernamestream,
            passwordstream
        ) { usernameInvalid: Boolean, passwordInvalid: Boolean ->
            !usernameInvalid && !passwordInvalid
        }
        invalidFieldsStream.subscribe{
                isValid ->
            if (isValid){
                binding.btnLogin.isEnabled = true
                binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(this,R.color.primary_color)
            }
            else{
                binding.btnLogin.isEnabled = false
                binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(this,android.R.color.darker_gray)
            }
        }
        //Biometric login Implementation



        //if Biometric is true
        binding.btnLogin.setOnClickListener{

            if(binding.etEmail.text.toString() =="admin" && binding.etPassword.text.toString() == "admin"){
                startActivity(Intent(this,HomeActivity::class.java))
            }
            else{

                Toast.makeText(
                    applicationContext,
                    "verify your data!", Toast.LENGTH_SHORT
                )
                    .show()
            }

        }
        binding.tvHaventAccount.setOnClickListener{
            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }

    private fun showTextMinimalAlert(isNotValid: Boolean,text: String){
        if(text =="Username")
            binding.etEmail.error = if (isNotValid) "$text should containe at least 6 caracters" else null
        else if (text =="Password")
            binding.etPassword.error = if (isNotValid) "$text  should containe at least 8 caracters" else null

    }
}