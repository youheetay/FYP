package com.example.fyp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.Account
import com.example.fyp.PayActivity
import com.example.fyp.R
import com.example.fyp.adapter.accountAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountFragment : Fragment(), accountAdapter.OnButtonClickListener {

    private lateinit var recyclerView : RecyclerView
    private lateinit var accountList : ArrayList<Account>
    private lateinit var accountAdapter: accountAdapter

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_account, container, false)

        recyclerView = rootView.findViewById(R.id.accountRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        accountList = arrayListOf()

        accountAdapter = accountAdapter(accountList,this)

        recyclerView.adapter = accountAdapter

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            val userId = currentUser.uid
            EventChangeListener(userId)
        }

        rootView.findViewById<Button>(R.id.addAccountBtn).setOnClickListener{
            createBtn()
        }

        rootView.findViewById<Button>(R.id.paypalButton).setOnClickListener{
            val paymentIntent = Intent(requireContext(), PayActivity::class.java)
            startActivity(paymentIntent)
        }

        return rootView
    }

    private fun EventChangeListener(userId: String){

        // Example of data retrieval in your Fragment/Activity
        db.collection("Account")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                accountList.clear() // Clear existing data
                for (document in result) {
                    val account = document.toObject(Account::class.java)
                    accountList.add(account)
                }
                accountAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error getting data: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createBtn() {
        val inflater = LayoutInflater.from(requireContext())
        val v = inflater.inflate(R.layout.card_account_add, null)

        val addDialog = AlertDialog.Builder(requireContext())
            .setView(v)
            .create()

        addDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Create"){ dialog, _ ->
            val accName = v.findViewById<EditText>(R.id.accountNameAdd).text.toString()
            val accCardNumStr = v.findViewById<EditText>(R.id.accountCardNumAdd).text.toString()
            val accDateStr = v.findViewById<EditText>(R.id.cardDateAdd).text.toString()
            val accCodeStr = v.findViewById<EditText>(R.id.cardCodeAdd).text.toString()
            val accAmountStr = v.findViewById<EditText>(R.id.accountAmountAdd).text.toString()

            if(validateInput(accName,accCardNumStr,accDateStr,accCodeStr,accAmountStr)){

                val accCardNum = accCardNumStr.toInt()
                val accDate = accDateStr.toInt()
                val accCode = accCodeStr.toInt()
                val accAmount = accAmountStr.toDouble()
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser?.uid

                val account = Account(
                    accName = accName,
                    accCardNumber = accCardNum,
                    accCardDate = accDate,
                    accCardCode = accCode,
                    accCardAmount = accAmount,
                    userId = userId
                )

                db.collection("Account").add(account)
                    .addOnSuccessListener {documentReference ->
                        // After successfully adding to Firestore, you can get the document ID
                        val documentId = documentReference.id
                        db.collection("Account")
                            .document(documentId)
                            .update("id", documentId)
                            .addOnSuccessListener {
                                // Log success
                                Log.d("DashBoardFragment", "Document ID added successfully")
                            }
                            .addOnFailureListener { exception ->
                                // Log error
                                Log.e("DashBoardFragment", "Error adding Document ID: $exception")
                            }
                        account.id = documentId
                        Toast.makeText(requireContext(), "Upload Successful", Toast.LENGTH_SHORT).show()
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser != null){
                            val userId = currentUser.uid
                            EventChangeListener(userId)
                        }
                        dialog.dismiss()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
                    }


            }
            dialog.dismiss()
        }

        addDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        addDialog.show()
    }

    private fun validateInput(accName: String, accCardNumber: String, accDate : String, accCode : String, accAmount : String): Boolean
    {
        if (accName.isEmpty() || accCardNumber.isEmpty() || accDate.isEmpty() || accCode.isEmpty() || accAmount.isEmpty()) {
            // Show an error message for empty fields
            Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }


        return true // All validation checks passed
    }

    override fun onEditButtonClick(position: Int) {
        // Handle edit button click
        // Implement the logic to show the edit dialog or navigate to the edit screen
        // You can use the position parameter to get the clicked item in the list
        Toast.makeText(requireContext(), "Edit button clicked for position $position", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteButtonClick(position: Int) {
        val accountToDelete = accountList[position]

        // Perform deletion in Firestore
        db.collection("Account")
            .document(accountToDelete.id!!)
            .delete()
            .addOnSuccessListener {
                // Successfully deleted from Firestore
                Log.d("AccountFragment", "DocumentSnapshot successfully deleted!")
                Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()

                // Remove the deleted item from the local list
                accountList.removeAt(position)
                accountAdapter.notifyItemRemoved(position)
            }
            .addOnFailureListener { e ->
                // Log error and show a toast message
                Log.e("AccountFragment", "Error deleting document", e)
                Toast.makeText(requireContext(), "Error deleting account", Toast.LENGTH_SHORT).show()
            }
    }

}