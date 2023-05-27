package com.example.myuserstory.ui.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.myuserstory.R
import com.example.myuserstory.data.remote.response.SignInResponse
import com.example.myuserstory.databinding.ActivitySignInBinding
import com.example.myuserstory.helper.ViewModelFactory
import com.example.myuserstory.ui.main.MainActivity
import com.example.myuserstory.ui.signup.SignUpActivity


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val signInViewModel: SignInViewModel by lazy {
        ViewModelProvider(this, factory)[SignInViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupAnimation()

        binding.btnAskSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            val minPassLength = 8
            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = getString(R.string.email_hint)
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = getString(R.string.password_hint)
                }
                password.length < minPassLength -> {
                    binding.edLoginPassword.error = getString(R.string.invalid_password)
                }

                else -> setupViewModel(email, password)
            }
        }
    }

    private fun setupViewModel(email: String, password: String) {
        signInViewModel.signin(email, password).observe(this) { result ->
            val data = result.loginResult
            if (data != null) {
                signInViewModel.saveUser(data.name, data.userId, data.token)
                createAlertDialog(result)
            }
            signInViewModel.repository.remoteData.isLoading.observe(this) {
                showLoading(it)
            }
        }
    }

    private fun createAlertDialog(result: SignInResponse) {
        val builder = AlertDialog.Builder(this)
        if (result.error) {
            if (result.message == "400") {
                builder.setTitle(R.string.sign_in_status_title)
                builder.setMessage(R.string.invalid_email)
                builder.setIcon(R.drawable.baseline_close_24)
            }
            else if (result.message == "401") {
                builder.setTitle(R.string.sign_in_status_title)
                builder.setMessage(R.string.sign_in_status_user_not_found)
                builder.setIcon(R.drawable.baseline_close_24)
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
            Handler(Looper.getMainLooper()).postDelayed({
                alertDialog.dismiss()
            }, getString(R.string.delay_milis).toLong())
        }
        else {
            builder.setTitle(R.string.sign_in_status_title)
            builder.setMessage(R.string.sign_in_status_success)
            builder.setIcon(R.drawable.baseline_check_circle_outline_24)
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
            Handler(Looper.getMainLooper()).postDelayed({
                alertDialog.dismiss()

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }, getString(R.string.delay_milis).toLong())
        }
    }

    private fun setupAnimation() {
        val tvSignIn = ObjectAnimator.ofFloat(binding.tvSignIn, View.ALPHA, 1f).setDuration(500)
        val ivSignInIcon = ObjectAnimator.ofFloat(binding.ivSignInIcon, View.ALPHA, 1f).setDuration(500)
        val tvSignInDesc1 = ObjectAnimator.ofFloat(binding.tvSignInDesc1, View.ALPHA, 1f).setDuration(500)
        val tvSignInDesc2 = ObjectAnimator.ofFloat(binding.tvSignInDesc2, View.ALPHA, 1f).setDuration(500)
        val tvEmailSignIn = ObjectAnimator.ofFloat(binding.tvEmailSignIn, View.ALPHA, 1f).setDuration(500)
        val edLoginEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val tvPassSignIn = ObjectAnimator.ofFloat(binding.tvPasswordSignIn, View.ALPHA, 1f).setDuration(500)
        val edLoginPass = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val btnSignIn = ObjectAnimator.ofFloat(binding.btnSignIn, View.ALPHA, 1f).setDuration(500)
        val tvAskRegister = ObjectAnimator.ofFloat(binding.tvAskRegister, View.ALPHA, 1f).setDuration(500)
        val btnAskSignUp = ObjectAnimator.ofFloat(binding.btnAskSignUp, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(tvSignIn, ivSignInIcon, tvSignInDesc1, tvSignInDesc2, tvEmailSignIn, edLoginEmail, tvPassSignIn, edLoginPass, btnSignIn, tvAskRegister, btnAskSignUp)
            startDelay -= 500
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.signInProgressBar.visibility = View.VISIBLE
        }
        else {
            binding.signInProgressBar.visibility = View.GONE
        }
    }
}