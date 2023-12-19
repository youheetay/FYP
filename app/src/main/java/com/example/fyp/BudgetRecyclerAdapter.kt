package com.example.fyp

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BudgetRecyclerAdapter(
    private val context: Context,
    private val budgetList : ArrayList<Budget>,
    private val parentContext: Context,
    private val expenseList : ArrayList<Expense>
) : RecyclerView.Adapter<BudgetRecyclerAdapter.MyViewHolder>() {
    private var isLogicExecuted = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BudgetRecyclerAdapter.MyViewHolder {


        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.budgetlist_item, parent, false)



        return MyViewHolder(itemView)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val budgetName: TextView = itemView.findViewById(R.id.budgetName)
        val period: TextView = itemView.findViewById(R.id.Period)
        val targetAmt: TextView = itemView.findViewById(R.id.targetAmt)
        val editBtn:ImageButton = itemView.findViewById(R.id.editButton)
    }

    override fun onBindViewHolder(holder: BudgetRecyclerAdapter.MyViewHolder, position: Int) {
        val budget: Budget = budgetList[position]
        holder.budgetName.text = budget.budgetName
        holder.period.text = budget.category
        holder.targetAmt.text = budget.targetAmount.toString()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid


        Log.d("Expense", "Expense List Size: ${expenseList.size}")
        Log.d("Expense", "Budget List Size: ${budgetList.size}")

        // Find the corresponding expense for the current budget
        val expense = expenseList.find { it.userId == userId && it.eCategory == budget.category }
        Log.d("Expense", "Expense List Size: $expense")

//        setupExpenseListener()
        // Assuming you have a list of all expenses
        if (expense != null || !isLogicExecuted) {
            Log.d("Expense", "Checking expense: ${expense?.eCategory}, ${expense?.userId}")
            // Subtract the expense amount from the target amount
            val currentTargetAmt = budget.targetAmount

            val newTargetAmt = (currentTargetAmt)?.minus((expense?.eNum!!))
            // Update the target amount TextView
            holder.targetAmt.text = newTargetAmt.toString()
            budget.targetAmount = newTargetAmt

            val db = FirebaseFirestore.getInstance()
            val updateBudget = budgetList[position]
            val updatedData = mapOf(
                "targetAmount" to newTargetAmt
            )
            isLogicExecuted = true

            val documentId = updateBudget.budgetID // Use the existing document ID

            // Update the target amount in Firestore
            db.collection("budgets").document(documentId.toString())
                .update(updatedData)
                .addOnSuccessListener {
                    // Update successful
                    Log.d("Expense", "Target amount updated successfully")

                    // Update the target amount TextView
                    holder.targetAmt.text = budget.targetAmount.toString()
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    Log.e("Expense", "Error updating target amount: ${e.message}")
                }


        }

        val percentage = budget.targetAmount?.let { expense?.eNum?.div(it)?.times(100) }
        val progressFront = holder.itemView.findViewById<ProgressBar>(R.id.budgetProgress)
        val percentageFront = percentage

        // Set up an ObjectAnimator to animate the progress changes
        val progressBarAnimator = ObjectAnimator.ofInt(progressFront, "progress", 0, percentageFront?.toInt() ?: 0)
        progressBarAnimator.duration = 2000 // Set the duration of the animation in milliseconds

        // Define colors for different progress ranges
        val greenColor = Color.GREEN
        val yellowColor = Color.YELLOW
        val redColor = Color.RED

        // Calculate the color based on the progress percentage
        val color = when {
//            percentageFront != null && percentageFront >= 50 -> greenColor
            percentageFront != null && percentageFront >= 100 -> redColor
            else -> greenColor
        }

        // Set the color filter to the ProgressBar
        progressFront.indeterminateDrawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)

        // Start the animation
        progressBarAnimator.start()



//        updateTargetAmt(holder,budget, expense)

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
            val periodAdapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, periodArray)
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

//                alertDialogBuilder.setPositiveButton("Update") { _, _ ->
                    val newName = nameEditText.text.toString()
                    val newCat = periodEditText.selectedItem.toString()
                    val newAmount = amountEditText.text.toString().toDoubleOrNull() ?: 0.0
                    val db = FirebaseFirestore.getInstance()

                    updateBudgetDetails(holder, position, newName, newCat, newAmount)

//                }
//                alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
//                    dialog.dismiss()
//                }
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
        holder: BudgetRecyclerAdapter.MyViewHolder,
        position: Int,
        newName: String?,
        newCat: String?,
        newAmount: Double?,
    ) {
        Log.d("BudgetAdapter", "updateBudgetDetails: position=$position, budgetList.size=${budgetList.size}")
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
                Log.d("BudgetAdapter", "updateBudgetDetails: position=$position, budgetList.size=${budgetList.size}")
                // Update the local list first
                budgetList[position].budgetName = newName
                budgetList[position].category = newCat
                budgetList[position].targetAmount = newAmount

                // Check if the list is not empty before notifying item change
                if (budgetList.isNotEmpty()) {
                    // Call notifyItemChanged after updating the local list
                    notifyItemChanged(position)
                }

                Toast.makeText(
                    holder.itemView.context,
                    "Update Success",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
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
                Toast.makeText(
                    holder.itemView.context,
                    "Budget deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
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
        holder: BudgetRecyclerAdapter.MyViewHolder,
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


    // Assuming you have a listener setup method
//    private fun setupExpenseListener() {
//
//        val currentUser = FirebaseAuth.getInstance().currentUser
//        val userId = currentUser?.uid
//        val db = FirebaseFirestore.getInstance()
//
//        // Add a real-time listener to the "Expense" collection
//        db.collection("Expense")
//            .whereEqualTo("userId", userId)
//            .addSnapshotListener { snapshot, e ->
//                if (e != null) {
//                    Log.e("Expense", "Listen failed.", e)
//                    return@addSnapshotListener
//                }
//
//                if (snapshot != null && !snapshot.isEmpty) {
//                    // Iterate through the changed documents
//                    for (documentChange in snapshot.documentChanges) {
//                        when (documentChange.type) {
//                            DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
//                                val changedExpense = documentChange.document.toObject(Expense::class.java)
//
//                                // Find the corresponding budget for the changed expense
//                                val changedBudget = budgetList.find { it.category == changedExpense.eCategory }
//
//                                if (changedBudget != null) {
//                                    // Subtract the expense amount from the target amount
//                                    val currentTargetAmt = changedBudget.targetAmount
//                                    val newTargetAmt = currentTargetAmt?.minus(changedExpense.eNum)
//
//                                    // Update the target amount locally
//                                    changedBudget.targetAmount = newTargetAmt
//
//                                    // Update the target amount in Firestore
//                                    changedBudget.budgetID?.let {
//                                        db.collection("budgets").document(it)
//                                            .update("targetAmount", newTargetAmt)
//                                            .addOnSuccessListener {
//                                                // Update successful
//                                                Log.d("Expense", "Target amount updated successfully")
//                                                // Notify your RecyclerView adapter about the data change
//                                                BudgetRecyclerAdapter.notifyDataSetChanged()
//                                            }
//                                            .addOnFailureListener { updateError ->
//                                                // Handle failure
//                                                Log.e("Expense", "Error updating target amount: ${updateError.message}")
//                                            }
//                                    }
//                                }
//                            }
//                            DocumentChange.Type.REMOVED -> {
//                                // Handle removal if necessary
//                            }
//                        }
//                    }
//                }
//            }
//    }

}
