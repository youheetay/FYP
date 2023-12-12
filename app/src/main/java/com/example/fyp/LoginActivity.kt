package com.example.fyp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val userId : EditText = findViewById(R.id.username)
        val userPassword : EditText = findViewById(R.id.password)
        val loginBtn : Button = findViewById(R.id.loginBtn)
        val forgetPasswordText : TextView = findViewById(R.id.forgetPasswordText)
        val signupText : TextView = findViewById(R.id.signupText)


        loginBtn.setOnClickListener {
            val email = userId.text.toString()
            val password = userPassword.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            db.collection("users").document(userId)
                                .get()
                                .addOnSuccessListener { documentSnapshot ->
                                    if (documentSnapshot.exists()) {
                                        val mainIntent = Intent(this, MainActivity::class.java)
                                        startActivity(mainIntent)
                                        finish()
                                    } else {
                                        val profileSetupIntent =
                                            Intent(this, ProfileSetupActivity::class.java)
                                        startActivity(profileSetupIntent)
                                        finish()
                                    }
                                }
                        }
//                        val profileSetupIntent = Intent(this, ProfileSetupActivity::class.java)
//                        startActivity(profileSetupIntent)
//                        finish()
                    }else{
                        Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if(email.isEmpty()){
                Toast.makeText(this,"Email cannot be empty!",Toast.LENGTH_SHORT).show()
            }
            else if(password.isEmpty()){
                Toast.makeText(this,"Password cannot be empty!",Toast.LENGTH_SHORT).show()
            }



        }

        forgetPasswordText.setOnClickListener{
            val resetPassIntent = Intent(this, ResetActivity::class.java)
            startActivity(resetPassIntent)
        }

        signupText.setOnClickListener {
            val signupIntent = Intent(this, RegisterActivity::class.java)
            startActivity(signupIntent)
        }
    }
}