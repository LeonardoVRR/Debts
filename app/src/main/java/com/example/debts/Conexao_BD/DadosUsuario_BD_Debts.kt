package com.example.debts.Conexao_BD

import android.content.Context
import com.example.debts.API_Flask.Flask_Consultar_MySQL
//import com.example.debts.BD_MySQL_App.Metodos_BD_MySQL
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.layout_Item_lista.OperacaoFinanceira
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
//import java.time.LocalDateTime
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException

class DadosUsuario_BD_Debts(private val context: Context) {

    object listas_MySQL {
        var metasUsuario: List<dados_listaMeta_DebtMap> = listOf()
        var rendimentosUsuario: List<OperacaoFinanceira> = listOf()
        var gastosUsuario: List<OperacaoFinanceira> = listOf()
        var cartoesUsuario: List<OperacaoFinanceira> = listOf()
    }

    object listaMetaEstados {
        var estados: MutableList<Boolean> = mutableListOf()
    }

    var usuarioLogado: String = recuperarUsuarioLogado()

    //private val dadosUsuario_MySQL = Metodos_BD_MySQL()

    private val dadosUsuario = BancoDados(context)
    private var nomeUsuario: String? = null
    private var emailUsuario: String? = null
    private var cpfUsuario: String? = null
    private var senhaUsuario: String? = null
    private var idUsuario: Int = 0

    // Função para salvar o estado de login
    fun salvarEstadoLogin(loginEfetuado: Boolean) {
        val sharedPref = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("loginEfetuado", loginEfetuado)
            apply()
        }
    }

    // Função para verificar o estado de login
    fun verificarEstadoLogin(): Boolean {
        val sharedPref = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("loginEfetuado", false) // false é o valor padrão
    }

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

    //função para salvar a ultima vez que foi feito a consulta em uma tabela no BD
    fun setLastUpdateTimestamp_ListaMySQL(timestamp: LocalDateTime, salvarConsultaLista: String) {

        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")

        val timestampConvertido: String = timestamp.format(formato)

        val sharedPreferences = context.getSharedPreferences("timesTamp_$salvarConsultaLista", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("lastUpdateTimestamp_$salvarConsultaLista", timestampConvertido)
        editor.apply() // Salva o timestamp
    }

    // Função para resgatar a última vez que foi feito a consulta em uma tabela no BD
    fun getLastUpdateTimestamp_ListaMySQL(obterConsultaLista: String): LocalDateTime {
        val sharedPreferences = context.getSharedPreferences("timesTamp_$obterConsultaLista", Context.MODE_PRIVATE)
        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")

        val savedTimestamp = sharedPreferences.getString("lastUpdateTimestamp_$obterConsultaLista", null)

        return try {
            if (savedTimestamp != null) {
                LocalDateTime.parse(savedTimestamp, formato)
            } else {
                // Define um valor padrão se não houver timestamp salvo
                LocalDateTime.MIN
            }
        } catch (e: DateTimeParseException) {
            // Caso a análise falhe, defina um valor padrão
            LocalDateTime.MIN
        }
    }

    fun pegarIdUsuario(): Int {

        //val dados = dadosUsuario.salvarDadosUsuario(usuarioLogado)

        val dados = Flask_Consultar_MySQL.dadosUsuario.listaDados

        if (dados.isNotEmpty()) {
            idUsuario = dados[4].toInt()
        }

        //CustomToast().showCustomToast(context, "ID: $idUsuario")

        return idUsuario
    }

    fun pegarNomeUsuario(): String {
        //val dados = dadosUsuario.salvarDadosUsuario(usuarioLogado)

        val dados = Flask_Consultar_MySQL.dadosUsuario.listaDados

        if (dados.isNotEmpty()) {
            nomeUsuario = dados[0]
        }

        return nomeUsuario ?: "Nome não encontrado"
    }

    fun pegarSenhaUsuario(): String {

        //val dados = dadosUsuario.salvarDadosUsuario(usuarioLogado)

        val dados = Flask_Consultar_MySQL.dadosUsuario.listaDados

        if (dados.isNotEmpty()) {
            senhaUsuario = dados[3]
        }

        return senhaUsuario ?: "Senha não encontrada"
    }

    fun pegarCPFUsuario(): String {

        //val dados = dadosUsuario.salvarDadosUsuario(usuarioLogado)

        val dados = Flask_Consultar_MySQL.dadosUsuario.listaDados

        if (dados.isNotEmpty()) {
            cpfUsuario = dados[2]
        }

        return cpfUsuario ?: "cpf não encontrado"
    }

    fun pegarEmailUsuario(): String {

        //val dados = dadosUsuario.salvarDadosUsuario(usuarioLogado)

        val dados = Flask_Consultar_MySQL.dadosUsuario.listaDados

        if (dados.isNotEmpty()) {
            emailUsuario = dados[1]
        }

        return emailUsuario ?: "Email não encontrado"
    }

    fun pegarListaEstadosMetas(IDusuario: Int, IDmeta: String): MutableList<Boolean> {

        listaMetaEstados.estados = BancoDados(context).MetasConcluidas(IDusuario, IDmeta)

        return listaMetaEstados.estados
    }

}