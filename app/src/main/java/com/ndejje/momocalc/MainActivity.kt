package com.ndejje.momocalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ndejje.momocalc.ui.theme.MomoCalculatorAppTheme
import org.w3c.dom.Text

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
fun BrkenInput() {
   var amount = ""

    TextField(
        value = amount,
        onValueChange = { amount = it },
        label = {
            Text(stringResource(R.string.enter_amount))
        }
    )
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MomoCalculatorAppTheme {
        BrkenInput()
    }
}