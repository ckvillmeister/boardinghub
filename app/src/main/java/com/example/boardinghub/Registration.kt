package com.example.boardinghub

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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

class Registration : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var firstname: EditText
    private lateinit var middlename: EditText
    private lateinit var lastname: EditText
    private lateinit var register: Button
    private lateinit var back: Button
    private lateinit var logo: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()
        email = findViewById(R.id.regEmail)
        firstname = findViewById(R.id.regFirstname)
        middlename = findViewById(R.id.regMiddlename)
        lastname = findViewById(R.id.regLastname)
        password = findViewById(R.id.regPassword)
        register = findViewById(R.id.regRegister)
        back = findViewById(R.id.regBack)
        logo = findViewById(R.id.ivLogo2)
        logo.setImageResource(R.mipmap.ic_bh_newlogo)

        register.setOnClickListener {
            var e_mail = email.text.toString().trim()
            var pass = password.text.toString().trim()

            if (e_mail.isEmpty()) {
                Toast.makeText(this, "Please provide an email!", Toast.LENGTH_SHORT).show()
            }
            else if (firstname.text.isEmpty()) {
                Toast.makeText(this, "Please enter your first name!", Toast.LENGTH_SHORT).show()
            }
            else if (lastname.text.isEmpty()) {
                Toast.makeText(this, "Please enter your lastname name!", Toast.LENGTH_SHORT).show()
            }
            else if (password.text.isEmpty()) {
                Toast.makeText(this, "Please provide a password!", Toast.LENGTH_SHORT).show()
            }
            else {
                db.collection("users").document(e_mail).get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        Toast.makeText(this, "Email already exist!", Toast.LENGTH_SHORT).show()

                    } else {
                        val auth = FirebaseAuth.getInstance()

                        auth.createUserWithEmailAndPassword(e_mail, pass)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser

                                    user?.let {
                                        val userDetails = hashMapOf(
                                            "firstname" to firstname.text.toString().trim(),
                                            "middlename" to middlename.text.toString().trim(),
                                            "lastname" to lastname.text.toString().trim()
                                        )

                                        val db = FirebaseFirestore.getInstance()
                                        db.collection("users").document(user.uid).set(userDetails)
                                            .addOnSuccessListener {
                                                Toast.makeText(this, "Registration successful! You will be redirected to the login.", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(this, Login::class.java)
                                                Handler(Looper.getMainLooper()).postDelayed({ startActivity(intent) }, 3000)
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w("FirestoreError", "Error writing document", e)
                                            }
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    task.exception?.let {
                                        Log.e("AuthError", "Authentication failed: ${it.message}")
                                        Toast.makeText(this, "Authentication failed: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                    }
                }
            }
        }

        back.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }
}