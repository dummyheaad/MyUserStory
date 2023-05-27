package com.example.myuserstory.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.myuserstory.R
import com.example.myuserstory.databinding.ActivitySignUpBinding
import com.example.myuserstory.helper.ViewModelFactory
import com.example.myuserstory.ui.signin.SignInActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val signUpViewModel: SignUpViewModel by lazy {
        ViewModelProvider(this, factory)[SignUpViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupAnimation()
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
        binding.btnSignUp.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val minPassLength = 8
            when {
                name.isEmpty() -> {
                    binding.edRegisterName.error = getString(R.string.name_hint)
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = getString(R.string.email_hint)
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.error = getString(R.string.password_hint)
                }
                password.length < minPassLength -> {
                    binding.edRegisterPassword.error = getString(R.string.invalid_password)
                }
                else -> setupViewModel(name, email, password)
            }
        }
    }

    private fun setupViewModel(name: String, email: String, password: String) {
        signUpViewModel.signup(name, email, password).observe(this) { result ->
            createAlertDialog(result.message)
            signUpViewModel.repository.remoteData.isLoading.observe(this) {
                showLoading(it)
            }
        }
    }

    private fun createAlertDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        if (message == "201") {
            builder.setTitle(R.string.sign_up_status_title)
            builder.setMessage(R.string.sign_up_status_success)
            builder.setIcon(R.drawable.baseline_check_circle_outline_24)
        }
        else if (message == "400") {
            builder.setTitle(R.string.sign_up_status_title)
            builder.setMessage(R.string.sign_up_used_email)
            builder.setIcon(R.drawable.baseline_close_24)
        }
        val alertDialog:AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            alertDialog.dismiss()
            if (message == "201") {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, getString(R.string.delay_milis).toLong())
    }

    private fun setupAnimation() {
        val tvSignUp = ObjectAnimator.ofFloat(binding.tvSignUp, View.ALPHA, 1f).setDuration(500)
        val ivSignUpIcon = ObjectAnimator.ofFloat(binding.ivSignUpIcon, View.ALPHA, 1f).setDuration(500)
        val tvNameSignUp = ObjectAnimator.ofFloat(binding.tvNameSignUp, View.ALPHA, 1f).setDuration(500)
        val edRegisterName = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val tvEmailSignUp = ObjectAnimator.ofFloat(binding.tvEmailSignUp, View.ALPHA, 1f).setDuration(500)
        val edRegisterEmail = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val tvPasswordSignUp = ObjectAnimator.ofFloat(binding.tvPasswordSignUp, View.ALPHA, 1f).setDuration(500)
        val edRegisterPassword = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val btnSignUp = ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(tvSignUp, ivSignUpIcon, tvNameSignUp, edRegisterName, tvEmailSignUp, edRegisterEmail, tvPasswordSignUp, edRegisterPassword, btnSignUp)
            startDelay -= 500
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {

        if (isLoading) {
            binding.signUpProgressBar.visibility = View.VISIBLE
        } else {
            binding.signUpProgressBar.visibility = View.GONE
        }
    }
}