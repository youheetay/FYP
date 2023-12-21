package com.example.fyp

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.fyp.BudgetRecyclerAdapter.MyViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.logging.Handler

class BudgetRecyclerAdapter(
    private val context: Context,
    private val budgetList : ArrayList<Budget>,
    private val parentContext: Context,
    private val expenseList : ArrayList<Expense>,
) : RecyclerView.Adapter<MyViewHolder>() {
    private var previousPercentage: Double = 0.0
    private var previousTarget: Double = 0.0
    private var previousExp: Double = 0.0
    private var db = Firebase.firestore
    private var swipeToRefreshLayout: SwipeRefreshLayout? = null

    // Add this function to set the SwipeRefreshLayout
    fun setSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
        this.swipeToRefreshLayout = swipeRefreshLayout
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.budgetlist_item, parent, false)



        return MyViewHolder(itemView)
    }

    // Flag to check if data has been updated
    private var isDataUpdated: Boolean = false

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val budgetName: TextView = itemView.findViewById(R.id.budgetName)
        val period: TextView = itemView.findViewById(R.id.Category)
        val targetAmt: TextView = itemView.findViewById(R.id.targetAmt)
        val editBtn: ImageButton = itemView.findViewById(R.id.editButton)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val budget: Budget = budgetList[position]
        holder.budgetName.text = budget.budgetName
        holder.period.text = budget.category
        holder.targetAmt.text = budget.targetAmount.toString()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        // Find the corresponding expense for the current budget
        val expense = expenseList.find { it.eCategory == budget.category && it.userId == userId }
//        val expense = expenseList
//            .filter { it.eCategory == budget.category && it.userId == userId }
//            .maxByOrNull { it.numberSequence ?: 0 }

        if (expense != null) {
            Log.d("Expense", "Expense details: $expense")
            // Update the target amount and percentage
            calculateTargetAmt(holder, budget, expense)
            updatePercentage(holder, budget, expense)


            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid
            if (userId != null) {
                EventChangeListener(userId)
            }
        }


//        Log.d("Expense", "Expense List Size: ${expenseList.size}")
//        Log.d("Expense", "Budget List Size: ${budgetList.size}")
//
//        // Find the corresponding expense for the current budget
//        val expense = expenseList.find { it.userId == userId && it.eCategory == budget.category }
//
//
//        Log.d("Expense", "Expense List Size: $expense")
//
//        // Assuming you have a list of all expenses
//        if (expense != null) {
//
//            // Update the target amount TextView
//            holder.targetAmt.text = budget.targetAmount.toString()

//            calculateTargetAmt(holder,expense,budget)

//        }

//            var currentTargetAmt = budget.target
//
//            // Subtract the expense amount from the target amount
//            val newTargetAmt = (currentTargetAmt)?.minus((expense?.eNum!!))
//            val expenseNum = expense.eNum + previousExp
//            previousExp = expenseNum
//
//
//            val newTarget = newTargetAmt?.minus(previousTarget)
//            if (newTarget != null) {
//                previousTarget  = newTarget
//            }
//
//            budget.target = newTarget
//
//            // Update the target amount TextView
//            holder.targetAmt.text = newTarget.toString()
//
//            val db = FirebaseFirestore.getInstance()
//            val updateBudget = budgetList[position]
//            val updatedData = mapOf(
//                "target" to newTarget
//            )
//
//
//            val documentId = updateBudget.budgetID // Use the existing document ID
//
//            // Update the target amount in Firestore
//            db.collection("budgets").document(documentId.toString())
//                .update(updatedData)
//                .addOnSuccessListener {
//
//                    // Calculate the percentage after updating the target amount
//                    val percentage = calculatePercentage(budget.targetAmount, expenseNum)
//
//                    // Calculate the sum of the previous percentage and the new percentage
//                    val newPercentage = previousPercentage + percentage
//
//                    // Store the current percentage for future use
//                    previousPercentage = newPercentage
//
//                    updateProgressBar(holder, newPercentage)
//
//                    // Update the target amount TextView
//                    holder.targetAmt.text = newTarget.toString()
//                    // Update successful
//                    Log.d("Expense", "Target amount updated successfully")
//
//                }
//                .addOnFailureListener { e ->
//                    // Handle failure
//                    Log.e("Expense", "Error updating target amount: ${e.message}")
//                }
//
//        }


        holder.editBtn.setOnClickListener {
            val positionUpdate = holder.adapterPosition
            val updateBudget = budgetList[positionUpdate]

            val dialogView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.fragment_budget, null)
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            val alertDialog = alertDialogBuilder.show()

            val nameEditText = dialogView.findViewById<EditText>(R.id.budgetname)
            val periodEditText = dialogView.findViewById<Spinner>(R.id.spinner)
            val amountEditText = dialogView.findViewById<EditText>(R.id.targetAmount)

            nameEditText.setText(updateBudget.budgetName)

            // Set the selected item of the spinner to the value from updateBudget
            val periodArray = holder.itemView.resources.getStringArray(R.array.category)
            val periodAdapter = ArrayAdapter(
                holder.itemView.context,
                android.R.layout.simple_spinner_item,
                periodArray
            )
            periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            periodEditText.adapter = periodAdapter

            val periodPosition = periodArray.indexOf(updateBudget.category)
            periodEditText.setSelection(periodPosition)

            amountEditText.setText(updateBudget.targetAmount.toString())

            alertDialogBuilder.setView(dialogView)
            val dialog = alertDialogBuilder.create() // Get the AlertDialog instance

            dialogView.findViewById<ImageButton>(R.id.saveEditBtn).setOnClickListener {
                //dismiss dialog
                dialog.dismiss()

                val newName = nameEditText.text.toString()
                val newCat = periodEditText.selectedItem.toString()
                val newAmount = amountEditText.text.toString().toDoubleOrNull() ?: 0.0
                val db = FirebaseFirestore.getInstance()

                updateBudgetDetails(holder, position, newName, newCat, newAmount)

            }

            dialogView.findViewById<ImageButton>(R.id.cancelEditBtn).setOnClickListener {
                //dismiss dialog
                dialog.dismiss()

            }

            dialogView.findViewById<ImageButton>(R.id.dltEditBtn).setOnClickListener {
                //dismiss dialog
                dialog.dismiss()

//              // Perform the delete operation
                deleteBudget(holder, position)

            }


            dialog.show()
//            alertDialogBuilder.show()

        }
    }

    private fun updateBudgetDetails(
        holder: MyViewHolder,
        position: Int,
        newName: String?,
        newCat: String?,
        newAmount: Double?,
    ) {
        Log.d(
            "BudgetAdapter",
            "updateBudgetDetails: position=$position, budgetList.size=${budgetList.size}"
        )
        val db = FirebaseFirestore.getInstance()

        val updateBudget = budgetList[position]
        val updatedData = mapOf(
            "budgetName" to newName,
            "category" to newCat,
            "targetAmount" to newAmount
        )
        val documentId = updateBudget.budgetID // Use the existing document ID

        db.collection("budgets").document(documentId.toString())
            .update(updatedData)
            .addOnSuccessListener {
                Log.d(
                    "BudgetAdapter",
                    "updateBudgetDetails: position=$position, budgetList.size=${budgetList.size}"
                )
                // Update the local list first
                budgetList[position].budgetName = newName
                budgetList[position].category = newCat
                budgetList[position].targetAmount = newAmount

                // Check if the list is not empty before notifying item change
                if (budgetList.isNotEmpty()) {
                    // Call notifyItemChanged after updating the local list
                    notifyItemChanged(position)
                }

                makeText(
                    holder.itemView.context,
                    "Update Success",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                makeText(
                    holder.itemView.context,
                    "Error updating budget: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun deleteBudget(holder: MyViewHolder, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val budgetToDelete = budgetList[position]
        val documentId = budgetToDelete.budgetID // Use the existing document ID

        // Remove the item from the list
        budgetList.removeAt(position)
        notifyItemRemoved(position)

        // Delete the corresponding document from Firestore
        db.collection("budgets").document(documentId.toString())
            .delete()
            .addOnSuccessListener {
                makeText(
                    holder.itemView.context,
                    "Budget deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                makeText(
                    holder.itemView.context,
                    "Error deleting budget: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    override fun getItemCount(): Int {
        return budgetList.size
    }

    private fun updateTargetAmt(
        holder: MyViewHolder,
        budget: Budget,
        expense: Expense
    ) {
        // Get the target amount from the budget
        var targetAmount = budget.targetAmount

        // Check if the expense category matches the budget category
        if (budget.category == expense.eCategory) {
            // Subtract the expense amount from the target amount
            targetAmount = targetAmount?.minus(expense.eNum)

            // Update the target amount in the budget
            budget.targetAmount = targetAmount

            // Update the target amount in Firestore
            budget.budgetID?.let {
                val db = FirebaseFirestore.getInstance()
                db.collection("budgets").document(it)
                    .update("targetAmount", budget.targetAmount)
                    .addOnSuccessListener {
                        // Update successful
                        Log.d("Expense", "Target amount updated successfully")

                        // Update the target amount TextView
                        holder.targetAmt.text = targetAmount.toString()
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Log.e("Expense", "Error updating target amount: ${e.message}")
                    }
            }
        }
    }


    fun calculateTargetAmt(holder: MyViewHolder, budget: Budget, expense: Expense) {
        // Get the total expense amount for the category
        val totalExpenseForCategory = calculateTotalExpenseForCategory(expenseList, budget)

        // Calculate the remaining amount
        val remainingAmount = budget.targetAmount?.minus(totalExpenseForCategory)
        if (remainingAmount != null) {
            previousExp = remainingAmount
        }
        // Update the target amount TextView
        holder.targetAmt.text = remainingAmount.toString()

        // Update the budget's targetAmount field
        budget.target = remainingAmount

//        val expenseNum = expense.eNum + previousExp
//        previousExp = expenseNum
//
//        val newExpenseAmt = budget.target?.minus(expenseNum)
//        budget.target = newExpenseAmt
//
//        // Update the target amount TextView
//        holder.targetAmt.text = newExpenseAmt.toString()
    }

    fun updatePercentage(holder: MyViewHolder, budget: Budget, expense: Expense) {
        val remainingAmount = budget.targetAmount?.minus(previousExp)
        Log.d("Expense", "Expense num: $remainingAmount")
        val percentage = calculatePercentage(budget.targetAmount, remainingAmount)
        updateProgressBar(holder, percentage)
    }


    private fun calculatePercentage(originalAmount: Double?, remainingAmount: Double?): Double {
        if (originalAmount == null || remainingAmount == null || originalAmount <= 0) {
            return 0.0
        }
        Log.d("Expense", "Remain num: $remainingAmount")
        val percentage = (remainingAmount / originalAmount) * 100
        return if (percentage < 0) 0.0 else percentage.coerceAtMost(100.0)
    }


    private fun updateProgressBar(holder: MyViewHolder, percentage: Double) {
        // Set the percentage to the TextView
        holder.itemView.findViewById<TextView>(R.id.budgetPercentage).text =
            String.format("%.2f%%", percentage)

        // Set up an ObjectAnimator to animate the progress changes
        val progressFront = holder.itemView.findViewById<ProgressBar>(R.id.budgetProgress)
        val progressBarAnimator =
            ObjectAnimator.ofInt(progressFront, "progress", 0, percentage.toInt())
        progressBarAnimator.duration = 2000 // Set the duration of the animation in milliseconds


        // Set the progress bar color based on the percentage condition
        if (percentage <= 99) {
            // Change the progress bar color to green
            progressFront.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
        } else {
            // Reset to the default color
            progressFront.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
        }


        // Start the animation
        progressBarAnimator.start()
    }

    // Function to automatically update the target amount and percentage after an expense is added
    fun updateBudgetAfterExpenseAdded(holder: MyViewHolder, newExpense: Expense) {
        // Find the corresponding budget for the expense
        val budget = budgetList.find { it.category == newExpense.eCategory }

        // Check if a corresponding budget is found
        if (budget != null) {
            // Update the target amount and percentage
            calculateTargetAmt(holder, budget, newExpense)
            updatePercentage(holder, budget, newExpense)
        }
    }

    // Function to set the data update flag
    private fun setDataUpdated(updated: Boolean) {
        isDataUpdated = updated
    }


    private fun EventChangeListener(userId: String) {

        // Example of real-time data retrieval in your Fragment/Activity
        db.collection("budgets")
            .whereEqualTo("userID", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle error
                    // Toast.makeText(this, "Error getting data: $exception", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                budgetList.clear() // Clear existing data

                for (document in snapshot!!) {
                    val budget = document.toObject(Budget::class.java)
                    budgetList.add(budget)
                }

                // Set the data update flag to true
                setDataUpdated(true)
            }

        // Notify the adapter after updating the data only if the data has been updated
        if (isDataUpdated) {
            android.os.Handler().postDelayed({
                notifyDataSetChanged()
                setDataUpdated(false)
            }, 100) // Delay in milliseconds
        }


    }

    private fun calculateTotalExpenseForCategory(
        expenseList: List<Expense>,
        budget: Budget
    ): Double {
        return expenseList
            .filter { it.eCategory == budget.category && it.userId == budget.userID }
            .sumByDouble { it.eNum }
    }

    // Add this function to handle SwipeRefreshLayout refresh
    fun refreshData() {
        // Check if SwipeRefreshLayout is not null
        swipeToRefreshLayout?.isRefreshing = false
        notifyDataSetChanged()
    }

    fun refreshApp() {
        swipeToRefreshLayout?.setOnRefreshListener {
            // Handle the refresh event
            refreshData()
        }
    }

    // Add this function to recalculate data after refreshing
    fun recalculateData(holder: MyViewHolder) {
        // Iterate through your budgetList and update the necessary data
        for (budget in budgetList) {
            val expense = expenseList.find { it.eCategory == budget.category }
            if (expense != null) {
                // Recalculate data based on budget and expense
                calculateTargetAmt(holder, budget, expense)
                updatePercentage(holder, budget, expense)
            }
        }

    }
}

