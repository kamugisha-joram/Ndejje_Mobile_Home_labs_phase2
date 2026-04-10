package com.ndejje.momocalc

import java.util.Locale
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ndejje.momocalc.ui.theme.MoMoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by rememberSaveable { mutableStateOf(systemInDarkTheme) }

            MoMoAppTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        topBar = {
                            MoMoTopBar(
                                isDarkTheme = isDarkTheme,
                                onThemeToggle = { isDarkTheme = !isDarkTheme }
                            )
                        }
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
fun MoMoTopBar(isDarkTheme: Boolean, onThemeToggle: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.icn),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(stringResource(R.string.app_title))
            }
        },
        actions = {
            IconButton(onClick = onThemeToggle) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme"
                )
            }
        }
    )
}

enum class NetworkType {
    MTN, AIRTEL
}

data class MaxWithdrawalInfo(
    val withdrawalAmount: Double,
    val fee: Double,
    val tax: Double,
    val total: Double
) : java.io.Serializable

@Composable
fun MoMoCalcScreen(modifier: Modifier = Modifier) {
    var amountInput by rememberSaveable { mutableStateOf("") }
    var selectedNetwork by rememberSaveable { mutableStateOf(NetworkType.MTN) }
    var maxWithdrawalResult by rememberSaveable { mutableStateOf<MaxWithdrawalInfo?>(null) }
    
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val baseFee = calculateWithdrawalFee(amount)
    val tax = if (amount > 0) amount * 0.005 else 0.0
    val totalCharge = if (amount > 0) baseFee + tax else 0.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Network",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // MTN Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { selectedNetwork = NetworkType.MTN }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mtn),
                    contentDescription = "MTN",
                    modifier = Modifier.size(60.dp)
                )
                RadioButton(
                    selected = selectedNetwork == NetworkType.MTN,
                    onClick = { selectedNetwork = NetworkType.MTN }
                )
                Text("MTN")
            }

            // Airtel Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { selectedNetwork = NetworkType.AIRTEL }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.aitel),
                    contentDescription = "Airtel",
                    modifier = Modifier.size(60.dp)
                )
                RadioButton(
                    selected = selectedNetwork == NetworkType.AIRTEL,
                    onClick = { selectedNetwork = NetworkType.AIRTEL }
                )
                Text("Airtel")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        HoistedAmountInput(
            amount = amountInput,
            onAmountChange = { 
                amountInput = it
                maxWithdrawalResult = null // Reset result when input changes
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val balance = amountInput.toDoubleOrNull() ?: 0.0
                maxWithdrawalResult = calculateMaxWithdrawal(balance)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate Max Withdrawal from this Balance")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (maxWithdrawalResult == null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Withdrawal Fee: UGX ${String.format(Locale.getDefault(), "%.0f", baseFee)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Mobile Money Tax (0.5%): UGX ${String.format(Locale.getDefault(), "%.0f", tax)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total Charge: UGX ${String.format(Locale.getDefault(), "%.0f", totalCharge)}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            maxWithdrawalResult?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Maximum Withdrawal Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text("You can withdraw: UGX ${String.format(Locale.getDefault(), "%.0f", result.withdrawalAmount)}", fontWeight = FontWeight.Bold)
                        Text("Withdrawal Fee: UGX ${String.format(Locale.getDefault(), "%.0f", result.fee)}")
                        Text("Mobile Money Tax: UGX ${String.format(Locale.getDefault(), "%.0f", result.tax)}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total Deducted: UGX ${String.format(Locale.getDefault(), "%.0f", result.total)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

private fun calculateMaxWithdrawal(balance: Double): MaxWithdrawalInfo {
    // Fee brackets based on calculateWithdrawalFee logic
    val brackets = listOf(
        4000001.0 to 20000.0,
        2000001.0 to 18000.0,
        1000001.0 to 15000.0,
        500001.0 to 12500.0,
        250001.0 to 7000.0,
        125001.0 to 3575.0,
        60001.0 to 1925.0,
        45001.0 to 1500.0,
        30001.0 to 1000.0,
        15001.0 to 800.0,
        5001.0 to 700.0,
        2501.0 to 440.0,
        0.0 to 330.0
    )

    for ((minAmount, fee) in brackets) {
        // Formula: Withdrawal + Fee + (Withdrawal * 0.005) <= Balance
        // 1.005 * Withdrawal + Fee <= Balance
        // Withdrawal <= (Balance - Fee) / 1.005
        val possibleWithdrawal = (balance - fee) / 1.005
        if (possibleWithdrawal >= minAmount) {
            val tax = possibleWithdrawal * 0.005
            return MaxWithdrawalInfo(
                withdrawalAmount = possibleWithdrawal,
                fee = fee,
                tax = tax,
                total = possibleWithdrawal + fee + tax
            )
        }
    }
    return MaxWithdrawalInfo(0.0, 0.0, 0.0, 0.0)
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
    modifier: Modifier = Modifier,
    isError: Boolean = false
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
