package edu.curso.smartwatts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.curso.smartwatts.ui.theme.SmartWattsTheme
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

data class CorpoResposta(var idToken: String = "")

class MainActivity : ComponentActivity() {

    private val API_KEY = "AIzaSyCo5qVSuhwbrIbBJRnD0bcMQcAONZvnrhI"
    private val URL_BASE = "https://identitytoolkit.googleapis.com/v1"

    private val httpClient = OkHttpClient()
    private val gson = Gson()

    private var email by mutableStateOf("")
    private var password by mutableStateOf("")
    private var token by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartWattsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        }
    }

    @Composable
    fun LoginScreen() {
        var passwordVisible by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Logo do SmartWatts",
                    modifier = Modifier
                        .size(180.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Bem-Vindo!",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Esconder senha" else "Mostrar senha"
                            )
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { realizarLogin() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(16.dp))

                ClickableText(
                    text = AnnotatedString("Ainda não tem uma conta? Cadastre-se aqui"),
                    onClick = {
                        val intent = Intent(this@MainActivity, CadastroActivity::class.java)
                        startActivity(intent)
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }

    private fun realizarLogin() {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val loginJson = """
            {
                "email": "$email",
                "password": "$password",
                "returnSecureToken": true
            }
        """

        val request = Request.Builder()
            .url("$URL_BASE/accounts:signInWithPassword?key=$API_KEY")
            .post(loginJson.toRequestBody("application/json".toMediaType()))
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Login", "Erro ao fazer o login", e)
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Erro ao se conectar. Verifique sua conexão com a internet.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    try {
                        val corpoResposta = gson.fromJson(responseBody, CorpoResposta::class.java)
                        token = corpoResposta.idToken
                        Log.i("Login", "Token recebido: $token")
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Login realizado com sucesso!", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } catch (e: Exception) {
                        Log.e("Login", "Erro ao processar a resposta: $responseBody", e)
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Erro ao processar a resposta do servidor.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    try {
                        val errorMap = gson.fromJson(responseBody, Map::class.java)
                        val errorMessage = (errorMap["error"] as? Map<*, *>)?.get("message") as? String
                        Log.e("Login", "Erro: $errorMessage")
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Erro no login: $errorMessage",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        Log.e("Login", "Erro desconhecido ao processar resposta: $responseBody", e)
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Erro desconhecido. Tente novamente.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        })
    }

    @Preview(showBackground = true)
    @Composable
    fun LoginPreview() {
        SmartWattsTheme {
            LoginScreen()
        }
    }
}
