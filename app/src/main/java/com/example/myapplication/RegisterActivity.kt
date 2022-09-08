package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Completable.merge
import io.reactivex.Flowable.merge
import io.reactivex.Maybe.merge
import io.reactivex.Observable
import java.util.*

@SuppressLint("CheckResult")
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val namestream = RxTextView.textChanges(binding.etFullName)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }
        namestream.subscribe{
            showNameExistAlert(it)
        }
        val emailstream = RxTextView.textChanges(binding.etEmail)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }
        emailstream.subscribe{
            showEmailValidAlert(it)
        }
        val usernamestream = RxTextView.textChanges(binding.etUsername)
            .skipInitialValue()
            .map { username ->
                username.length < 6
            }
        usernamestream.subscribe{
            showTextMinimalAlert(it,"username")
        }
        val passwordstream = RxTextView.textChanges(binding.etPassword)
            .skipInitialValue()
            .map { password ->
                password.length < 8
            }
        passwordstream.subscribe{
            showTextMinimalAlert(it,"pasword")
        }
        val passwordConfirmStream = Observable.merge(
            RxTextView.textChanges(binding.etPassword)
                .skipInitialValue()
                .map { password->
                    password.toString() != binding.etCpassword.text.toString()
                },
            RxTextView.textChanges(binding.etCpassword)
                .skipInitialValue()
                .map { confirmPassword ->
                    confirmPassword.toString() != binding.etPassword.text.toString()
                }  )
        passwordConfirmStream.subscribe {
            showPasswordCValidAlert(it)
        }
        val invalidFieldsStream = Observable.combineLatest(
            namestream,
            emailstream,
            usernamestream,
            passwordConfirmStream,
            passwordstream
        ) { nameInvalid: Boolean, emailInvalid: Boolean, usernameInvalid: Boolean, passwordInvalid: Boolean, passwordConfirmInvalid: Boolean ->
            !nameInvalid && !emailInvalid && !usernameInvalid && !passwordInvalid && !passwordConfirmInvalid
        }
        invalidFieldsStream.subscribe{
            isValid ->
            if (isValid){
                binding.btnRegister.isEnabled = true
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this,R.color.primary_color)
            }
            else{
                binding.btnRegister.isEnabled = false
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this,android.R.color.darker_gray)
            }
        }
        binding.btnRegister.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
        }
        binding.tvHaventAccount.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
    private fun showNameExistAlert(isNotValid: Boolean){
        binding.etFullName.error = if (isNotValid) " should not be empty" else null
    }
    private fun showTextMinimalAlert(isNotValid: Boolean,text: String){
        if(text =="Username")
            binding.etUsername.error = if (isNotValid) "$text should containe at least 6 caracters" else null
        else if (text =="Password")
            binding.etPassword.error = if (isNotValid) "$text  should containe at least 8 caracters" else null

    }
    private fun showEmailValidAlert(isNotValid: Boolean){
        binding.etEmail.error = if (isNotValid) " should not be empty" else null
    }
    private fun showPasswordCValidAlert(isNotValid: Boolean){
        binding.etEmail.error = if (isNotValid) " should not be empty" else null
    }


}