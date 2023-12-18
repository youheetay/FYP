package com.example.fyp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.db.williamchart.view.LineChartView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ReportFragment : Fragment() {

    private lateinit var lineChart: LineChartView
    private lateinit var tvChartData: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_report, container, false)
        lineChart = rootView.findViewById(R.id.lineChart)
        tvChartData = rootView.findViewById(R.id.tvChartData)

        fetchDataFromFirestore()

        return rootView
    }

    // Inside ReportFragment class
    private fun fetchDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        db.collection("Expense")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                var newData = mutableListOf<Float>()

                for (document in querySnapshot.documents) {
                    val data = document.data
                    val expenseValue = data?.get("enum") as? Double
                    val expenseFloatValue = expenseValue?.toFloat()

                    Log.d("Firestore", "Expense value is null,  document ${document.id}")
                    if (expenseFloatValue != null) {
                        newData.add(expenseFloatValue)
                    } else {
                        Log.d("Firestore", "Expense value is null for document ${document.id}")
                    }
                }


                // Specify the maximum allowed Y value (adjust as needed)
                val maxAllowedYValue = 20F

                // Update the line chart with the fetched data
                updateLineChartData(newData, maxAllowedYValue)
            }
            .addOnFailureListener { error ->
                // Handle errors
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateLineChartData(newData: List<Float>, maxYValue: Float) {
        // Make lineSet mutable
        lineSet = newData.mapIndexed { index, value -> index.toString() to value }.toMutableList()
        // Notify the line chart to update with the new data
        updateLineChart(newData)
    }


    private fun updateLineChart(newData: List<Float>) {
        // Ensure the lineSet is cleared before updating
        lineSet.clear()

        newData.forEachIndexed { index, value ->
            // Ensure all data points are above the Y-axis
            val adjustedValue = if (value < 0) 0F else value
            lineSet.add(index.toString() to adjustedValue)
        }

        lineChart.apply {
            gradientFillColors = intArrayOf(
                Color.parseColor("#64B5F6"),
                Color.TRANSPARENT
            )
            animation.duration = animationDuration
            animate(lineSet)
            onDataPointTouchListener = { index, _, _ ->
                tvChartData.text = lineSet[index].second.toString()
            }
        }
    }




    companion object {
        // Make lineSet mutable
        var lineSet = mutableListOf<Pair<String, Float>>()
        private const val animationDuration = 1000L
    }
}