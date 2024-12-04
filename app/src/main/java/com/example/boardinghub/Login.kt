package com.example.boardinghub

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var login: Button
    private lateinit var register: Button
    private lateinit var logo: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        FirebaseFirestore.setLoggingEnabled(true);

        username = findViewById(R.id.txtUsername)
        password = findViewById(R.id.regPassword)
        login = findViewById(R.id.btnLogin)
        register = findViewById(R.id.regRegister)
        logo = findViewById(R.id.ivLogo)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        logo.setImageResource(R.mipmap.ic_boardinghub_logo_round)

        login.setOnClickListener {
            val user = username.text.toString().trim()
            val pass = password.text.toString().trim()
            if (user.isNotEmpty() && pass.isNotEmpty()) {
                auth.signInWithEmailAndPassword(user, pass).addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        checkUserInFirestore(user)
                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }

        register.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }
    }

    private fun checkUserInFirestore(user: String) {
        db.collection("users").document(user).get() .addOnSuccessListener {
            document -> if (document.exists()) {
                Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                Handler(Looper.getMainLooper()).postDelayed({ startActivity(intent) }, 3000)
            } else {
                Toast.makeText(this, "User not found in database.", Toast.LENGTH_SHORT).show()
            }
        } .addOnFailureListener { e ->
            Toast.makeText(this, "Error fetching user: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}