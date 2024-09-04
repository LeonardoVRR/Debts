package com.example.debts.Conexao_BD

import android.content.Context
import android.util.Log
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.CustomToast

class DadosUsuario_BD_Debts(private val context: Context) {

    var usuarioLogado: String = recuperarUsuarioLogado()

    private val dadosUsuario = BancoDados(context)
    private var nomeUsuario: String? = null
    private var emailUsuario: String? = null
    private var cpfUsuario: String? = null
    private var senhaUsuario: String? = null
    private var idUsuario: Int = 0

    // Função para salvar o nome do usuário logado nas preferências compartilhadas.
    fun salvarUsuarioLogado(usuario: String) {
        // Obtém o SharedPreferences para armazenar dados com o nome "UserPrefs".
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Obtém o editor para modificar as preferências.
        val editor = sharedPreferences.edit()

        // Salva o nome do usuário com a chave "usuarioLogado".
        editor.putString("usuarioLogado", usuario)

        // Aplica as mudanças de forma assíncrona.
        editor.apply()
    }

    // Função para recuperar o nome do usuário logado das preferências compartilhadas.
    fun recuperarUsuarioLogado(): String {
        // Obtém o SharedPreferences para ler dados com o nome "UserPrefs".
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Recupera o valor associado à chave "usuarioLogado". Retorna uma string vazia se não encontrado.
        return sharedPreferences.getString("usuarioLogado", "") ?: ""
    }

    fun pegarIdUsuario(): Int {

        val dados = dadosUsuario.salvarDadosUsuario(usuarioLogado)

        if (dados != null) {
            idUsuario = dados[4].toInt()
        }

        //CustomToast().showCustomToast(context, "ID: $idUsuario")

        return idUsuario
    }

    fun pegarNomeUsuario(): String {
        val dados = dadosUsuario.salvarDadosUsuario(usuarioLogado)

        if (dados != null) {
            nomeUsuario = dados[0]
        }

        return nomeUsuario ?: "Nome não encontrado"
    }

    fun pegarSenhaUsuario(): String {

        val dados = dadosUsuario.salvarDadosUsuario(usuarioLogado)

        if (dados != null) {
            senhaUsuario = dados[3]
        }

        return senhaUsuario ?: "Senha não encontrada"
    }

    fun pegarCPFUsuario(): String {

        val dados = dadosUsuario.salvarDadosUsuario(usuarioLogado)

        if (dados != null) {
            cpfUsuario = dados[2]
        }

        return cpfUsuario ?: "cpf não encontrado"
    }

    fun pegarEmailUsuario(): String {

        val dados = dadosUsuario.salvarDadosUsuario(usuarioLogado)

        if (dados != null) {
            emailUsuario = dados[1]
        }

        return emailUsuario ?: "Email não encontrado"
    }
}