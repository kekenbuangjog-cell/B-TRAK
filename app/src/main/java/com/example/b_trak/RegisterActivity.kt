package com.example.b_trak

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * RegisterActivity provides the UI for creating a new user account.
 * Now integrated with Room Database for persistent user storage.
 */
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnLoginLink = findViewById<TextView>(R.id.text_back_to_login)
        val btnRegister = findViewById<Button>(R.id.btn_register_account)
        val inputUser = findViewById<EditText>(R.id.reg_user)
        val inputEmail = findViewById<EditText>(R.id.reg_email)
        val inputPass = findViewById<EditText>(R.id.reg_pass)
        val inputConfirmPass = findViewById<EditText>(R.id.reg_confirm_pass)

        btnLoginLink.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            val username = inputUser.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val password = inputPass.text.toString().trim()
            val confirmPass = inputConfirmPass.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "PLEASE FILL ALL FIELDS", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPass) {
                Toast.makeText(this, "PASSWORDS DO NOT MATCH", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Database operations on lifecycleScope
            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(this@RegisterActivity)
                val existingUser = db.userDao().getUserByUsername(username)

                if (existingUser != null) {
                    Toast.makeText(this@RegisterActivity, "USERNAME ALREADY EXISTS", Toast.LENGTH_SHORT).show()
                } else {
                    val newUser = User(username = username, email = email, password = password)
                    db.userDao().insertUser(newUser)
                    Toast.makeText(this@RegisterActivity, "ACCOUNT CREATED SUCCESSFULLY", Toast.LENGTH_SHORT).show()
                    finish() // Return to Login
                }
            }
        }
    }
}