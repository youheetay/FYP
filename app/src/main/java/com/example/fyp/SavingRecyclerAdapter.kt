package com.example.fyp

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import org.w3c.dom.Text


class SavingRecyclerAdapter (private val context: Context,
                             private val savingList: ArrayList<SavingPlan>,
                             private val parentContext: Context
): RecyclerView.Adapter<SavingRecyclerAdapter.MyViewHolder>() {




    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SavingRecyclerAdapter.MyViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.savinglist_item, parent, false)

        return MyViewHolder(itemView)
    }


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val savingName: TextView = itemView.findViewById(R.id.savingPlan)
        val targetAmt:TextView = itemView.findViewById(R.id.savingAmt)
        val savedAmt:TextView = itemView.findViewById(R.id.savedAmt)
        val editBtn: ImageButton = itemView.findViewById(R.id.editSavingButton)
    }

    override fun onBindViewHolder(holder: SavingRecyclerAdapter.MyViewHolder, position: Int) {
        val saving: SavingPlan = savingList[position]
        holder.savingName.text = saving.savingName
        holder.targetAmt.text = saving.targetAmount.toString()
        holder.savedAmt.text = saving.savedAmount.toString()

        // Calculate the percentage
        val targetAmount = saving.targetAmount ?: 1.0 // Using 1.0 as a default to avoid division by zero
        val percentage = (saving.savedAmount?.div(targetAmount))?.times(100)
        val progressFront = holder.itemView.findViewById<ProgressBar>(R.id.progressBar2)

        val percentageFront = percentage

        // Set up an ObjectAnimator to animate the progress changes
        val progressBarAnimator = ObjectAnimator.ofInt(progressFront, "progress", 0, percentageFront?.toInt() ?: 0)
        progressBarAnimator.duration = 2000 // Set the duration of the animation in milliseconds



        // Start the animation
        progressBarAnimator.start()

        // Set the percentage to the TextView
        holder.itemView.findViewById<TextView>(R.id.savingPercent).text = String.format("%d%%", percentage?.toInt() ?: 0)

        // Set the progress bar color based on the percentage condition
        if (percentage != null && percentage <= 99) {
            // Change the progress bar color to green
            progressFront.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
        } else {
            // Reset to the default color
            progressFront.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
        }

        holder.targetAmt.visibility = View.GONE
        holder.editBtn.setOnClickListener {

            val positionUpdate = holder.adapterPosition
            val updateSaving = savingList[positionUpdate]

            val dialogView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.activity_savedamount, null)
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            val alertDialog = alertDialogBuilder.show()

            val progress = dialogView.findViewById<CircularProgressBar>(R.id.circularProgressBar)

            val amountEditText = dialogView.findViewById<TextView>(R.id.savedAmountDetail)

            amountEditText.setText(updateSaving.savedAmount.toString())


            alertDialogBuilder.setView(dialogView)
            val dialog = alertDialogBuilder.create() // Get the AlertDialog instance

            // Calculate and set the percentage
            val targetAmount = updateSaving.targetAmount
            val savedAmount = updateSaving.savedAmount

            if (targetAmount != null && savedAmount != null) {
                val percentage = (savedAmount.div(targetAmount)) * 100

                // Update the TextView with the percentage
//                dialogView.findViewById<TextView>(R.id.percentageTextView).text =
//                    String.format("%.2f%%", percentage)

                // Update the CircularProgressBar
                progress.setProgressWithAnimation(percentage.toFloat())
                holder.itemView.findViewById<TextView>(R.id.savingPercent).text = String.format("%d%%", percentage?.toInt() ?: 0)
                // Set the progress bar color based on the percentage condition
                if (percentage != null && percentage <= 99) {
                    // Change the progress bar color to green
                    progressFront.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
                } else {
                    // Reset to the default color
                    progressFront.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
                }
            }


            dialogView.findViewById<ImageButton>(R.id.backBtn).setOnClickListener {
                //dismiss dialog
                dialog.dismiss()

            }

            dialogView.findViewById<Button>(R.id.addAmtBtn).setOnClickListener{



                val dialogView = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.addsaveddialog, null)
                val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
                val alertDialog = alertDialogBuilder.show()


                val savedAmtEditText = dialogView.findViewById<EditText>(R.id.editSavedAmt)
                val confirmBtn = dialogView.findViewById<Button>(R.id.insertBtn)
                val backBtn = dialogView.findViewById<ImageButton>(R.id.backBtn2)

                alertDialogBuilder.setView(dialogView)
                val dialog = alertDialogBuilder.create() // Get the AlertDialog instance

                backBtn.setOnClickListener {
                    dialog.dismiss()
                }

                confirmBtn.setOnClickListener {
                    // Dismiss the second dialog
                    dialog.dismiss()

                    // Handle the addition here
                    val enteredAmount = savedAmtEditText.text.toString().toDoubleOrNull() ?: 0.0
                    // Perform the addition logic as per your requirements
                    // Get the current saved amount from the data model
                    val currentSavedAmount = updateSaving.savedAmount

                    // Add the entered amount to the current saved amount
                    val newSavedAmount = currentSavedAmount?.plus(enteredAmount)

                    // Update the UI to display the new saved amount (optional)
                    amountEditText.text = newSavedAmount.toString()

                    // Update the data model with the new saved amount (if needed)
                    updateSaving.savedAmount = newSavedAmount

                    // Assuming your target amount is also a Double
                    val targetAmount = saving.targetAmount ?: 1.0  // Using 1.0 as a default to avoid division by zero

//                     Calculate the progress value as a percentage
                    val progressValue = ((newSavedAmount?.div(targetAmount))?.times(100))?.toFloat()
                    if (progressValue != null) {
                        val progressV = progressValue
                        progress.setProgressWithAnimation(progressV)
                        // Set the progress bar color based on the percentage condition
                        if (percentage != null && percentage <= 99) {
                            // Change the progress bar color to green
                            progressFront.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
                        } else {
                            // Reset to the default color
                            progressFront.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
                        }
                    }


                    val db = FirebaseFirestore.getInstance()
                    val updateSaving = savingList[position]
                    val updatedData = mapOf(
                        "savedAmount" to newSavedAmount
                    )

                    val documentId = updateSaving.savingID // Use the existing document ID

                    db.collection("savings").document(documentId.toString())
                        .update(updatedData)
                        .addOnSuccessListener {
                            savingList[position].savedAmount = newSavedAmount
                            notifyItemChanged(position)

                            Toast.makeText(
                                holder.itemView.context,
                                "Update Success",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                holder.itemView.context,
                                "Error updating saving plan: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                }
                dialog.show()


            }

            dialogView.findViewById<Button>(R.id.setReachedBtn).setOnClickListener{
                dialog.dismiss()

                deleteSaving(holder,position)
            }

            dialogView.findViewById<ImageButton>(R.id.editSavingBtn).setOnClickListener {
                //dismiss dialog
                dialog.dismiss()

                val positionUpdate = holder.adapterPosition
                val updateSaving = savingList[positionUpdate]

                val dialogView = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.fragment_saving, null)
                val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
                val alertDialog = alertDialogBuilder.show()

                val nameEditText = dialogView.findViewById<EditText>(R.id.editSavingName)
                val targetEditText = dialogView.findViewById<EditText>(R.id.editTargetAmount)
                val savedEditText = dialogView.findViewById<EditText>(R.id.editSavedAmount)

                nameEditText.setText(updateSaving.savingName)
                targetEditText.setText(updateSaving.targetAmount.toString())
                savedEditText.setText(updateSaving.savedAmount.toString())

                alertDialogBuilder.setView(dialogView)
                val dialog = alertDialogBuilder.create() // Get the AlertDialog instance

                dialogView.findViewById<ImageButton>(R.id.saveSavingBtn).setOnClickListener {
                    //dismiss dialog
                    dialog.dismiss()

                    val newName = nameEditText.text.toString()
                    val newTarget = targetEditText.text.toString().toDoubleOrNull() ?: 0.0
                    val newSaved = savedEditText.text.toString().toDoubleOrNull() ?: 0.0
                    val db = FirebaseFirestore.getInstance()

                    updateSavingDetails(holder, position, newName, newTarget, newSaved)

                }

                dialogView.findViewById<ImageButton>(R.id.cancelBtn).setOnClickListener {
                    //dismiss dialog
                    dialog.dismiss()

                }

                dialogView.findViewById<ImageButton>(R.id.dltSavingBtn).setOnClickListener {
                    //dismiss dialog
                    dialog.dismiss()

                   // Perform the delete operation
                    deleteSaving(holder, position)

                }


                dialog.show()
//            alertDialogBuilder.show()



            }

            dialog.show()
    }
}
    override fun getItemCount(): Int {
        return savingList.size
    }

    private fun deleteSaving(holder: MyViewHolder, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val savingToDelete = savingList[position]
        val documentId = savingToDelete.savingID // Assuming savingID is the correct field for the document ID

        // Remove the item from the list
        savingList.removeAt(position)
        notifyItemRemoved(position)

        Log.d("DocumentId", documentId.toString())

        // Delete the corresponding document from Firestore
        db.collection("savings").document(documentId.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                    holder.itemView.context,
                    "Saving deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    holder.itemView.context,
                    "Error deleting saving: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateSavingDetails(
        holder: SavingRecyclerAdapter.MyViewHolder,
        position: Int,
        newName: String,
        newAmount: Double,
        newSaved:Double
    ) {

        val db = FirebaseFirestore.getInstance()
        val updateSaving = savingList[position]
        val updatedData = mapOf(
            "savingName" to newName,
            "targetAmount" to newAmount,
            "savedAmount" to newSaved
        )

        val documentId = updateSaving.savingID // Use the existing document ID

        db.collection("savings").document(documentId.toString())
            .update(updatedData)
            .addOnSuccessListener {
                savingList[position].savingName = newName
                savingList[position].targetAmount = newAmount
                savingList[position].savedAmount = newSaved
                notifyItemChanged(position)
                Toast.makeText(
                    holder.itemView.context,
                    "Update Success",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    holder.itemView.context,
                    "Error updating saving plan: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }



}
