package com.example.fyp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.Account
import com.example.fyp.Expense
import com.example.fyp.PayPalActivity
import com.example.fyp.R
import com.example.fyp.adapter.accountAdapter
import com.example.fyp.adapter.dashboardAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountFragment : Fragment() {

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

        accountAdapter = accountAdapter(accountList)

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
            val intent = Intent(requireContext(), PayPalActivity::class.java)
            startActivity(intent)
//            val paypalFragment = PaypalFragment()
//            val fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.main_frame, paypalFragment)
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.commit()
        }

        return rootView
    }

    private fun EventChangeListener(userId: String){

        // Example of data retrieval in your Fragment/Activity
        db.collection("Account")
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
                val accDate = accCardNumStr.toInt()
                val accCode = accCardNumStr.toInt()
                val accAmount = accCardNumStr.toDouble()

                val account = Account(
                    accName = accName,
                    accCardNumber = accCardNum,
                    accCardDate = accDate,
                    accCardCode = accCode,
                    accCardAmount = accAmount
                )

                db.collection("Account").add(account)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Upload Successful", Toast.LENGTH_SHORT).show()
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

}