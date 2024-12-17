package com.example.budgetapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BudgetList(viewModel: BudgetViewModel = viewModel()) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Load transactions when the composable is first launched
    LaunchedEffect(Unit) {
        viewModel.loadTransactions(context)
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Box(modifier = Modifier.background(Color.Black)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Content",
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    )
                    Text(
                        text = "$",
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(16.dp)
                    )
                    Text(
                        text = "Date",
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier
                            .weight(0.6f)
                            .padding(16.dp)
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(viewModel.transactions) { item ->
                    BudgetListTransaction(transaction = item)
                    HorizontalDivider(thickness = 2.dp)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(onClick = { showDialog = true }, modifier = Modifier.padding(16.dp)) {
                Text("+", fontSize = 30.sp)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        if (amount.isNotBlank() && description.isNotBlank() && amount.toDoubleOrNull() != null) {
                            viewModel.addTransaction(context, amount.toDouble(), description)
                            showDialog = false
                            amount = ""
                            description = ""
                        }
                    }) {
                        Text("Add")
                    }
                    Button(onClick = {
                        showDialog = false
                        amount = ""
                        description = ""
                    }) {
                        Text("Cancel")
                    }
                }
            },
            title = { Text("Add Transaction") },
            text = {
                Column {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("Description") }
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("Amount") }
                    )
                }
            }
        )
    }
}

@Composable
fun BudgetListTransaction(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = transaction.description,
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        )
        Text(
            text = "${transaction.amount}",
            modifier = Modifier
                .weight(0.5f)
                .padding(16.dp)
        )
        Text(
            text = transaction.date,
            modifier = Modifier
                .weight(0.6f)
                .padding(16.dp)
        )
    }
}

@Composable
fun MonthlySpendingBarChart(monthlyData: List<Pair<String, Double>>) {
    val maxSpending = monthlyData.maxOfOrNull { it.second } ?: 1.0

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Monthly Spending", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(monthlyData) { (month, total) ->
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(200.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Bar representing spending
                    Box(
                        modifier = Modifier
                            .height((200 * (total / maxSpending)).dp)
                            .width(40.dp)
                            .background(Color.Blue)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Month label
                    Text(text = month, style = MaterialTheme.typography.bodySmall)
                    // Spending label
                    Text(text = "$${"%.2f".format(total)}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

