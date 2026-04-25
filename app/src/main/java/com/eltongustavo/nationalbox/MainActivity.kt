package com.eltongustavo.nationalbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.eltongustavo.nationalbox.ui.theme.black
import com.eltongustavo.nationalbox.ui.theme.blue
import com.eltongustavo.nationalbox.ui.theme.gray
import com.eltongustavo.nationalbox.ui.theme.light_gray
import com.eltongustavo.nationalbox.ui.theme.white

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Home()
            }
        }
    }
}

@Composable
fun Home() {
    var textFieldValue by remember { mutableStateOf("1") }
    var pokemonIdCalculated by remember { mutableIntStateOf(1) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val boxNumber = ((pokemonIdCalculated - 1) / 30) + 1
    val targetSlotIndex = (pokemonIdCalculated - 1) % 30
    val row = (targetSlotIndex / 6) + 1
    val column = (targetSlotIndex % 6) + 1

    val updatePosition = {
        val id = textFieldValue.toIntOrNull() ?: 1
        if (id > 0) pokemonIdCalculated = id
        focusManager.clearFocus()
    }

    Scaffold(
        containerColor = white,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = blue,
            ) {
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .height(55.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "National Box",
                        color = white,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = white,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("POSIÇÃO ATUAL", style = MaterialTheme.typography.labelSmall, color = gray)
                        Text("Pokémon #${pokemonIdCalculated}", style = MaterialTheme.typography.titleMedium)
                        HorizontalDivider(modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 10.dp), thickness = 0.6.dp)
                        Text(
                            text = "BOX $boxNumber, Linha $row, Coluna $column",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = blue
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = light_gray,
                    border = BorderStroke(1.dp, white)
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(6),
                            modifier = Modifier.heightIn(max = 1000.dp),
                            userScrollEnabled = false
                        ) {
                            items(30) { index ->
                                val isSelected = index == targetSlotIndex
                                val slotPokemonId = ((boxNumber - 1) * 30) + index + 1

                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(white)
                                        .then(
                                            if (isSelected) Modifier.border(2.dp, blue, RoundedCornerShape(4.dp))
                                            else Modifier
                                        ),
                                    contentAlignment = Alignment.TopEnd
                                ) {

                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$slotPokemonId.png",
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize().padding(4.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Box {
                                        Text(
                                            text = slotPokemonId.toString(),
                                            color = black,
                                            fontSize = 8.sp,
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .background(
                                                    color = white.copy(alpha = 0.7f),
                                                    shape = RoundedCornerShape(4.dp)

                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { if (it.length <= 5) textFieldValue = it },
                    label = { Text("Número na National Dex ") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(0.9f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { updatePosition() }),
                    singleLine = true
                )
            }
        }
    }
}

@Preview
@Composable
fun HomePreview(){
    Home()
}