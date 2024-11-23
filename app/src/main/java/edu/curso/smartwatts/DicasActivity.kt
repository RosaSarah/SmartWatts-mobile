package edu.curso.smartwatts

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import edu.curso.smartwatts.ui.theme.SmartWattsTheme

data class FaturaResumo(
    val consumoTotal: Double = 0.0,
    val valorTotal: Double = 0.0,
    val quantidadeFaturas: Int = 0
)

class DicasActivity : ComponentActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private var faturaResumo by mutableStateOf(FaturaResumo())
    private var dicas by mutableStateOf("Carregando dicas...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmartWattsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DicasScreen()
                }
            }
        }

        // Carrega o resumo das faturas ao iniciar a tela
        carregarResumoFaturas()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DicasScreen() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.iconl), // Substitua pelo ID correto da logo
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Resumo das Faturas",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )

                        // Exibe os dados de forma mais organizada
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Consumo Total: ${faturaResumo.consumoTotal} kWh",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Valor Total: R$ ${faturaResumo.valorTotal}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Quantidade de Faturas: ${faturaResumo.quantidadeFaturas}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        Text(
                            text = "Dicas para Reduzir o Consumo:",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB))
                        ) {
                            Text(
                                text = dicas,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        )
    }

    private fun carregarResumoFaturas() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Erro: usuário não autenticado", Toast.LENGTH_LONG).show()
            return
        }

        val faturasRef = database.getReference("faturas").child(userId)
        faturasRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    var consumoTotal = 0.0
                    var valorTotal = 0.0
                    var quantidadeFaturas = 0

                    snapshot.children.forEach { child ->
                        val fatura = child.getValue(Fatura::class.java)
                        if (fatura != null) {
                            consumoTotal += fatura.consumoKwh.toDoubleOrNull() ?: 0.0
                            valorTotal += fatura.valor.toDoubleOrNull() ?: 0.0
                            quantidadeFaturas++
                        }
                    }

                    faturaResumo = FaturaResumo(
                        consumoTotal = consumoTotal,
                        valorTotal = valorTotal,
                        quantidadeFaturas = quantidadeFaturas
                    )

                    // Gera dicas com base nos dados carregados
                    gerarDicas(consumoTotal, quantidadeFaturas)
                } else {
                    dicas = "Nenhuma fatura encontrada. Cadastre suas faturas para receber dicas!"
                }
            }
            .addOnFailureListener { e ->
                Log.e("DicasActivity", "Erro ao carregar faturas", e)
                Toast.makeText(this, "Erro ao carregar faturas: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun gerarDicas(consumoTotal: Double, quantidadeFaturas: Int) {
        val consumoMedio = consumoTotal / quantidadeFaturas

        dicas = when {
            consumoMedio < 100 -> {
                "Seu consumo está baixo! Continue economizando energia e aproveite para verificar se todos os equipamentos estão sendo usados de maneira eficiente."
            }
            consumoMedio in 100.0..200.0 -> {
                "Seu consumo está moderado. Considere substituir lâmpadas por LED, desligar aparelhos em stand-by e revisar o uso de eletrodomésticos."
            }
            consumoMedio > 200 -> {
                "Seu consumo está alto! Reduza o uso de ar-condicionado, evite banho demorado e certifique-se de desligar os aparelhos que não estão em uso."
            }
            else -> {
                "Não foi possível gerar dicas específicas. Verifique se todas as suas faturas estão corretas."
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DicasPreview() {
        SmartWattsTheme {
            DicasScreen()
        }
    }
}
