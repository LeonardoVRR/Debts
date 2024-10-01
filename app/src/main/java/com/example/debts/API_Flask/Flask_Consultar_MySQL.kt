package com.example.debts.API_Flask

import android.content.Context
import android.util.Log
import com.example.debts.CustomToast
import com.example.debts.models.LoginResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.jvm.Throws
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType

class Flask_Consultar_MySQL(private val context: Context) {

    @Throws(IOException::class)
    fun fazerLogin(json: String): String {
        val client = OkHttpClient()

        val url = "http://192.168.0.22:5000/login"

        val builder: Request.Builder = Request.Builder()
        builder.url(url)

        val mediaType: MediaType? = MediaType.parse("application/json; charset=utf-8")
        val body: RequestBody = RequestBody.create(mediaType, json)

        builder.post(body)

        val request: Request = builder.build()

        // Executa a requisição
        val response: okhttp3.Response = client.newCall(request).execute()

        // Verifica se a resposta não é nula e retorna o corpo da resposta como string
        return response.body()?.string() ?: throw IOException("Corpo da resposta nulo")

    }

    fun validarLogin(nome: String, senha: String): Boolean {
        var resultado = false

        val jsonRequest = """
        {
            "nome": "$nome",
            "senha": "$senha"
        }
    """.trimIndent()

        try {
            val jsonResponse = fazerLogin(jsonRequest)
            Log.d("RESPOSTA BRUTA", jsonResponse)  // Log da resposta bruta

            // Usando Gson para converter a resposta JSON em um objeto
            val gson = Gson()
            val loginResponse = gson.fromJson(jsonResponse, LoginResponse::class.java)

            Log.d("RESPOSTA FLASK", jsonResponse)  // Log da resposta
            Log.d("RESPOSTA FLASK2", "$loginResponse")

            // Verificando a mensagem da resposta
            when (loginResponse.message) {
                "Login valido" -> {
                    val dadosUsuario = loginResponse.dados_usuario
                    if (dadosUsuario != null) {
                        Log.d("Dados do Usuário", "Nome: ${dadosUsuario.nome}, Email: ${dadosUsuario.email}")
                        resultado = true
                    } else {
                        Log.e("ERRO", "Dados do usuário estão nulos mesmo após login bem-sucedido.")
                    }
                }
                "Login invalido" -> {
                    resultado = false
                }
                else -> {
                    Log.e("ERRO", "Mensagem não reconhecida: ${loginResponse.message}")
                }
            }

        } catch (e: IOException) {
            Log.e("ERRO", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO", "Erro inesperado: ${e.message}")
        }

        return resultado
    }


//    fun fazerLogin(loginRequest: LoginRequest): Boolean {
//        var loginValido = false
//
//        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
//
//        val call = apiService.login(loginRequest)
//        call.enqueue(object : Callback<LoginResponse> {
//            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
//                if (response.isSuccessful) {
//                    val loginResponse = response.body()
//
//                    if (loginResponse?.message == "Login bem-sucedido") {
//                        loginValido = true
//                        val dadosUsuario = loginResponse.dados_usuario
//                        CustomToast().showCustomToast(context, "Bem-vindo, ${dadosUsuario?.nome}")
//                    }
//                } else {
//                    loginValido = false
//                    CustomToast().showCustomToast(context, "Login inválido")
//                    Log.d("Login inválido:", "${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                CustomToast().showCustomToast(context, "Falha na requisição: ${t.message}")
//                Log.e("Falha na requisição", "${t.message}")
//            }
//        })
//
//        return loginValido
//    }
}