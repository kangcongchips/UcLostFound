package com.example.uclostfound

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uclostfound.data.SampleData
import com.example.uclostfound.data.Session
import com.example.uclostfound.databinding.ActivityLoginBinding
import com.example.uclostfound.model.User

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var registerMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnToggle.setOnClickListener { toggleMode() }
        binding.btnSubmit.setOnClickListener { submit() }
    }

    private fun toggleMode() {
        registerMode = !registerMode
        if (registerMode) {
            binding.tvMode.text = "Create an account"
            binding.tilFullName.visibility = View.VISIBLE
            binding.btnSubmit.text = "Register"
            binding.btnToggle.text = "Have an account? Log in"
        } else {
            binding.tvMode.text = "Log in to continue"
            binding.tilFullName.visibility = View.GONE
            binding.btnSubmit.text = "Log in"
            binding.btnToggle.text = "No account? Register"
        }
    }

    private fun submit() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        binding.tilFullName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        var valid = true

        if (registerMode && fullName.length < 2) {
            binding.tilFullName.error = "Enter your full name"
            valid = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Enter a valid email address"
            valid = false
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            valid = false
        }

        if (!valid) return

        // Phase 1: check against the in-memory accounts list
        val user: User
        if (registerMode) {
            if (SampleData.accounts.containsKey(email.lowercase())) {
                binding.tilEmail.error = "This email is already registered"
                return
            }
            user = User("u_" + email.lowercase(), fullName, email, "STUDENT")
            SampleData.accounts[email.lowercase()] = Pair(password, user)
        } else {
            val account = SampleData.accounts[email.lowercase()]
            if (account == null || account.first != password) {
                binding.tilPassword.error = "Wrong email or password"
                return
            }
            user = account.second
        }
        Session.currentUser = user

        Toast.makeText(this, "Logged in as ${user.role}", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}