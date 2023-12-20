package com.example.fyp

data class Budget(
    var budgetID: String?= "",
    var budgetName: String? = null,
    var category: String? = null,
    var targetAmount: Double? = null,
    var target : Double ?= null,
    val userID: String ?= null
)