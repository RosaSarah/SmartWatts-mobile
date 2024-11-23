package edu.curso.smartwatts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.curso.smartwatts.ui.theme.SmartWattsTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

data class Usuario(
    val nomeCompleto: String = "",
    val cpf: String = "",
    val telefone: String = "",
    val email: String = "",
    val cep: String = "",
    val senha: String = ""
)

class CadastroActivity : ComponentActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    var nomeCompleto by mutableStateOf("")
    var cpf by mutableStateOf("")
    var telefone by mutableStateOf("")
    var email by mutableStateOf("")
    var cep by mutableStateOf("")
    var senha by mutableStateOf("")
    var confirmacaoSenha by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartWattsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CadastroScreen()
                }
            }
        }
    }

    @Composable
    fun CadastroScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cadastro de Usuário",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campos de entrada
                OutlinedTextField(
                    value = nomeCompleto,
                    onValueChange = { nomeCompleto = it },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cpf,
                    onValueChange = { cpf = it },
                    label = { Text("CPF") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = telefone,
                    onValueChange = { telefone = it },
                    label = { Text("Telefone") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cep,
                    onValueChange = { cep = it },
                    label = { Text("CEP") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirmacaoSenha,
                    onValueChange = { confirmacaoSenha = it },
                    label = { Text("Confirmação de Senha") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botão de Cadastro
                Button(
                    onClick = { cadastrarUsuario() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cadastrar")
                }
            }
        }
    }

    private fun cadastrarUsuario() {

        // Verifica se todos os campos estão preenchidos
        if (nomeCompleto.isBlank() || email.isBlank() || senha.isBlank() || confirmacaoSenha.isBlank() || cep.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_LONG).show()
            return
        }


        if (senha != confirmacaoSenha) {
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_LONG).show()
            return
        }

        // Usando Firebase Authentication para criar o usuário
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Usuário criado com sucesso
                    val userId = firebaseAuth.currentUser?.uid
                    if (userId != null) {
                        // Criação do objeto Usuario
                        val usuario = Usuario(
                            nomeCompleto = nomeCompleto,
                            cpf = cpf,
                            telefone = telefone,
                            email = email,
                            cep = cep,
                            senha = senha
                        )

                        // Referência do Firebase Database
                        val usuariosRef = database.getReference("usuarios").child(userId)

                        // Salvando dados no Firebase Realtime Database
                        usuariosRef.setValue(usuario)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()

                                // Redireciona para a MainActivity
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish() // Finaliza a tFinaliza a tela de cadastro
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao salvar os dados: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    // Se o cadastro falhar
                    Toast.makeText(this, "Erro ao cadastrar usuário: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    @Preview(showBackground = true)
    @Composable
    fun CadastroPreview() {
        SmartWattsTheme {
            CadastroScreen()
        }
    }
}
