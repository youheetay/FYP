package com.example.fyp

data class Account(
    var id: String = "",
    var accName : String = "",
    var accCardNumber : Long = 0,
    var accCardDate : Int = 0,
    var accCardCode : Int = 0,
    var accCardAmount : Double = 0.0,
    var userId : String? = null
)