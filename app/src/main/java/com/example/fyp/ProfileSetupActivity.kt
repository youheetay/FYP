package com.example.fyp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        auth  = FirebaseAuth.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser


        val userName : EditText = findViewById(R.id.profileSetUser)
        val userEmail : TextView = findViewById(R.id.profileEmail)
        val userGender : Spinner = findViewById(R.id.profileSetGender)
        val userContact : EditText = findViewById(R.id.profileSetContact)
        val proceedBtn : Button = findViewById(R.id.proceedBtn)

        userEmail.setText(currentUser?.email)

        proceedBtn.setOnClickListener{
            val name = userName.text.toString()
            val email = userEmail.text.toString()
            val contactStr = userContact.text.toString()
            val gender = userGender.selectedItem.toString()
            val userId = currentUser?.uid
            val contact = contactStr

            if (userId != null) {
                val user = User(
                    userID = userId,
                    userName = name,
                    userEmail= email,
                    userContact = contact,
                    userGender = gender
                )
                db.collection("users").document(userId)
                    .set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Profile Setup Successfully!",Toast.LENGTH_SHORT).show()
                        val MainIntent = Intent(this, MainActivity::class.java)
                        startActivity(MainIntent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this,"Profile Setup Failed!",Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

}