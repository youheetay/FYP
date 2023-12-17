package com.example.fyp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.Account
import com.example.fyp.R

class accountAdapter(private val account: ArrayList<Account>, private val buttonClickListener: OnButtonClickListener) :
    RecyclerView.Adapter<accountAdapter.MyViewHolder>() {
    interface OnButtonClickListener {
        fun onEditButtonClick(position: Int)
        fun onDeleteButtonClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): accountAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_wallet,parent,false)
        return accountAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: accountAdapter.MyViewHolder, position: Int) {
        val account : Account = account[position]
        holder.accName.text = account.accName
        holder.accCardNumber.text = account.accCardNumber.toString()
        holder.accBalance.text = String.format("%.2f", account.accCardAmount)

        var buttonsVisible = false // Initially, buttons are not visible

        holder.itemView.setOnClickListener {
            // Toggle the visibility of Edit and Delete buttons
            buttonsVisible = !buttonsVisible
            holder.buttonsLayoutAccount.visibility = if (buttonsVisible) View.VISIBLE else View.GONE
            //holder.deleteButton.visibility = if (buttonsVisible) View.VISIBLE else View.GONE
        }

        holder.editButton.setOnClickListener {
            buttonClickListener.onEditButtonClick(position)
        }

        holder.deleteButton.setOnClickListener {
            buttonClickListener.onDeleteButtonClick(position)
        }
    }

    override fun getItemCount(): Int {
        return account.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val accName : TextView = itemView.findViewById(R.id.accountName)
        val accCardNumber : TextView = itemView.findViewById(R.id.accountNum)
        val accBalance : TextView = itemView.findViewById(R.id.balance)
        val editButton: Button = itemView.findViewById(R.id.buttonEditAccount)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteAccount)
        val buttonsLayoutAccount: LinearLayout = itemView.findViewById(R.id.buttonsLayoutAccount)
    }
}