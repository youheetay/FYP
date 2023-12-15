package com.example.fyp.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.Account
import com.example.fyp.Expense
import com.example.fyp.R
import com.example.fyp.adapter.dashboardAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DashBoardFragment : Fragment() {

    private lateinit var datePicker : DatePickerDialog
    private lateinit var dateButton : Button
    private lateinit var editDateButton: Button
    private lateinit var recyclerView : RecyclerView
    private lateinit var expenseList : ArrayList<Expense>
    private lateinit var dashboardAdapter: dashboardAdapter
    private var selectedExpense: Expense? = null
    private lateinit var setThisMonthExpense: TextView
    private lateinit var setLastMonthExpense: TextView
    private lateinit var accounts: List<Account>


    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_dash_board, container, false)

        recyclerView = rootView.findViewById(R.id.dashboardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        expenseList = arrayListOf()

        dashboardAdapter = dashboardAdapter(expenseList,
            onCardClickListener = { position ->
                val clickedExpense = expenseList[position]
                clickedExpense.isButtonsLayoutVisible = !clickedExpense.isButtonsLayoutVisible
                dashboardAdapter.notifyItemChanged(position)
            },
            onEditClickListener = { position ->
                val clickedExpense = expenseList[position]
                editExpense(clickedExpense)
            }
        )

        recyclerView.adapter = dashboardAdapter


        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            val userId = currentUser.uid
            EventChangeListener(userId)
        }

        rootView.findViewById<FloatingActionButton>(R.id.createExpense).setOnClickListener{
            addExpense()
        }

        setThisMonthExpense = rootView.findViewById(R.id.setThisMonthExpense)
        setLastMonthExpense = rootView.findViewById(R.id.setLastMonthExpense)


        return rootView
    }

    private fun editExpense(expense : Expense) {
        val inflater = LayoutInflater.from(requireContext())
        val v = inflater.inflate(R.layout.card_edit_expense, null)

        val editDialog = AlertDialog.Builder(requireContext())
            .setView(v)
            .create()

        v.findViewById<EditText>(R.id.expenseNameEdit).setText(expense.eName)
        v.findViewById<EditText>(R.id.amountExpenseEdit).setText(expense.eNum.toString())
        editDateButton = v.findViewById(R.id.datePickerBtnEdit)
        editDateButton.setText(expense.eDate)
        initDatePickerEdit(v)

        val spinnerCategory = v.findViewById<Spinner>(R.id.spinnerExpenseEdit)
        val categories = resources.getStringArray(R.array.category).toMutableList()
        val hint = "Select Category"

        categories.add(0, hint)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
        spinnerCategory.setSelection(categories.indexOf(expense.eCategory))


        editDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Edit") { dialog, _ ->
            val eName = v.findViewById<EditText>(R.id.expenseNameEdit).text.toString()
            val eNumStr = v.findViewById<EditText>(R.id.amountExpenseEdit).text.toString()
            val eDate = editDateButton.text.toString()
            val eCategory = spinnerCategory.selectedItem.toString()
            val eAccount = expense.accountId

            if (validateInput(eName, eNumStr, eCategory,eAccount)) {
                val eNum = eNumStr.toDouble()

                // Update the fields of the selectedExpense
                expense.eName = eName
                expense.eNum = eNum
                expense.eDate = eDate
                expense.eCategory = eCategory

                // Update the document in Firestore
                updateExpenseInFirestore(expense)

                dialog.dismiss()
            }
        }

        // Set up the negative button to cancel the edit
        editDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        editDialog.show()

    }


    private fun updateExpenseInFirestore(expense: Expense) {
        // Assuming that you have an 'id' field in your Expense class
        val documentId = expense.id
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Update the document in Firestore
        if (documentId != null) {
            db.collection("Expense")
                .document(documentId)
                .set(expense)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Expense updated successfully", Toast.LENGTH_SHORT).show()
                    // You might want to refresh your data after an update
                    val userId = currentUser?.uid
                    if (userId != null) {
                        EventChangeListener(userId)
                    }
                }
                .addOnFailureListener { error ->
                    Toast.makeText(requireContext(), "Error updating expense: $error", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun addExpense() {
        val inflater = LayoutInflater.from(requireContext())
        val v = inflater.inflate(R.layout.expense_add_card, null)

        val addDialog = AlertDialog.Builder(requireContext())
            .setView(v)
            .create()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val spinnerAccount = v.findViewById<Spinner>(R.id.spinnerAccount)
        val spinnerAccountValue = populateAccountSpinner(spinnerAccount)
        val numberSequence = Expense.getNextNumberSequence()

        initDatePicker(v)
        dateButton = v.findViewById(R.id.datePickerBtn)
        dateButton.setText(getTodaysDate())
        val spinnerCategory = v.findViewById<Spinner>(R.id.spinnerExpense)
        val categories = resources.getStringArray(R.array.category).toMutableList()
        val hint = "Select Category"

        categories.add(0, hint)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        addDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ok") { dialog, _ ->
            val eName = v.findViewById<EditText>(R.id.expenseNameAdd).text.toString()
            val eNumStr = v.findViewById<EditText>(R.id.amountExpenseAdd).text.toString()
            val eDate = dateButton.text.toString()
            val eCategory = spinnerCategory.selectedItem.toString()
            val accountId = getSelectedAccountId(spinnerAccount)

            val userId = currentUser?.uid

            if (validateInput(eName, eNumStr,eCategory,accountId)) {
                val eNum = eNumStr.toDouble()

                val expense = Expense(
                    eName = eName,
                    eNum = eNum,
                    eDate = eDate,
                    eCategory = eCategory,
                    userId = userId,
                    accountId = accountId,
                    numberSequence = numberSequence
                )

                deductAmountFromAccount(accountId, eNum)

                db.collection("Expense")
                    .add(expense)
                    .addOnSuccessListener { documentReference ->
                        // After successfully adding to Firestore, you can get the document ID
                        val documentId = documentReference.id

                        // Now you can update the document with the document ID
                        db.collection("Expense")
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

                        // Update the Expense object with the document ID
                        expense.id = documentId

                        Toast.makeText(requireContext(), "Upload Successful", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()

                        val userId = currentUser?.uid
                        if (userId != null) {
                            EventChangeListener(userId)
                        }
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
                    }
            }
        }

        addDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        addDialog.show()
    }

    private fun populateAccountSpinner(spinner: Spinner) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("Account")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    accounts = result.toObjects(Account::class.java)

                    // Populate the spinner with account names
                    val accountNames = accounts.map { it.accName }.toMutableList()
                    val hint = "Select Account"
                    accountNames.add(0, hint)

                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        accountNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Error getting accounts: $exception",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun getSelectedAccountId(spinner: Spinner): String {

        // Check if accounts is initialized
        if (!::accounts.isInitialized) {
            return ""
        }

        val selectedAccountName = spinner.selectedItem.toString()
        val selectedAccount = accounts.find { it.accName == selectedAccountName }
        return selectedAccount?.id ?: ""
    }




    private fun deductAmountFromAccount(accountId: String, expenseAmount: Double) {
        val accountRef = db.collection("Account").document(accountId)

        // Use FieldValue.increment to atomically subtract the expense amount
        accountRef.update("accCardAmount", FieldValue.increment(-expenseAmount))
            .addOnSuccessListener {
                Log.d("DashBoardFragment", "Expense amount deducted from account successfully")
            }
            .addOnFailureListener { e ->
                Log.e("DashBoardFragment", "Error deducting expense amount from account", e)
            }
    }



    // Update the function to take an optional sorting option
    private fun EventChangeListener(userId: String) {
        db.collection("Expense")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                expenseList.clear() // Clear existing data
                var currentMonthTotal = 0.0
                var previousMonthTotal = 0.0

                val currentDate = Calendar.getInstance()
                val currentYear = currentDate.get(Calendar.YEAR)
                val currentMonth = currentDate.get(Calendar.MONTH) + 1 // Months are zero-based
                val previousMonth = if (currentMonth == 1) 12 else currentMonth - 1

                for (document in result) {
                    val expense = document.toObject(Expense::class.java)
                    expenseList.add(expense)

                    // Calculate totals based on current and previous months
                    val expenseDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(expense.eDate)
                    val expenseCalendar = Calendar.getInstance().apply { time = expenseDate }

                    if (expenseCalendar.get(Calendar.YEAR) == currentYear && expenseCalendar.get(Calendar.MONTH) == currentMonth - 1) {
                        currentMonthTotal += expense.eNum
                    }

                    if (expenseCalendar.get(Calendar.YEAR) == currentYear && expenseCalendar.get(Calendar.MONTH) == previousMonth - 1) {
                        previousMonthTotal += expense.eNum
                    }
                }

                // Sort the list using a custom comparator based on date and number sequence
                expenseList.sortWith(compareByDescending<Expense> {
                    SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse("${it.eDate} 00:00:00")
                        .time
                }.thenByDescending {
                    it.numberSequence
                })

                // Update TextViews with the calculated totals
                setThisMonthExpense.text = String.format("%.2f", currentMonthTotal)
                setLastMonthExpense.text = String.format("%.2f", previousMonthTotal)

                // Notify the adapter about the data change
                dashboardAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error getting data: $exception", Toast.LENGTH_SHORT).show()
            }
    }









    private fun validateInput(eName: String, eNumStr: String, eCategory: String, eAccount: String): Boolean
    {
        if (eName.isEmpty() || eNumStr.isEmpty() || eAccount.isEmpty()) {
            // Show an error message for empty fields
            Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        val eNum: Double? = eNumStr.toDoubleOrNull()

        if (eNum == null) {
            // Show an error message for invalid eNum
            Toast.makeText(requireContext(), "Invalid number format for eNum", Toast.LENGTH_SHORT).show()
            return false
        }

        if(eCategory == "Select Category"){
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
            return false
        }

        if(eAccount == "Select Account"){
            Toast.makeText(requireContext(), "Please select an account", Toast.LENGTH_SHORT).show()
            return false
        }

        return true // All validation checks passed
    }


    //Calendar Function
    private fun getTodaysDate(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        var month = cal.get(Calendar.MONTH)
        month += 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
    }

    private fun initDatePicker(view: View) {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val formattedDate = makeDateString(day, month + 1, year)
                dateButton.text = formattedDate
            }

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val style = AlertDialog.THEME_HOLO_LIGHT

        datePicker = DatePickerDialog(requireContext(), style, dateSetListener, year, month, day)

        // Set a listener to open the date picker when the button is clicked
        view.findViewById<Button>(R.id.datePickerBtn).setOnClickListener {
            datePicker.show()
        }
    }

    private fun initDatePickerEdit(view: View) {
        editDateButton = view.findViewById(R.id.datePickerBtnEdit)

        val dateComponents = getDateComponents(editDateButton.text.toString())

        if(dateComponents != null){
            val day = dateComponents.first
            val month = dateComponents.second
            val year = dateComponents.third

            val style = AlertDialog.THEME_HOLO_LIGHT

            datePicker = DatePickerDialog(requireContext(), style, { _, year, month, day ->
                val formattedDate = makeDateString(day, month, year)
                editDateButton.text = formattedDate
            }, year, month - 1, day)

            // Set a listener to open the date picker when the button is clicked
            editDateButton.setOnClickListener {
                datePicker.show()
            }
        }



    }

    fun getDateComponents(dateString: String): Triple<Int, Int, Int>? {
        try {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = dateFormat.parse(dateString)

            val calendar = Calendar.getInstance()
            calendar.time = date

            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1 // Calendar months are zero-based
            val year = calendar.get(Calendar.YEAR)

            return Triple(day, month, year)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return "$day-$month-$year"
    }

}