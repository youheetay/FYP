package com.example.fyp

import android.app.AlertDialog
import android.content.Context
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
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BudgetRecyclerAdapter(
    private val context: Context,
    private val budgetList : ArrayList<Budget>,
    private val parentContext: Context,
    private val expenseList : ArrayList<Expense>
) : RecyclerView.Adapter<BudgetRecyclerAdapter.MyViewHolder>() {


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
        holder.period.text = budget.period
        holder.targetAmt.text = budget.targetAmount.toString()
        val expense: Expense = expenseList[position]
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



            // Set the selected item of the spinner to the value from updateBudget
            val periodArray = holder.itemView.resources.getStringArray(R.array.budgetPeriod)
            val periodAdapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, periodArray)
            periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            periodEditText.adapter = periodAdapter

            val periodPosition = periodArray.indexOf(updateBudget.period)

            nameEditText.setText(updateBudget.budgetName)
            periodEditText.setSelection(periodPosition)
            amountEditText.setText(updateBudget.targetAmount.toString())

            alertDialogBuilder.setView(dialogView)
            val dialog = alertDialogBuilder.create() // Get the AlertDialog instance

            dialogView.findViewById<ImageButton>(R.id.saveEditBtn).setOnClickListener {
                //dismiss dialog
                dialog.dismiss()


                    val newName = nameEditText.text.toString()
                    val newPeriod = periodEditText.selectedItem.toString()
                    val newAmount = amountEditText.text.toString().toDoubleOrNull() ?: 0.0
                    val db = FirebaseFirestore.getInstance()

                    updateBudgetDetails(holder, position, newName, newPeriod, newAmount)

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
        newPeriod: String?,
        newAmount: Double?,
    ) {
        Log.d("BudgetAdapter", "updateBudgetDetails: position=$position, budgetList.size=${budgetList.size}")
        val db = FirebaseFirestore.getInstance()

        val updateBudget = budgetList[position]
        val updatedData = mapOf(
            "budgetName" to newName,
            "period" to newPeriod,
            "targetAmount" to newAmount
        )
        val documentId = updateBudget.budgetID // Use the existing document ID

        db.collection("budgets").document(documentId.toString())
            .update(updatedData)
            .addOnSuccessListener {
                Log.d("BudgetAdapter", "updateBudgetDetails: position=$position, budgetList.size=${budgetList.size}")
                // Update the local list first
                budgetList[position].budgetName = newName
                budgetList[position].period = newPeriod
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
        val targetAmount = budget.targetAmount

        // Subtract the expense amount from the target amount
        val updatedTargetAmount = targetAmount?.minus(expense.eNum)

        // Check if the updated target amount is not null and greater than or equal to zero
        if (updatedTargetAmount != null && updatedTargetAmount >= 0) {
            // Update the target amount in the budget
            budget.targetAmount = updatedTargetAmount

            // Update the target amount in Firestore
            budget.budgetID?.let {
                val db = FirebaseFirestore.getInstance()
                db.collection("budgets").document(it)
                    .update("targetAmount", budget.targetAmount)
                    .addOnSuccessListener {
                        // Update successful
                        Log.d("Expense", "Target amount updated successfully")

                        // Update the target amount TextView
                        holder.targetAmt.text = updatedTargetAmount.toString()
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Log.e("Expense", "Error updating target amount: ${e.message}")
                    }
            }
        } else {
            // Handle the case where the updated target amount is negative or null
            Log.e("Expense", "Invalid updated target amount: $updatedTargetAmount")
        }
    }


}
