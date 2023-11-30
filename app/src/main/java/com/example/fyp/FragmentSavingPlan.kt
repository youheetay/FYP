package com.example.fyp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FragmentSavingPlan : Fragment() {

    private lateinit var budgetBtn: Button
    private lateinit var savingBtn: Button
    private lateinit var budgetArrayList: ArrayList<Budget>
    private lateinit var recyclerView: RecyclerView
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_saving_plan, container, false)

        budgetBtn = rootView.findViewById(R.id.budgetBtn)
        savingBtn = rootView.findViewById(R.id.savingBtn)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        budgetArrayList = arrayListOf()

        // Check if the Firestore collection has data
        db.collection("budgets")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isNotEmpty()) {
                    // Firestore collection has data, show RecyclerView
                    showRecyclerViewWithData()
                } else {
                    // Firestore collection is empty, set up OnClickListener for adding new data
                    setupOnClickListenerForAddingData()
                }
            }
            .addOnFailureListener { error ->
                // Handle failure
                Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
            }

        savingBtn.setOnClickListener {
            // Handle saving button click
        }

        return rootView
    }

    // Function to show RecyclerView with data
    private fun showRecyclerViewWithData() {
        // TODO: Implement code to show RecyclerView with data

    }

    // Function to set up OnClickListener for adding new data
    private fun setupOnClickListenerForAddingData() {
        budgetBtn.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = layoutInflater.inflate(R.layout.activity_budget, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(requireContext()).setView(mDialogView)
            //show dialog
            val mAlertDialog = mBuilder.show()

            //login button click of custom layout
            mDialogView.findViewById<ImageButton>(R.id.saveBtn).setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
                //get text from EditTexts of custom layout
                val name = mDialogView.findViewById<EditText>(R.id.savingName).text.toString()
                val periodInputLayout = mDialogView.findViewById<TextInputLayout>(R.id.periods)
                val autoCompleteTextView =
                    periodInputLayout.findViewById<AutoCompleteTextView>(R.id.period)
                val period = autoCompleteTextView.text.toString()
                val targetAmt =
                    mDialogView.findViewById<EditText>(R.id.targetAmount).text.toString()

                val budget = Budget(
                    budgetName = name,
                    period = period,
                    targetAmount = targetAmt.toDoubleOrNull() // Assuming editTargetAmount is a String
                )

                // Store the Budget object in Firestore
                db.collection("budgets")
                    .add(budget)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(
                            requireContext(),
                            "Upload Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(
                            requireContext(),
                            error.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            mDialogView.findViewById<ImageButton>(R.id.cancelBtn).setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
            }
        }
    }
}

