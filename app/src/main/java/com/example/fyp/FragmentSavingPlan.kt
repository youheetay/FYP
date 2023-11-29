package com.example.fyp

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.ktx.Firebase


class FragmentSavingPlan : Fragment() {

    private lateinit var budgetBtn : Button
    private lateinit var savingBtn : Button
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =  inflater.inflate(R.layout.fragment_saving_plan, container, false)

        budgetBtn = rootView.findViewById(R.id.budgetBtn)
        savingBtn = rootView.findViewById(R.id.savingBtn)

        budgetBtn.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView =  inflater.inflate(R.layout.activity_budget, container, false)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
            //show dialog
            val mAlertDialog = mBuilder.show()

            //login button click of custom layout
           mDialogView.findViewById<Button>(R.id.saveBtn).setOnClickListener {
               //dismiss dialog
               mAlertDialog.dismiss()
               //get text from EditTexts of custom layout
               val name = mDialogView.findViewById<EditText>(R.id.savingName).text.toString()
               val periodInputLayout = mDialogView.findViewById<TextInputLayout>(R.id.periods)
               val autoCompleteTextView = periodInputLayout.findViewById<AutoCompleteTextView>(R.id.period) // Replace R.id.autoCompleteTextView with the actual ID of the AutoCompleteTextView inside TextInputLayout
               val period = autoCompleteTextView.text.toString()
               val targetAmt = mDialogView.findViewById<EditText>(R.id.targetAmount).text.toString()

               val budget = Budget(
                   budgetName = name,
                   period = period,
                   targetAmount = targetAmt.toDoubleOrNull() // Assuming editTargetAmount is a String
               )

                // Store the Budget object in Firestore
               db.collection("budgets")
                   .add(budget)
                   .addOnSuccessListener { documentReference ->
                       Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show()
                   }
                   .addOnFailureListener { error ->
                       Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                   }



           }
        }

        savingBtn.setOnClickListener {

        }

        return rootView
    }


}