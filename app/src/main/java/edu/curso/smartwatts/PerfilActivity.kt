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

class PerfilActivity : ComponentActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    var usuario by mutableStateOf(Usuario("", "", "", "", "", ""))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmartWattsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PerfilScreen()
                }
            }
        }

        // Carrega os dados do usuário ao abrir a tela
        carregarDadosDoUsuario()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PerfilScreen() {
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
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Atualizar Dados do Usuário",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campos de entrada reutilizando o `data class Usuario`
                        OutlinedTextField(
                            value = usuario.nomeCompleto,
                            onValueChange = { usuario = usuario.copy(nomeCompleto = it) },
                            label = { Text("Nome Completo") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = usuario.cpf,
                            onValueChange = { usuario = usuario.copy(cpf = it) },
                            label = { Text("CPF") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = usuario.telefone,
                            onValueChange = { usuario = usuario.copy(telefone = it) },
                            label = { Text("Telefone") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = usuario.email,
                            onValueChange = { usuario = usuario.copy(email = it) },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true // Email geralmente não é editável
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = usuario.cep,
                            onValueChange = { usuario = usuario.copy(cep = it) },
                            label = { Text("CEP") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botão para Atualizar
                        Button(
                            onClick = { atualizarDadosDoUsuario() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Atualizar")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botão para Excluir
                        Button(
                            onClick = { excluirUsuario() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                        ) {
                            Text("Excluir Conta")
                        }
                    }
                }
            }
        )
    }

    private fun carregarDadosDoUsuario() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Erro: usuário não autenticado", Toast.LENGTH_LONG).show()
            return
        }

        val usuariosRef = database.getReference("usuarios").child(userId)
        usuariosRef.get()
            .addOnSuccessListener { snapshot ->
                val usuarioCarregado = snapshot.getValue(Usuario::class.java)
                if (usuarioCarregado != null) {
                    usuario = usuarioCarregado
                } else {
                    Toast.makeText(this, "Erro ao carregar os dados", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("PerfilActivity", "Erro ao carregar os dados do usuário", e)
                Toast.makeText(this, "Erro ao carregar os dados: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun atualizarDadosDoUsuario() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Erro: usuário não autenticado", Toast.LENGTH_LONG).show()
            return
        }

        val usuariosRef = database.getReference("usuarios").child(userId)
        usuariosRef.setValue(usuario)
            .addOnSuccessListener {
                Toast.makeText(this, "Dados atualizados com sucesso!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Log.e("PerfilActivity", "Erro ao atualizar os dados", e)
                Toast.makeText(this, "Erro ao atualizar os dados: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun excluirUsuario() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Erro: usuário não autenticado", Toast.LENGTH_LONG).show()
            return
        }

        // Exclui os dados do usuário do Firebase Database
        val usuariosRef = database.getReference("usuarios").child(userId)
        usuariosRef.removeValue()
            .addOnSuccessListener {
                // Exclui o usuário da Firebase Authentication
                firebaseAuth.currentUser?.delete()
                    ?.addOnSuccessListener {
                        Toast.makeText(this, "Conta excluída com sucesso!", Toast.LENGTH_LONG).show()
                        finish() // Fecha a tela de perfil após exclusão
                    }
                    ?.addOnFailureListener { e ->
                        Log.e("PerfilActivity", "Erro ao excluir o usuário", e)
                        Toast.makeText(this, "Erro ao excluir a conta: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("PerfilActivity", "Erro ao excluir os dados", e)
                Toast.makeText(this, "Erro ao excluir os dados: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    @Preview(showBackground = true)
    @Composable
    fun PerfilPreview() {
        SmartWattsTheme {
            PerfilScreen()
        }
    }
}
