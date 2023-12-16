package com.example.fyp

import com.google.firebase.firestore.Exclude

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
        fun getNextNumberSequence(): Int {
            counter++
            return counter
        }

        // Reset the counter (call this when needed, like when the app starts)
        fun resetCounter() {
            counter = 0
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