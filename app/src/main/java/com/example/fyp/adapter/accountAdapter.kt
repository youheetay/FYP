package com.example.fyp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.Account
import com.example.fyp.R

class accountAdapter (private val account : ArrayList<Account>): RecyclerView.Adapter<accountAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): accountAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_wallet,parent,false)
        return accountAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: accountAdapter.MyViewHolder, position: Int) {
        val account : Account = account[position]
        holder.accName.text = account.accName
        holder.accCardNumber.text = account.accCardNumber.toString()
        holder.accBalance.text = account.accCardAmount.toString()
    }

    override fun getItemCount(): Int {
        return account.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val accName : TextView = itemView.findViewById(R.id.accountName)
        val accCardNumber : TextView = itemView.findViewById(R.id.accountNum)
        val accBalance : TextView = itemView.findViewById(R.id.balance)
    }
}