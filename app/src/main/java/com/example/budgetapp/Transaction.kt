package com.example.budgetapp

data class Transaction (
    val id : Int,
    val amount: Double,
    val description: String,
    val date: String)