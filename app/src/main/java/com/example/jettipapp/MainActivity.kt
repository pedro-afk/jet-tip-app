package com.example.jettipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.util.calculateTotalPerPerson
import com.example.jettipapp.util.calculateTotalTip
import com.example.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            //modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 134.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(150.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        //.clip(shape = RoundedCornerShape(corner = CornerSize(12.dp)))
        color = Color(0xFFE9D7F7),

    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$$total", style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MainContent() {
    BillForm () {}
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValChange: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("0")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val splitByState = remember {
        mutableStateOf(1)
    }
    val range = IntRange(start = 1, endInclusive = 100)
    val tipPercentage = (sliderPositionState.value * 100).toInt()
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    Column() {
        TopHeader(totalPerPerson = totalPerPersonState.value)
        Surface(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillState.value.trim())
                        keyboardController?.hide()
                    }
                )
                if (validState) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Split",
                            modifier = Modifier.align(
                                alignment = Alignment.CenterVertically
                            )
                        )
                        Spacer(modifier = Modifier.width(120.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            RoundIconButton(
                                imageVector = Icons.Default.Remove,
                                onClick = {
                                    splitByState.value =
                                        if (splitByState.value > 1) splitByState.value - 1 else 1
                                    totalPerPersonState.value = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        tipPercentage = tipPercentage,
                                        splitBy = splitByState.value,
                                    )
                                }
                            )
                            Text(
                                text = splitByState.value.toString(),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 9.dp, end = 9.dp),
                                fontWeight = FontWeight.Bold,
                            )
                            RoundIconButton(
                                imageVector = Icons.Default.Add,
                                onClick = {
                                    if (splitByState.value < range.last) {
                                        splitByState.value++
                                        totalPerPersonState.value = calculateTotalPerPerson(
                                            totalBill = totalBillState.value.toDouble(),
                                            tipPercentage = tipPercentage,
                                            splitBy = splitByState.value,
                                        )
                                    }
                                }
                            )
                        }
                    }
                    //Tip Row
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Tip",
                            modifier = Modifier.align(
                                alignment = Alignment.CenterVertically
                            )
                        )
                        Spacer(modifier = Modifier.width(200.dp))
                        Text(
                            text = "$${"%.2f".format(tipAmountState.value)}",
                            modifier = Modifier.align(
                                alignment = Alignment.CenterVertically
                            ),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "$tipPercentage%",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold,
                    )
                    //Slider
                    Slider(
                        value = sliderPositionState.value,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                            tipAmountState.value =
                                calculateTotalTip(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = tipPercentage
                                )
                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                tipPercentage = tipPercentage,
                                splitBy = splitByState.value,
                            )
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        //steps = 5,
                        onValueChangeFinished = {

                        }
                    )

                } else {
                    Box {}
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipAppTheme {
        MyApp {
            MainContent()
        }
    }
}