//package com.example.fyp
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.firestore.FirebaseFirestore
//
//
//class budgetFragment : Fragment() {
//
//    private lateinit var budgetList : ArrayList<Budget>
//    private lateinit var expenseList : ArrayList<Expense>
//    private val db = FirebaseFirestore.getInstance()
//    private lateinit var budgeName : TextView
//    private lateinit var budgetRecyclerAdapter: BudgetRecyclerAdapter
//    private lateinit var recyclerView2 : RecyclerView
//    private lateinit var targetAmt : TextView
//    private lateinit var category : TextView
//    private var previousPercentage: Double = 0.0
//    private var previousTarget: Double = 0.0
//    private var previousExp: Double = 0.0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        val rootView = inflater.inflate(R.layout.budgetlist_item, container, false)
//        val secondView = inflater.inflate(R.layout.fragment_saving_plan,container,false)
//
//        budgetList = arrayListOf()
//        expenseList = arrayListOf()
//
//        targetAmt = rootView.findViewById(R.id.targetAmt)
//        category  = rootView.findViewById(R.id.Category)
//        budgetRecyclerAdapter = BudgetRecyclerAdapter(requireContext(), budgetList, requireContext(),expenseList)
//        recyclerView2 = secondView.findViewById(R.id.recyclerView)
//        recyclerView2.adapter = budgetRecyclerAdapter
//
//        if(expenseList)
//        calculateTargetAmt(rootView,expenseList,budgetList)
//
//
//
//        return rootView
//    }
//
//    private fun calculateTargetAmt(rootView: View, expenses: List<Expense>, budgets: List<Budget>) {
//        for (i in expenses.indices) {
//            calculateTargetAmt(rootView, expenses[i], budgets[i])
//        }
//    }
//
//    private fun calculateTargetAmt(view: View,expense: Expense, budget: Budget) {
//        val inflater = LayoutInflater.from(requireContext())
//        val v = inflater.inflate(R.layout.budgetlist_item, null)
//
//        val currentTargetAmt = budget.target
//
//        // Subtract the expense amount from the target amount
//        val newTargetAmt = (currentTargetAmt)?.minus((expense?.eNum!!))
//
//        val newTarget = newTargetAmt?.minus(previousTarget)
//        if (newTarget != null) {
//            previousTarget  = newTarget
//        }
//
//        budget.target = newTarget
//
//        val expenseNum = expense.eNum + previousExp
//        previousExp = expenseNum
//
//        val newExpenseAmt = budget.target?.minus(expenseNum)
//
//        // Update the target amount TextView
//        v.findViewById<TextView>(R.id.targetAmt).text = newTarget.toString()
//
////        val db = FirebaseFirestore.getInstance()
////        val updateBudget = budgetList[position]
////            val updatedData = mapOf(
////                "target" to newTarget
////            )
////
////
////            val documentId = updateBudget.budgetID // Use the existing document ID
////
////        // Update the target amount in Firestore
////            db.collection("budgets").document(documentId.toString())
////                .update(updatedData)
////                .addOnSuccessListener {
////
////                    // Calculate the percentage after updating the target amount
////                    val percentage = calculatePercentage(budget.targetAmount, expenseNum)
////
////                    // Calculate the sum of the previous percentage and the new percentage
////                    val newPercentage = previousPercentage + percentage
////
////                    // Store the current percentage for future use
////                    previousPercentage = newPercentage
////
////                    updateProgressBar(holder, newPercentage)
////
////                    // Update the target amount TextView
////                    holder.targetAmt.text = newTarget.toString()
////                    // Update successful
////                    Log.d("Expense", "Target amount updated successfully")
////
////                }
////                .addOnFailureListener { e ->
////                    // Handle failure
////                    Log.e("Expense", "Error updating target amount: ${e.message}")
////                }
//
//    }
//
//
//
//
//
//
//
//}