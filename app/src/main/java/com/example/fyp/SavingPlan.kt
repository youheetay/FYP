package com.example.fyp

data class SavingPlan(
    val savingID: String?= "",
    var savingName: String? = null,
    var targetAmount: Double? = null,
    var savedAmount:Double?= null,
    val userID:String ?= null
)
