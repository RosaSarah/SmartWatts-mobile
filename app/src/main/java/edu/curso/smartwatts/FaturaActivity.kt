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

data class Fatura(
    val valor: String = "",
    val consumoKwh: String = "",
    val mesReferencia: String = "",
    val bandeiraTarifaria: String = "",
    val cep: String = ""
)

class FaturaActivity : ComponentActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private var fatura by mutableStateOf(Fatura())
    private var cepResidencia by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmartWattsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FaturaScreen()
                }
            }
        }

        // Carrega o CEP do usuário ao abrir a tela
        carregarCepDoUsuario()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FaturaScreen() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Registrar Fatura",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )

                        // Campo para o valor
                        OutlinedTextField(
                            value = fatura.valor,
                            onValueChange = { fatura = fatura.copy(valor = it) },
                            label = { Text("Valor (R$)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Campo para o consumo
                        OutlinedTextField(
                            value = fatura.consumoKwh,
                            onValueChange = { fatura = fatura.copy(consumoKwh = it) },
                            label = { Text("Consumo (kWh)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Campo para o mês de referência
                        OutlinedTextField(
                            value = fatura.mesReferencia,
                            onValueChange = { fatura = fatura.copy(mesReferencia = it) },
                            label = { Text("Mês de Referência") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Campo para a Bandeira Tarifária
                        OutlinedTextField(
                            value = fatura.bandeiraTarifaria,
                            onValueChange = { fatura = fatura.copy(bandeiraTarifaria = it) },
                            label = { Text("Bandeira Tarifária") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Campo do CEP da residência (somente leitura)
                        OutlinedTextField(
                            value = cepResidencia,
                            onValueChange = { },
                            label = { Text("CEP da Residência") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botão para registrar fatura
                        Button(
                            onClick = { registrarFatura() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Registrar")
                        }
                    }
                }
            }
        )
    }

    private fun carregarCepDoUsuario() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Erro: usuário não autenticado", Toast.LENGTH_LONG).show()
            return
        }

        val usuariosRef = database.getReference("usuarios").child(userId)
        usuariosRef.child("cep").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    cepResidencia = snapshot.value?.toString() ?: ""
                    Log.d("FaturaActivity", "CEP carregado: $cepResidencia")
                } else {
                    Log.e("FaturaActivity", "CEP não encontrado no banco de dados.")
                    Toast.makeText(this, "CEP não encontrado. Atualize seus dados no perfil.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("FaturaActivity", "Erro ao carregar o CEP do usuário", e)
                Toast.makeText(this, "Erro ao carregar o CEP: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    private fun registrarFatura() {
        // Verifica se os campos estão preenchidos
        if (fatura.valor.isBlank() ||
            fatura.consumoKwh.isBlank() ||
            fatura.mesReferencia.isBlank() ||
            fatura.bandeiraTarifaria.isBlank()) {
            Toast.makeText(this, "Por favor, preencha todos os campos antes de registrar a fatura.", Toast.LENGTH_LONG).show()
            return
        }

        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Erro: usuário não autenticado", Toast.LENGTH_LONG).show()
            return
        }

        val faturasRef = database.getReference("faturas").child(userId).push() // Cria um nó único para cada fatura
        val novaFatura = fatura.copy(cep = cepResidencia)

        faturasRef.setValue(novaFatura)
            .addOnSuccessListener {
                Toast.makeText(this, "Fatura registrada com sucesso!", Toast.LENGTH_LONG).show()
                finish() // Finaliza a tela após o registro
            }
            .addOnFailureListener { e ->
                Log.e("FaturaActivity", "Erro ao registrar a fatura", e)
                Toast.makeText(this, "Erro ao registrar a fatura: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    @Preview(showBackground = true)
    @Composable
    fun FaturaPreview() {
        SmartWattsTheme {
            FaturaScreen()
        }
    }
}
