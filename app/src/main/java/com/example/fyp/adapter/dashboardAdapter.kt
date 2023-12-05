package com.example.fyp.adapter

import android.app.AlertDialog
import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.Expense
import com.example.fyp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class dashboardAdapter(
    private val expenseList: ArrayList<Expense>,
    private val onCardClickListener: (position: Int) -> Unit,
    private val onEditClickListener: (position: Int) -> Unit
) :
    RecyclerView.Adapter<dashboardAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_dashboard, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val expense: Expense = expenseList[position]
        holder.eName.text = expense.eName
        holder.eNum.text = expense.eNum.toString()
        holder.eDate.text = expense.eDate
        holder.eCategory.text = expense.eCategory

        holder.buttonsLayout.visibility = if (expense.isButtonsLayoutVisible) View.VISIBLE else View.GONE

        // Add click listener to the whole card
        holder.itemView.setOnClickListener {
            onCardClickListener.invoke(position)
        }

        holder.buttonEdit.setOnClickListener {
            onEditClickListener.invoke(position)
        }

        holder.buttonDelete.setOnClickListener {
            val clickedExpense = expenseList[position]

            // Show a confirmation dialog
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Delete Expense")
            alertDialogBuilder.setMessage("Confirm To Delete?")
            alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
                // Remove the notification from the list locally
                expenseList.removeAt(position)
                notifyItemRemoved(position)

                // Delete the notification from Firestore
                val db = FirebaseFirestore.getInstance()

                db.collection("Expense")
                    .document(clickedExpense.id ?: "")  // Use 'id' instead of 'documentId'
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            holder.itemView.context,
                            "You have deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("dashboardAdapter", "Expense deleted successfully")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("dashboardAdapter", "Error deleting Expense Record from Firestore: $exception")
                    }

            }

            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialogBuilder.show()
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eName: TextView = itemView.findViewById(R.id.expenseName)
        val eNum: TextView = itemView.findViewById(R.id.expenseRM)
        val eDate: TextView = itemView.findViewById(R.id.expenseDate)
        val eCategory : TextView = itemView.findViewById(R.id.category)
        val buttonsLayout: LinearLayout = itemView.findViewById(R.id.buttonsLayout)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

        init {
            buttonsLayout.visibility = View.GONE
        }
    }
}