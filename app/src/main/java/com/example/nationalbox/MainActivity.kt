package com.example.nationalbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nationalbox.ui.theme.NationalBoxTheme
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.FocusRequester


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NationalBoxTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(innerPadding)
                }
            }
        }
    }
}

@Composable
fun MainContent(innerPadding: PaddingValues) {
    var displayedNumber by remember { mutableStateOf("") } // Estado para o número exibido

    Column(modifier = Modifier.padding(innerPadding)) {
        // Header do Aplicativo (retângulo vermelho + texto)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.Red)
        ) {
            Text(
                text = "National Box",
                color = Color.White,
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // --------------------------------------------------------------------

        // Corpo do aplicativo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Gray)
        )  {
            // Usar uma Column para empilhar os campos numéricos
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight() // Para ocupar a altura total do Box
                    .fillMaxWidth() // Para ocupar a largura total do Box
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // Espaçamento entre os campos
                NationalNumber(
                    onEnterPressed = { number ->
                        displayedNumber = number // Atualiza o número exibido
                    }
                )
                // Exibe o número após pressionar Enter
                if (displayedNumber.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp)) // Espaçamento entre os campos
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(Color.Black)
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "O pokémon de número " + displayedNumber + " ficará na:",
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    val numeroDex = displayedNumber.toInt() // Converte a String em Int

                    // Considerando que a box é 5x6 (5 linhas e 6 colunas)
                    val columns = 6
                    val rows = 5

                    // Cálculo da box, linha e coluna
                    val boxNumber = (numeroDex - 1) / (rows * columns) + 1
                    val lineNumber = ((numeroDex - 1) % (rows * columns)) / columns + 1
                    val columnNumber = ((numeroDex - 1) % (rows * columns)) % columns + 1

                    // Retângulos para Box, Linha e Coluna
                    val labels = listOf("Box: " + boxNumber, "Linha: " + lineNumber, "Coluna: " + columnNumber)
                    labels.forEach { label ->
                        Spacer(modifier = Modifier.height(16.dp)) // Espaçamento entre os retângulos
                        Box(
                            modifier = Modifier
                                .width(250.dp)
                                .height(60.dp)
                                .background(Color.Black)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = label,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center),
                                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                }
            }
        }

        // --------------------------------------------------------------------

        // Footer do aplicativo (retângulo preto na parte inferior)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(Color.Black)
        ) {
            Text(
                text = "Desenvolvido por Elton G.S.F",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun NationalNumber(onEnterPressed: (String) -> Unit) {
    val numberState = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus() // Solicita foco assim que o componente é composto
    }

    TextField(
        value = numberState.value,
        onValueChange = { newValue ->
            numberState.value = newValue.filter { it.isDigit() } // Permite apenas dígitos
        },
        label = { Text("Digite o número do Pokémon") },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Enter) {
                    onEnterPressed(numberState.value) // Chama a função quando Enter é pressionado
                    numberState.value = "" // Limpa o campo numérico
                    true // Indica que o evento foi consumido
                } else {
                    false
                }
            }
            .focusRequester(focusRequester) // Aplica o focusRequester
    )
}
