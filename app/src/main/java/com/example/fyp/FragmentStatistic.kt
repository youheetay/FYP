package com.example.fyp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore


class FragmentStatistic : Fragment() {

    private lateinit var viewPagerHome : ViewPager2
    private lateinit var statisticViewPagerAdapter : statisticViewPagerAdapter
    private lateinit var totalExp : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_statistic, container, false)
        viewPagerHome = rootView.findViewById(R.id.view_pager2)
        statisticViewPagerAdapter = statisticViewPagerAdapter(requireActivity())
        viewPagerHome.adapter = statisticViewPagerAdapter

        viewPagerHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })

        totalExp = rootView.findViewById(R.id.totalExp)

        fetchDataFromFirestore()

        return rootView
    }

    private fun fetchDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()

        db.collection("Expense")
            .get()
            .addOnSuccessListener { querySnapshot ->
                var totalExpense = 0.0

                for (document in querySnapshot.documents) {
                    val data = document.data
                    val expenseValue = data?.get("enum") as? Double

                    if (expenseValue != null) {
                        totalExpense += expenseValue
                    } else {
                        Log.d("Firestore", "Expense value is null for document ${document.id}")
                    }
                }

                // Convert the totalExpense to Float if needed
                val totalExpenseFloat = totalExpense.toFloat()

                // Set the text of totalExp TextView
                totalExp.text = totalExpenseFloat.toString()
            }
            .addOnFailureListener { error ->
                // Handle errors
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
