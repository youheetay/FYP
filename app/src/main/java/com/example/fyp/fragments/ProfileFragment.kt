package com.example.fyp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.fyp.LoginActivity
import com.example.fyp.R
import com.example.fyp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profilePhone: TextView
    private lateinit var profileGender: TextView
    private lateinit var icGenderMale: ImageView
    private lateinit var icGenderFemale: ImageView
    private lateinit var editProfileBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var editProfileDialog: AlertDialog
    private lateinit var profilePic: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        profileName = rootView.findViewById(R.id.profileName)
        profileEmail = rootView.findViewById(R.id.profileEmail)
        profilePhone = rootView.findViewById(R.id.profilePhone)
        profileGender = rootView.findViewById(R.id.profileGender)
        icGenderMale = rootView.findViewById(R.id.icGenderMale)
        icGenderFemale = rootView.findViewById(R.id.icGenderFemale)
        profilePic = rootView.findViewById(R.id.profilePic)
        editProfileBtn = rootView.findViewById(R.id.editProfileBtn)
        logoutBtn = rootView.findViewById(R.id.logoutBtn)


        // Get the current user's ID
        val userId = auth.currentUser?.uid

        // Get the reference to the user's document in Firestore
        val userRef: DocumentReference = db.collection("users").document(userId!!)

        // Retrieve data from Firestore
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.result
                if (document != null && document.exists()) {
                    val user: User = document.toObject(User::class.java)!!

                    // Set data to TextViews
                    profileName.text = user.userName
                    profileEmail.text = user.userEmail
                    profilePhone.text = user.userContact
                    profileGender.text = user.userGender

                    if (user.userGender.equals("male", ignoreCase = true)) {
                        icGenderMale.visibility = View.VISIBLE
                        icGenderFemale.visibility = View.INVISIBLE
                        profilePic.setImageResource(R.drawable.boy)
                    } else if (user.userGender.equals("female", ignoreCase = true)) {
                        icGenderMale.visibility = View.INVISIBLE
                        icGenderFemale.visibility = View.VISIBLE
                        profilePic.setImageResource(R.drawable.girl)
                    }



                } else {
                    Toast.makeText(context, "Document does not exist", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Error getting document", Toast.LENGTH_SHORT).show()
            }
        }

        editProfileBtn.setOnClickListener{
            val fragmentTransaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.main_frame, EditProfileFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        logoutBtn.setOnClickListener{
            auth.signOut()

            Toast.makeText(context, "Signout successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return rootView
    }



}
