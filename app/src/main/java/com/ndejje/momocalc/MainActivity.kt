package com.ndejje.momocalc

import java.util.Locale
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ndejje.momocalc.MoMoAppTheme
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoMoAppTheme {         // ← replaces raw MaterialTheme(...)
                Surface(modifier = Modifier.fillMaxSize()) {
                    Scaffold(topBar = { MoMoTopBar() }) { innerPadding ->
                        MoMoCalcScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoMoTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.icn),
                contentDescription = "MoMo Logo",
                modifier = Modifier
                    .padding(start = dimensionResource(R.dimen.spacing_medium))
                    .height(32.dp)
                    .wrapContentWidth(),
                contentScale = ContentScale.Fit
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

enum class NetworkType {
    NONE, MTN, AIRTEL
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
    var selectedNetwork by rememberSaveable { mutableStateOf(NetworkType.NONE) }
    var isMaxWithdrawalMode by rememberSaveable { mutableStateOf(false) }
    
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val maxWithdrawalResult = if (isMaxWithdrawalMode && amount > 0) {
        calculateMaxWithdrawal(amount, selectedNetwork != NetworkType.NONE)
    } else null

    val baseFee = calculateWithdrawalFee(amount)
    val tax = if (amount > 0 && selectedNetwork != NetworkType.NONE) amount * 0.005 else 0.0
    val totalCharge = if (amount > 0) baseFee + tax else 0.0

    val currentThemeColor = when (selectedNetwork) {
        NetworkType.MTN -> Color(0xFFFFCB05) // MTN Yellow
        NetworkType.AIRTEL -> Color(0xFFED1C24) // Airtel Red
        else -> Color(0xFF2196F3) // Blue
    }

    val displayTextStyle = MaterialTheme.typography.bodyLarge
    val totalLabelStyle = MaterialTheme.typography.headlineMedium

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.screen_padding)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Network",
            style = MaterialTheme.typography.bodyLarge,
            color = currentThemeColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Network Selection
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // General Option
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { selectedNetwork = NetworkType.NONE }
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .then(
                            if (selectedNetwork == NetworkType.NONE) Modifier.border(
                                2.dp,
                                currentThemeColor,
                                RoundedCornerShape(8.dp)
                            )
                            else Modifier
                        )
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = "General",
                        modifier = Modifier.size(48.dp),
                        tint = if (selectedNetwork == NetworkType.NONE) currentThemeColor else MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "General (without tax)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selectedNetwork == NetworkType.NONE) Color(0xFF2196F3) else Color.Unspecified
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // MTN Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedNetwork = NetworkType.MTN }
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .then(
                                if (selectedNetwork == NetworkType.MTN) Modifier.border(
                                    2.dp,
                                    currentThemeColor,
                                    RoundedCornerShape(8.dp)
                                )
                                else Modifier
                            )
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mtn),
                            contentDescription = "MTN",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Text(
                        text = "MTN",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedNetwork == NetworkType.MTN) currentThemeColor else Color.Unspecified
                    )
                }

                // Airtel Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedNetwork = NetworkType.AIRTEL }
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .then(
                                if (selectedNetwork == NetworkType.AIRTEL) Modifier.border(
                                    2.dp,
                                    currentThemeColor,
                                    RoundedCornerShape(8.dp)
                                )
                                else Modifier
                            )
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.aitel),
                            contentDescription = "Airtel",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Text(
                        text = "Airtel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedNetwork == NetworkType.AIRTEL) currentThemeColor else Color.Unspecified
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Max Withdrawal Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Calculate Max Withdrawal from Balance",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = currentThemeColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = isMaxWithdrawalMode,
                onCheckedChange = { isMaxWithdrawalMode = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = currentThemeColor,
                    checkedTrackColor = currentThemeColor.copy(alpha = 0.5f)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        HoistedAmountInput(
            amount = amountInput,
            onAmountChange = { 
                amountInput = it
            },
            label = if (isMaxWithdrawalMode) "Enter Current Balance (UGX)" else stringResource(R.string.enter_amount),
            modifier = Modifier.fillMaxWidth(),
            activeColor = currentThemeColor
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (!isMaxWithdrawalMode) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Withdrawal Fee: UGX ${String.format(Locale.getDefault(), "%.0f", baseFee)}",
                    style = displayTextStyle
                )
                Text(
                    text = "Mobile Money Tax (0.5%): UGX ${String.format(Locale.getDefault(), "%.0f", tax)}",
                    style = displayTextStyle
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total Charge: UGX ${String.format(Locale.getDefault(), "%.0f", totalCharge)}",
                    style = totalLabelStyle,
                    color = currentThemeColor,
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
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text("You can withdraw: UGX ${String.format(Locale.getDefault(), "%.0f", result.withdrawalAmount)}", style = displayTextStyle, fontWeight = FontWeight.Bold)
                        Text("Withdrawal Fee: UGX ${String.format(Locale.getDefault(), "%.0f", result.fee)}", style = displayTextStyle)
                        Text("Mobile Money Tax: UGX ${String.format(Locale.getDefault(), "%.0f", result.tax)}", style = displayTextStyle)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total Deducted: UGX ${String.format(Locale.getDefault(), "%.0f", result.total)}",
                            style = displayTextStyle,
                            color = currentThemeColor
                        )
                    }
                }
            } ?: run {
                if (amountInput.isNotEmpty()) {
                    Text("Please enter a valid amount", color = MaterialTheme.colorScheme.error, style = displayTextStyle)
                }
            }
        }
    }
}

private fun calculateMaxWithdrawal(balance: Double, includeTax: Boolean): MaxWithdrawalInfo {
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

    val taxMultiplier = if (includeTax) 1.005 else 1.0

    for ((minAmount, fee) in brackets) {
        // Formula: Withdrawal + Fee + (Withdrawal * 0.005) <= Balance if includeTax
        // taxMultiplier * Withdrawal + Fee <= Balance
        // Withdrawal <= (Balance - Fee) / taxMultiplier
        val possibleWithdrawal = (balance - fee) / taxMultiplier
        if (possibleWithdrawal >= minAmount) {
            val tax = if (includeTax) possibleWithdrawal * 0.005 else 0.0
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
    isError: Boolean = false,
    label: String? = null,
    activeColor: Color = MaterialTheme.colorScheme.primary
) {
    TextField(
        value = amount,
        onValueChange = onAmountChange,
        isError = isError,
        label = {
            Text(label ?: stringResource(if (isError) R.string.error_numbers_only else R.string.enter_amount))
        },
        trailingIcon = {
            if (amount.isNotEmpty()) {
                IconButton(onClick = { onAmountChange("") }) {
                    Text(
                        text = "x",
                        color = activeColor,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = activeColor,
            focusedLabelColor = activeColor,
            cursorColor = activeColor
        )
    )
}



/* @Preview(showBackground = true)
@Composable
fun Preview() {
    MoMoAppTheme {
        InternalStateInput()
    }
} */

/* @Preview(showBackground = true, name = "Empty State")
@Composable
fun PreviewEmpty() {
    HoistedAmountInput(amount = "", onAmountChange = {})
} */

/* @Preview(showBackground = true, name = "Filled State")
@Composable
fun PreviewFilled() {
    HoistedAmountInput(amount = "50000", onAmountChange = {})
} */

 /* @Preview(showBackground = true, name = "Error State")
@Composable
fun PreviewError() {
    HoistedAmountInput(amount = "abc", onAmountChange = {}, isError = true)
} */
