package com.example.fyp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    //private lateinit var mDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val userId : EditText = findViewById(R.id.regUser)
        val userPassword : EditText = findViewById(R.id.regPassword)
        val userDoublePassword : EditText = findViewById(R.id.regPasswordConfirm)
        val regBtn : Button = findViewById(R.id.registerBtn)
        val loginText : TextView = findViewById(R.id.signInText)

        regBtn.setOnClickListener {
            val email = userId.text.toString()
            val password = userPassword.text.toString()
            val doublePass = userDoublePassword.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty() && doublePass.isNotEmpty()){
                if(password == doublePass){
                    auth.createUserWithEmailAndPassword(email,doublePass).addOnCompleteListener{
                        if(it.isSuccessful){
                            val intent = Intent(this,LoginActivity::class.java)
                            Toast.makeText(this,"Register Successful", Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this,"Password does not matched!",Toast.LENGTH_SHORT).show()
                }
            }
            else if(email.isEmpty()){
                Toast.makeText(this,"Email cannot be empty!",Toast.LENGTH_SHORT).show()
            }
            else if(password.isEmpty() || doublePass.isEmpty()){
                Toast.makeText(this,"Password cannot be empty!",Toast.LENGTH_SHORT).show()
            }
        }

        loginText.setOnClickListener{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
}