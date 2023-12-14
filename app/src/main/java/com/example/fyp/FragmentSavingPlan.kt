package com.example.fyp

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class FragmentSavingPlan : Fragment() {

    private lateinit var budgetBtn: Button
    private lateinit var savingBtn: Button
    private lateinit var hintTV : TextView
    private lateinit var savingHintTV : TextView
    private lateinit var budgetList: ArrayList<Budget>
    private lateinit var expenseList:ArrayList<Expense>
    private lateinit var savingArrayList: ArrayList<SavingPlan>
    private lateinit var recyclerView: RecyclerView
    private lateinit var savingRecycler: RecyclerView
    private lateinit var BudgetRecyclerAdapter: BudgetRecyclerAdapter
    private lateinit var SavingRecyclerAdapter: SavingRecyclerAdapter
    private lateinit var savingTV: TextView
    private lateinit var budgetTV: TextView // Assuming you have a TextView for empty data
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
        val layoutInflater: LayoutInflater // pass the layout inflater here

        budgetBtn = rootView.findViewById(R.id.budgetBtn)
        savingBtn = rootView.findViewById(R.id.savingBtn)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        hintTV = rootView.findViewById(R.id.hintTV)
        savingHintTV = rootView.findViewById(R.id.savingHintTV)
        savingRecycler = rootView.findViewById(R.id.savingRecycler)
        savingTV = rootView.findViewById(R.id.savingTV)
        budgetTV = rootView.findViewById(R.id.budgetTV) // Adjust the ID accordingly

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        savingRecycler.layoutManager = LinearLayoutManager(requireContext())
        savingRecycler.setHasFixedSize(true)

        budgetList = arrayListOf()
        savingArrayList = arrayListOf()
        expenseList = arrayListOf()

        BudgetRecyclerAdapter = BudgetRecyclerAdapter(requireContext(), budgetList, requireContext(),expenseList)
        recyclerView.adapter = BudgetRecyclerAdapter
        SavingRecyclerAdapter = SavingRecyclerAdapter(requireContext(),savingArrayList, requireContext())
        savingRecycler.adapter = SavingRecyclerAdapter

        // Check if the Firestore collection has data
        db.collection("budgets")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Firestore collection has data, show RecyclerView
                    budgetTV.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    hintTV.visibility = View.VISIBLE

                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid
                    if (userId != null) {
                        EventChangeListener(userId)
                    }
//                    EventChangeListener()

                } else {

                    budgetTV.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE


                }
            }
            .addOnFailureListener { error ->
                // Handle failure
                Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
            }

        // Check if the Firestore collection has data
        db.collection("savings")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Firestore collection has data, show RecyclerView
                    savingTV.visibility = View.GONE
                    savingRecycler.visibility = View.VISIBLE
                    savingHintTV.visibility = View.VISIBLE
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid
                    if (userId != null) {
                        SavingEventChangeListener(userId)
                    }

                } else {

                    savingTV.visibility = View.VISIBLE
                    savingRecycler.visibility = View.GONE
                    savingHintTV.visibility = View.GONE


                }
            }
            .addOnFailureListener { error ->
                // Handle failure
                Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
            }


        setupOnClickListenerForAddingData()

        return rootView
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

            val currentUser = FirebaseAuth.getInstance().currentUser

            //login button click of custom layout
            mDialogView.findViewById<ImageButton>(R.id.savePlanBtn).setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
                //get text from EditTexts of custom layout
                val name = mDialogView.findViewById<EditText>(R.id.budgetname).text.toString()
                val spinner = mDialogView.findViewById<Spinner>(R.id.spinner)
                val budgetPeriod = resources.getStringArray(R.array.budgetPeriod)
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,budgetPeriod)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                val budgetC = spinner.selectedItem.toString()
                val targetAmt =
                    mDialogView.findViewById<EditText>(R.id.targetAmount).text.toString()
                val userId = currentUser?.uid

                val budget = Budget(
                    budgetID = "",
                    budgetName = name,
                    category = budgetC,
                    targetAmount = targetAmt.toDoubleOrNull(), // Assuming editTargetAmount is a String
                    userID = userId ?: "" // Set the userID
                )

                // Store the Budget object in Firestore
                db.collection("budgets")
                    .add(budget)
                    .addOnSuccessListener { documentReference ->
                        // Update the documentId in the local Budget object
                        val newDocumentId = documentReference.id
                        db.collection("budgets")
                            .document(newDocumentId)
                            .update("budgetID",newDocumentId)
                            .addOnSuccessListener{
                            Toast.makeText(
                                requireContext(),
                                "Upload Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                        }.addOnFailureListener { error ->
                                Toast.makeText(
                                    requireContext(),
                                    error.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        val userId = currentUser?.uid
                        if (userId != null) {
                            EventChangeListener(userId)
                        }

                    }


            }

            mDialogView.findViewById<ImageButton>(R.id.cancelPlanBtn).setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
            }
        }

        savingBtn.setOnClickListener {
            // Handle saving button click
            //Inflate the dialog with custom view
            val mDialogView = layoutInflater.inflate(R.layout.activity_saving, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(requireContext()).setView(mDialogView)
            //show dialog
            val mAlertDialog = mBuilder.show()
            val currentUser = FirebaseAuth.getInstance().currentUser

            //login button click of custom layout
            mDialogView.findViewById<ImageButton>(R.id.savePlanBtn).setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
                //get text from EditTexts of custom layout
                val name = mDialogView.findViewById<EditText>(R.id.savingName).text.toString()
                val targetAmt = mDialogView.findViewById<EditText>(R.id.targetAmt2).text.toString()
                val savedAmt = mDialogView.findViewById<EditText>(R.id.savedAmount).text.toString()
                val userId = currentUser?.uid

                val savingPlan = SavingPlan(
                    savingID = "",
                    savingName = name,
                    targetAmount = targetAmt.toDoubleOrNull(), // Assuming editTargetAmount is a String
                    savedAmount = savedAmt.toDoubleOrNull(),
                    userID = userId ?: ""
                )

                // Store the Budget object in Firestore
                db.collection("savings")
                    .add(savingPlan)
                    .addOnSuccessListener { documentReference ->
                        // Update the documentId in the local Budget object
                        val newDocumentId = documentReference.id
                        db.collection("savings")
                            .document(newDocumentId)
                            .update("savingID",newDocumentId)
                            .addOnSuccessListener{
                                Toast.makeText(
                                    requireContext(),
                                    "Upload Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.addOnFailureListener { error ->
                                Toast.makeText(
                                    requireContext(),
                                    error.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    }
            }

            mDialogView.findViewById<ImageButton>(R.id.cancelPlanBtn).setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
            }

        }
    }

    private fun EventChangeListener(userId: String){

        // Example of data retrieval in your Fragment/Activity
        db.collection("budgets")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { result ->
                budgetList.clear() // Clear existing data
                for (document in result) {
                    val budget = document.toObject(Budget::class.java)
                    budgetList.add(budget)
                }

                // Notify the adapter after updating the data
                BudgetRecyclerAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error getting data: $exception", Toast.LENGTH_SHORT).show()
            }

        // Example of data retrieval in your Fragment/Activity
        db.collection("Expense")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                expenseList.clear() // Clear existing data
                for (document in result) {
                    val expense = document.toObject(Expense::class.java)
                    expenseList.add(expense)
                }

                // Notify the adapter after updating the data
                BudgetRecyclerAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error getting expense data: $exception", Toast.LENGTH_SHORT).show()
            }
    }



    private fun SavingEventChangeListener(userId: String){

        // Example of data retrieval in your Fragment/Activity
        db.collection("savings")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { result ->
//                savingArrayList.clear() // Clear existing data
                for (document in result) {
                    val saving = document.toObject(SavingPlan::class.java)
                    savingArrayList.add(saving)
                }

                SavingRecyclerAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error getting data: $exception", Toast.LENGTH_SHORT).show()
            }
    }


}
