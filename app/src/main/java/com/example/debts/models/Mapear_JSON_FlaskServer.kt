package com.example.debts.models

data class Usuario(
    val nome: String,
    val email: String,
    val cpf: String,
    val senha: String,
    val id_usuario: Int
)

data class LoginResponse(
    val message: String,
    val dados_usuario: Usuario?
)