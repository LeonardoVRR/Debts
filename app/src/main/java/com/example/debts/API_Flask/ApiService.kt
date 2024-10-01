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
