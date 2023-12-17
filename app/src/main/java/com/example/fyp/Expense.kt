package com.example.fyp

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore

data class Expense(
    var id: String? = null,
    var eName: String = "",
    var eNum: Double = 0.00,
    var eDate: String = "",
    var eCategory: String = "",
    var isButtonsLayoutVisible: Boolean = false,
    var accountId: String = "",
    val userId: String? = null,
    var numberSequence: Int? = null
) {
    companion object {
        // Counter variable to keep track of the numberSequence
        private var counter: Int = 0

        // Function to get the next numberSequence
        fun getNextNumberSequence(context: Context, callback: (Int) -> Unit) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId != null) {
                val db = FirebaseFirestore.getInstance()
                db.collection("Expense")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener { result ->
                        counter = result.size()
                        callback.invoke(counter)
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Invalid user ID", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // Exclude the companion object from Firestore serialization
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "eName" to eName,
            "eNum" to eNum,
            "eDate" to eDate,
            "eCategory" to eCategory,
            "isButtonsLayoutVisible" to isButtonsLayoutVisible,
            "accountId" to accountId,
            "userId" to userId,
            "numberSequence" to numberSequence
        )
    }
}
