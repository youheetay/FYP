 package com.example.fyp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.play.core.integrity.e
import com.google.firebase.auth.FirebaseAuth

 class ResetActivity : AppCompatActivity() {

    private lateinit var resetBtn : Button
    private lateinit var editEmail : EditText
    private lateinit var backBtn : Button
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        editEmail = findViewById(R.id.resetEmail)
        resetBtn = findViewById(R.id.resetBtn)
        backBtn = findViewById(R.id.backBtn)

        auth = FirebaseAuth.getInstance()

        resetBtn.setOnClickListener{
            val emailStr = editEmail.text.toString()
            if(emailStr != null){
                auth.sendPasswordResetEmail(emailStr)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Password reset email sent to $emailStr",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this,"Failed to send reset email. ${e.message}",Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(this,"Email is null",Toast.LENGTH_SHORT).show()
            }
        }

        backBtn.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

 }