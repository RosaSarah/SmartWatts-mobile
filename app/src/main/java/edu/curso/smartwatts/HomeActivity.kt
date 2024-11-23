package edu.curso.smartwatts

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import edu.curso.smartwatts.ui.theme.SmartWattsTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartWattsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        onNavigateToFatura = {
                            startActivity(Intent(this, FaturaActivity::class.java))
                        },
                        onNavigateToPerfil = {
                            startActivity(Intent(this, PerfilActivity::class.java))
                        },
                        onNavigateToDicas = {
                            startActivity(Intent(this, DicasActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToFatura: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToDicas: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.iconl), // Substituir pelo ID correto da logo
                            contentDescription = "Logo SmartWatts",
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "SmartWatts",
                            color = Color(0xFF2196F3), // Texto em azul
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { isMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu",
                            tint = Color(0xFF2196F3) // Ícone do menu em azul
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Dicas de Economia",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )

                // Lista Vertical de Dicas
                ListaVerticalDeDicas(
                    dicas = listOf(
                        "Use lâmpadas de LED para economizar energia.",
                        "Desligue os aparelhos da tomada quando não estiverem em uso.",
                        "Ajuste o termostato para uma temperatura eficiente.",
                        "Use eletrodomésticos no período fora de pico para economizar.",
                        "Faça manutenção regular nos equipamentos elétricos.",
                        "Mantenha o ar-condicionado com manutenção em dia.",
                        "Use ventiladores para reduzir o uso de ar-condicionado.",
                        "Opte por chuveiros de baixa potência.",
                        "Desligue luzes em cômodos desocupados.",
                        "Utilize luz natural sempre que possível."
                    )
                )
            }
        }
    )

    DropdownMenu(
        expanded = isMenuExpanded,
        onDismissRequest = { isMenuExpanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Fatura") },
            onClick = {
                isMenuExpanded = false
                onNavigateToFatura()
            },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Receipt, contentDescription = "Fatura")
            }
        )
        DropdownMenuItem(
            text = { Text("Perfil") },
            onClick = {
                isMenuExpanded = false
                onNavigateToPerfil()
            },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Person, contentDescription = "Perfil")
            }
        )
        DropdownMenuItem(
            text = { Text("Dicas de Economia") },
            onClick = {
                isMenuExpanded = false
                onNavigateToDicas()
            },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Help, contentDescription = "Dicas de Economia")
            }
        )
    }
}

@Composable
fun ListaVerticalDeDicas(dicas: List<String>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(dicas.size) { index ->
            DicaCard(dica = dicas[index])
        }
    }
}

@Composable
fun DicaCard(dica: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFBBDEFB)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = dica,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SmartWattsTheme {
        HomeScreen(
            onNavigateToFatura = {},
            onNavigateToPerfil = {},
            onNavigateToDicas = {}
        )
    }
}
