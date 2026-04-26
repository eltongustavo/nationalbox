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
import androidx.compose.material.icons.filled.Warning
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
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.eltongustavo.nationalbox.ui.theme.black
import com.eltongustavo.nationalbox.ui.theme.blue
import com.eltongustavo.nationalbox.ui.theme.gray
import com.eltongustavo.nationalbox.ui.theme.light_gray
import com.eltongustavo.nationalbox.ui.theme.white
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URL

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
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .respectCacheHeaders(false)
            .build()
    }

    var textFieldValue by remember { mutableStateOf("1") }
    var pokemonIdCalculated by remember { mutableIntStateOf(1) }
    var maxPokemonId by remember { mutableIntStateOf(1025) } 
    var refreshTrigger by remember { mutableIntStateOf(0) }
    var pokemonIdsInBox by remember { mutableStateOf((1..30).toList()) }

    // Pasta local para salvar as imagens
    val imagesDir = remember { File(context.filesDir, "pokemon_images").apply { if (!exists()) mkdirs() } }
    var isSyncing by remember { mutableStateOf(false) }
    var syncProgress by remember { mutableIntStateOf(0) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // 1. Busca o total de pokémons e inicia sincronização
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = URL("https://pokeapi.co/api/v2/pokemon-species/?limit=0").readText()
                val json = JSONObject(response)
                val count = json.getInt("count")
                if (count > 0) maxPokemonId = count

                // 2. Lógica de Sincronização
                val files = imagesDir.listFiles()?.filter { it.name.endsWith(".png") } ?: emptyList()
                if (files.size < maxPokemonId) {
                    isSyncing = true
                    for (id in 1..maxPokemonId) {
                        val file = File(imagesDir, "$id.png")
                        if (!file.exists()) {
                            try {
                                val url = URL("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png")
                                val connection = url.openConnection()
                                connection.connectTimeout = 5000
                                connection.readTimeout = 5000
                                
                                connection.getInputStream().use { input ->
                                    FileOutputStream(file).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                syncProgress = id
                                delay(500)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    isSyncing = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val boxNumber = ((pokemonIdCalculated - 1) / 30) + 1
    val targetSlotIndex = (pokemonIdCalculated - 1) % 30
    val row = (targetSlotIndex / 6) + 1
    val column = (targetSlotIndex % 6) + 1

    val updatePosition = {
        val id = textFieldValue.toIntOrNull() ?: 1
        val validId = id.coerceIn(1, maxPokemonId)
        pokemonIdCalculated = validId
        textFieldValue = validId.toString()

        val currentBox = ((validId - 1) / 30) + 1
        pokemonIdsInBox = (0..29).map { ((currentBox - 1) * 30) + it + 1 }

        refreshTrigger++ 
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "National Box",
                            color = white,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        if (isSyncing) {
                            Text(
                                text = "Sincronizando imagens: $syncProgress/$maxPokemonId",
                                color = white.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        }
                    }
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
                            items(
                                count = pokemonIdsInBox.size,
                                key = { index -> "${pokemonIdsInBox[index]}_$refreshTrigger" }
                            ) { index ->
                                val slotPokemonId = pokemonIdsInBox[index]
                                val isSelected = slotPokemonId == pokemonIdCalculated
                                val localFile = remember(slotPokemonId) { File(imagesDir, "$slotPokemonId.png") }

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
                                    if (slotPokemonId <= maxPokemonId) {
                                        val request = remember(slotPokemonId, refreshTrigger) {
                                            val data: Any = if (localFile.exists()) localFile
                                            else "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$slotPokemonId.png"

                                            ImageRequest.Builder(context)
                                                .data(data)
                                                .crossfade(true)
                                                .build()
                                        }

                                        SubcomposeAsyncImage(
                                            model = request,
                                            imageLoader = imageLoader,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize().padding(4.dp),
                                            contentScale = ContentScale.Fit,
                                            loading = {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(24.dp),
                                                        strokeWidth = 2.dp,
                                                        color = light_gray
                                                    )
                                                }
                                            },
                                            error = {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Warning,
                                                        contentDescription = null,
                                                        tint = gray.copy(alpha = 0.4f),
                                                        modifier = Modifier.size(32.dp)
                                                    )
                                                }
                                            }
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = gray.copy(alpha = 0.2f),
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
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
                    onValueChange = {
                        if (it.all { char -> char.isDigit() } && it.length <= maxPokemonId.toString().length) {
                            textFieldValue = it
                        }
                    },
                    label = { Text("Número na National Dex (Máx: $maxPokemonId)") },
                    leadingIcon = {
                        IconButton(onClick = { updatePosition() }) {
                            Icon(Icons.Default.Search, contentDescription = "Pesquisar")
                        }
                    },
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