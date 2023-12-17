package com.example.fyp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.fyp.R
import com.example.fyp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileFragment : Fragment() {

    private lateinit var editUserName : EditText
    private lateinit var editContact : EditText
    private lateinit var editGender : Spinner
    private lateinit var editEmail : TextView
    private lateinit var editBtn : Button
    private lateinit var cancelBtn : Button
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var icGenderMale: ImageView
    private lateinit var icGenderFemale: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        editUserName = rootView.findViewById(R.id.profileEditName)
        editContact = rootView.findViewById(R.id.profileEditPhone)
        editGender = rootView.findViewById(R.id.profileEditGender)
        editEmail = rootView.findViewById(R.id.profileEditEmail)
        editBtn = rootView.findViewById(R.id.editProfileConfirmBtn)
        cancelBtn = rootView.findViewById(R.id.cancelEditProfileBtn)
        icGenderMale = rootView.findViewById(R.id.icGenderMale)
        icGenderFemale = rootView.findViewById(R.id.icGenderFemale)

        displayUserInfo()
        editBtn.setOnClickListener {
            editUserProfile()
        }

        cancelBtn.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        return rootView
    }

    // Inside EditProfileFragment

    private fun displayUserInfo() {
        val userId = auth.currentUser?.uid
        val userRef: DocumentReference = db.collection("users").document(userId!!)

        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.result
                if (document != null && document.exists()) {
                    val user: User = document.toObject(User::class.java)!!

                    // Display user information in the EditText and TextView
                    editUserName.setText(user.userName)
                    editContact.setText(user.userContact)
                    editEmail.text = user.userEmail

                    // Set up gender spinner
                    val genderArray = resources.getStringArray(R.array.gender)
                    val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderArray)
                    genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    editGender.adapter = genderAdapter

                    // Set current gender value as selected
                    val currentGender = user.userGender
                    val genderPosition = genderArray.indexOf(currentGender)
                    editGender.setSelection(genderPosition.coerceIn(0, genderArray.size - 1))

                    if (user.userGender.equals("male", ignoreCase = true)) {
                        icGenderMale.visibility = View.VISIBLE
                        icGenderFemale.visibility = View.INVISIBLE
                    } else if (user.userGender.equals("female", ignoreCase = true)) {
                        icGenderMale.visibility = View.INVISIBLE
                        icGenderFemale.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(context, "Document does not exist", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Error getting document: ${task.exception}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editUserProfile() {
        val userId = auth.currentUser?.uid
        val userRef: DocumentReference = db.collection("users").document(userId!!)

        val newUsername = editUserName.text.toString().trim()
        val newPhoneStr = editContact.text.toString().trim()
        val newGender = editGender.selectedItem.toString()

        if (validateInputs(newUsername, newPhoneStr, newGender)) {
            try {
                val newPhone = newPhoneStr

                userRef.update(
                    mapOf(
                        "userName" to newUsername,
                        "userContact" to newPhone,
                        "userGender" to newGender
                    )
                )
                    .addOnSuccessListener {
                        Log.d("EditProfileFragment", "User profile updated successfully")
                        Toast.makeText(requireContext(), "Profile Update Successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditProfileFragment", "Error updating user profile", e)
                    }
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Invalid phone number format", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun validateInputs(username: String, phone: String, gender: String): Boolean {
        // Implement your validation logic here, for example, check if fields are not empty
        if (username.isEmpty() || phone.isEmpty() || gender == "Select Gender") {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}