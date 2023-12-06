package com.example.fyp

data class Expense(
    var id : String? = null,
    var eName : String = "",
    var eNum : Double = 0.00,
    var eDate : String = "",
    var eCategory : String = "",
    var isButtonsLayoutVisible: Boolean = false
)
