package com.example.fyp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class FragmentSavingPlan : Fragment() {

    private lateinit var budgetBtn : Button
    private lateinit var savingBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =  inflater.inflate(R.layout.fragment_saving_plan, container, false)

        budgetBtn = rootView.findViewById(R.id.budgetBtn)
        savingBtn = rootView.findViewById(R.id.savingBtn)

        budgetBtn.setOnClickListener {

        }

        savingBtn.setOnClickListener {

        }

        return rootView
    }


}