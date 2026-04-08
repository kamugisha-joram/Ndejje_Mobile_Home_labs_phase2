package com.ndejje.momocalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ndejje.momocalc.ui.theme.MoMoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoMoAppTheme {                        // our custom theme (Part B)
                Surface(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        topBar = { MoMoTopBar() }
                    ) { innerPadding ->
                        MoMoCalcScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoMoTopBar() {
    TopAppBar(
        title = { Text(stringResource(R.string.app_title)) }
    )
}

@Composable
fun MoMoCalcScreen(modifier: Modifier = Modifier) {
    var amountInput by remember { mutableStateOf("") }
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val fee = calculateWithdrawalFee(amount)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HoistedAmountInput(
            amount = amountInput,
            onAmountChange = { amountInput = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.fee_label, fee.toString()),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

private fun calculateWithdrawalFee(amount: Double): Double {
    return when {
        amount <= 0 -> 0.0
        amount <= 2500 -> 330.0
        amount <= 5000 -> 440.0
        amount <= 15000 -> 700.0
        amount <= 30000 -> 800.0
        amount <= 45000 -> 1000.0
        amount <= 60000 -> 1500.0
        amount <= 125000 -> 1925.0
        amount <= 250000 -> 3575.0
        amount <= 500000 -> 7000.0
        amount <= 1000000 -> 12500.0
        amount <= 2000000 -> 15000.0
        amount <= 4000000 -> 18000.0
        else -> 20000.0
    }
}

@Composable
fun InternalStateInput() {
    var amount by remember { mutableStateOf("") }

    TextField(
        value = amount,
        onValueChange = { amount = it },
        label = {
            Text(stringResource(R.string.enter_amount))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun HoistedAmountInput(
    amount: String,
    onAmountChange: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    TextField(
        value = amount,
        onValueChange = onAmountChange,
        isError = isError,
        label = {
            Text(stringResource(if (isError) R.string.error_numbers_only else R.string.enter_amount))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}



@Preview(showBackground = true)
@Composable
fun Preview() {
    MoMoAppTheme {
        InternalStateInput()
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun PreviewEmpty() {
    HoistedAmountInput(amount = "", onAmountChange = {})
}

@Preview(showBackground = true, name = "Filled State")
@Composable
fun PreviewFilled() {
    HoistedAmountInput(amount = "50000", onAmountChange = {})
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun PreviewError() {
    HoistedAmountInput(amount = "abc", onAmountChange = {}, isError = true)
}

