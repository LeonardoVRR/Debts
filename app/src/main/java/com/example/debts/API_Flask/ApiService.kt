package com.example.debts.API_Flask

import com.example.debts.models.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// Defina a interface com o endpoint
interface ApiService {

    @POST("/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}

// Classe para o corpo da requisição (JSON de envio)
data class LoginRequest(
    val nome: String,
    val senha: String
)

data class Meta(
    val data_meta: String,
    val id_meta: Int,
    val lista_metas: List<String>,
    val metas_concluidas: List<Boolean>,
    val nome_meta: String,
    val progresso_meta: Float
)

data class OpFinanc (
    val id: Int,
    val descricao: String,
    val tipo_movimento: String,
    val valor: Float,
    val data: String
)