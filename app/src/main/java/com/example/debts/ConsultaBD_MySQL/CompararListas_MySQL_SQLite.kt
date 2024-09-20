package com.example.debts.ConsultaBD_MySQL

import android.content.Context
import android.util.Log
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.layout_Item_lista.OperacaoFinanceira
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CompararListas_MySQL_SQLite (private val context: Context) {

    fun formatarDataString(data: String): String {

        val dia = data.split("-")[0].trim()
        val mes = data.split("-")[1].trim()
        val ano = data.split("-")[2].trim()

        val numeroMes = when (mes.lowercase()) {
            "janeiro" -> 1
            "fevereiro" -> 2
            "março" -> 3
            "abril" -> 4
            "maio" -> 5
            "junho" -> 6
            "julho" -> 7
            "agosto" -> 8
            "setembro" -> 9
            "outubro" -> 10
            "novembro" -> 11
            "dezembro" -> 12
            else -> 0 // Retorna 0 se o mês não for válido
        }

        // Criar um objeto Calendar para a data
        val calendario = Calendar.getInstance()
        calendario.set(ano.toInt(), numeroMes - 1, dia.toInt())

        // Formatar a data
        val formatoData = SimpleDateFormat("yyyy-MM-dd")
        val dataFormatada = formatoData.format(calendario.time)

        Log.d("DATA FORMATADA", "Dia: $dia, Mês: $mes, Ano: $ano, NumeroMes: $numeroMes")

        // Retorna a data formatada
        return dataFormatada
    }

    fun adicionarNovasMetas(listaMeta_MySQL: List<dados_listaMeta_DebtMap>, listaMeta_SQLite: List<dados_listaMeta_DebtMap>) {

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

//            Log.d("LISTA METAS MySQL", "$listaMeta_MySQL")
//            Log.d("LISTA METAS SQLite", "$listaMeta_SQLite")

            listaMetasNovas.forEach { meta ->

                val idMeta = meta.idMeta.toInt()
                val nomeMeta = meta.nomeMeta

                // formatando a data
                val dataMeta = meta.dataCriacaoMeta

                //Log.d("DATA CRIAÇÂO META", "$dataMeta")

                //val dataMetaFormatada = formatarDataString(dataMeta)

                val listaMetas = meta.listaMetas_Item
                val listaMetasConvertida = mutableListOf<String>()

                listaMetas.forEach { item ->  listaMetasConvertida.add(item.nomeMeta.toString())}

                // Criando a lista de estados (inicialmente todos false)
                val listaMetaEstados = MutableList(listaMetasConvertida.size) { false }

                val progressoMeta = meta.progressoMeta

                BancoDados(context).salvarMeta(nomeMeta, dataMeta, listaMetasConvertida.toList(), listaMetaEstados.toList(), progressoMeta, IDusuario, idMeta)

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

        Log.d("RESULTADO METAS", resultado)
    }

    fun adicionarNovosGastos(listaGasto_MySQL: List<OperacaoFinanceira>, listaGasto_SQLite: List<OperacaoFinanceira>) {

        val IDusuario = DadosUsuario_BD_Debts(context).pegarIdUsuario()

        val listaNovosGastos: MutableList<OperacaoFinanceira> = mutableListOf()

        for (gasto in listaGasto_MySQL) {
            // Comparar apenas o ID da meta
            if (listaGasto_SQLite.none { it.id == gasto.id }) {
                listaNovosGastos.add(gasto)
            }
        }

        listaNovosGastos.forEach { gasto ->

            val idGasto = gasto.id
            val nomeGasto = gasto.descricao

            // formatando a data
            val dataGasto = gasto.data
            val dataGastoFormatada = formatarDataString(dataGasto)

            val tipoMovimento = gasto.tipo_movimento
            val valorGasto = gasto.valor.toFloat()


            BancoDados(context).salvarGasto(nomeGasto, tipoMovimento, valorGasto, dataGastoFormatada, IDusuario, idGasto)

        }

        listaNovosGastos.clear()

        Log.d("RESULTADO GASTOS", "Novos Gastos SQLite")
    }

    fun adicionarNovosRendimentos(listaRendimentos_MySQL: List<OperacaoFinanceira>, listaRendimentos_SQLite: List<OperacaoFinanceira>) {

        val IDusuario = DadosUsuario_BD_Debts(context).pegarIdUsuario()

        val listaNovosGastos: MutableList<OperacaoFinanceira> = mutableListOf()

        for (rendimento in listaRendimentos_MySQL) {
            // Comparar apenas o ID da meta
            if (listaRendimentos_SQLite.none { it.id == rendimento.id }) {
                listaNovosGastos.add(rendimento)
            }
        }

        listaNovosGastos.forEach { rendimento ->

            val idRendimento = rendimento.id
            val nomeRendimento = rendimento.descricao

            // formatando a data
            val dataRendimento = rendimento.data
            val dataRendimentoFormatada = formatarDataString(dataRendimento)

            val tipoMovimento = rendimento.tipo_movimento
            val valorRendimento = rendimento.valor.toFloat()


            BancoDados(context).salvarRendimento(tipoMovimento, dataRendimentoFormatada, valorRendimento, IDusuario, idRendimento)

        }

        listaNovosGastos.clear()

        Log.d("RESULTADO RENDIMENTOS", "Novos Rendimentos SQLite")
    }
}