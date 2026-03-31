package com.ndejje.momocalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ndejje.momocalc.ui.theme.MomoCalculatorAppTheme
import org.w3c.dom.Text
import java.time.temporal.TemporalAmount

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MomoCalculatorAppTheme {

            }
        }
    }
}


@Composable
fun InternalStateInput() {
   var amount = ""

    TextField(
        value = amount,
        onValueChange = { amount = it },
        label = {
            Text(stringResource(R.string.enter_amount))
        }
    )
}

@Composable
fun HoistedInputAmountInput(
    amount: String,
    onAmountChange: (String) -> Unit,
    isError: Boolean = false
){
    TextField(
        value = amount,
        onValueChange = onAmountChange,
        isError = isError,
        label = {
            Text(stringResource(R.string.error_numbers_only))


            // to continue from here


        }
    )
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MomoCalculatorAppTheme {
        InternalStateInput()
    }
}