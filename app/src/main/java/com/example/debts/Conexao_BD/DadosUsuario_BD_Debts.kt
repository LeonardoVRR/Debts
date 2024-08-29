package com.example.debts.Conexao_BD

class DadosUsuario_BD_Debts {

    //função para pegar o nome do usuario no banco de dados
    fun pegarNomeUsuario(): String{
        val nome: String = "Leonardo"

        return nome
    }

    //função para pegar o senha do usuario no banco de dados
    fun pegarSenhaUsuario(): String {
        val senha: String = "123"

        return senha
    }

    //função para pegar o email do usuario no banco de dados
    fun pegarEmailUsuario(): String {
        val email: String = "teste@gmail.com"

        return email
    }
}