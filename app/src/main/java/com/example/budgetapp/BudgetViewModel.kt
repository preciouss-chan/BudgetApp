package com.example.budgetapp

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BudgetViewModel :ViewModel(){
    var transactions by mutableStateOf<List<Transaction>>(emptyList())
        private set

    fun loadTransactions(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                transactions = SharedPreferencesHelper.getTransactions(context)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTransaction(context: Context, amount: Double, description: String) {
        viewModelScope.launch {
            val newTransaction = Transaction(
                id = transactions.size + 1,
                amount = amount,
                description = description,
                date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
            )
            transactions = transactions + newTransaction
            withContext(Dispatchers.IO) {
                SharedPreferencesHelper.saveTransactions(context, transactions)
            }
        }
    }

    fun reloadTransactions(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                transactions = SharedPreferencesHelper.getTransactions(context)
            }
        }
    }

}