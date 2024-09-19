package com.example.debts.ConsultaBD_MySQL

import android.content.Context
import android.util.Log
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException

class CompararListas_MySQL_SQLite (private val context: Context) {

    fun formatDateString(dateString: String): String {
        // Define o formato de entrada
        val inputFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")

        // Converte a string para LocalDate
        val date = LocalDate.parse(dateString, inputFormatter)

        // Define o formato de saída
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Retorna a data formatada
        return date.format(outputFormatter)
    }

    fun adicionar_remover_Metas(listaMeta_MySQL: List<dados_listaMeta_DebtMap>, listaMeta_SQLite: List<dados_listaMeta_DebtMap>) {

        val IDusuario = DadosUsuario_BD_Debts(context).pegarIdUsuario()

        var resultado = ""

        if (listaMeta_MySQL.size > listaMeta_SQLite.size) {
            val listaMetasNovas: MutableList<dados_listaMeta_DebtMap> = mutableListOf()

            for (meta in listaMeta_MySQL) {
                // Comparar apenas o ID da meta
                if (listaMeta_SQLite.none { it.idMeta == meta.idMeta }) {
                    listaMetasNovas.add(meta)
                }
            }

            listaMetasNovas.forEach { meta ->

                val idMeta = meta.idMeta.toInt()
                val nomeMeta = meta.nomeMeta

                // formatando a data
                val dataMeta = meta.dataCriacaoMeta

                val dataMetaFormatada = formatDateString(dataMeta)

                val listaMetas = meta.listaMetas_Item
                val listaMetasConvertida = mutableListOf<String>()

                listaMetas.forEach { item ->  listaMetasConvertida.add(item.nomeMeta.toString())}

                // Criando a lista de estados (inicialmente todos false)
                val listaMetaEstados = MutableList(listaMetasConvertida.size) { false }

                val progressoMeta = meta.progressoMeta

                BancoDados(context).salvarMeta(nomeMeta, dataMetaFormatada, listaMetasConvertida.toList(), listaMetaEstados.toList(), progressoMeta, IDusuario, idMeta)

                resultado = "Novas Metas SQLite"
            }

            listaMetasNovas.clear()
        }

        else {
            val listaRemoverMetas = listaMeta_SQLite.filterNot { sqliteMeta ->
                listaMeta_MySQL.any { mysqlMeta ->
                    mysqlMeta.idMeta == sqliteMeta.idMeta
                }
            }

            listaRemoverMetas.forEach { item -> BancoDados(context).excluirMeta(IDusuario, item.idMeta) }

            resultado = "Metas Removidas do SQLite"
        }

        Log.d("RESULTADO", resultado)
    }
}