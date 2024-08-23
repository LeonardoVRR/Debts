package com.example.debts.Conexao_BD

class usuarioBD_Debts {
    lateinit var usuario: String
    lateinit var senha: String

    public fun getUsuario(usuario: String) {
        this.usuario = ""
    }

    public fun getSenha(senha: String) {
        this.senha = ""
    }
}