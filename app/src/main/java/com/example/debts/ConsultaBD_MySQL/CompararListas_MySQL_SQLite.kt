package com.example.debts.ConsultaBD_MySQL

import android.content.Context
import android.util.Log
import com.example.debts.API_Flask.Flask_Consultar_MySQL
import com.example.debts.BD_MySQL_App.Metodos_BD_MySQL
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.layout_Item_lista.OperacaoFinanceira
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CompararListas_MySQL_SQLite (private val context: Context) {

    fun formatarDataString(data: String): String {

        val dia = data.split("de")[0].trim()
        val mes = data.split("de")[1].trim()
        val ano = data.split("de")[2].trim()

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

    fun formatarValor(valorString: String): Float {
        // Remover o símbolo de moeda "R$", remover os espaços e os separadores de milhar.
        val valorLimpo = valorString
            .replace("R$", "") // Remove o símbolo "R$"
            .replace(".", "")  // Remove os separadores de milhar
            .replace(",", ".") // Substitui a vírgula decimal por ponto
            .trim()            // Remove espaços em branco extras

        // Converte a string limpa para Float
        return valorLimpo.toFloat()
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
            Log.d("LISTA NOVAS METAS", "$listaMetasNovas")

            listaMetasNovas.forEach { meta ->

                val idMeta = meta.idMeta.toInt()
                val cartao = meta.cartao
                val vlr_inicial = meta.vlr_inicial
                val perc_meta = meta.perc_meta
                val dt_meta_inicio = meta.dt_meta_inicio
                val dt_meta_conclusao = meta.dt_meta_conclusao
                val ramo_meta = meta.ramo_meta

                //Log.d("DATA CRIAÇÂO META", "$dataMeta")

                //val dataMetaFormatada = formatarDataString(dataMeta)

                BancoDados(context).salvarMeta(cartao, vlr_inicial, perc_meta, dt_meta_inicio, dt_meta_conclusao, IDusuario, ramo_meta, idMeta)

                resultado = "Novas Metas SQLite"
            }

            listaMetasNovas.clear()
        }

//        else {
//            val listaRemoverMetas = listaMeta_SQLite.filterNot { sqliteMeta ->
//                listaMeta_MySQL.any { mysqlMeta ->
//                    mysqlMeta.idMeta == sqliteMeta.idMeta
//                }
//            }
//
//            listaRemoverMetas.forEach { item -> BancoDados(context).excluirMeta(IDusuario, item.idMeta) }
//
//            resultado = "Metas Removidas do SQLite"
//        }

        Log.d("RESULTADO METAS", resultado)
    }

//    fun atualizarMetas(listaMeta_MySQL: List<dados_listaMeta_DebtMap>, listaMeta_SQLite: List<dados_listaMeta_DebtMap>): Boolean {
//
//        val listaAtualizarMetas: MutableList<dados_listaMeta_DebtMap> = mutableListOf()
//
//        val IDusuario = DadosUsuario_BD_Debts(context).pegarIdUsuario()
//
//        var metaAtualizada = false
//
//        for (meta in listaMeta_SQLite) {
//            // Comparar o progresso da meta e a lista de itens
//            if (listaMeta_MySQL.any { it.progressoMeta != meta.progressoMeta || it.listaMetas_Item != meta.listaMetas_Item }) {
//                listaAtualizarMetas.add(meta)
//            }
//        }
//
//        if (listaAtualizarMetas.isNotEmpty()) {
//
//            listaAtualizarMetas.forEach { meta ->
//                val idMeta = meta.idMeta.toInt()
//
//                // Converte os estados da lista de metas
//                val listaMetaEstados: MutableList<Boolean> = meta.listaMetas_Item.map { it.isChecked }.toMutableList()
//
//                val progressoMeta = meta.progressoMeta
//
//                Flask_Consultar_MySQL(context).atualizarMeta(
//                    IDusuario,
//                    idMeta,
//                    listaMetaEstados,
//                    progressoMeta
//                )
//
//            }
//
//            metaAtualizada = true
//        } else {
//            metaAtualizada = false
//        }
//
//        Log.d("RESULTADO METAS ATUALIZADAS", "$listaAtualizarMetas")
//
//        listaAtualizarMetas.clear()
//
//        return metaAtualizada
//    }

    fun adicionarNovosGastos(listaGasto_MySQL: List<OperacaoFinanceira>, listaGasto_SQLite: List<OperacaoFinanceira>) {

        val IDusuario = DadosUsuario_BD_Debts(context).pegarIdUsuario()

        val listaNovosGastos: MutableList<OperacaoFinanceira> = mutableListOf()

        for (gasto in listaGasto_MySQL) {
            // Comparar apenas o ID da meta
            if (listaGasto_SQLite.none { it.id == gasto.id }) {
                listaNovosGastos.add(gasto)
            }
        }

        Log.d("LISTA NOVOS GASTOS", "$listaNovosGastos")

        listaNovosGastos.forEach { gasto ->

            val idGasto = gasto.id
            val nomeGasto = gasto.descricao

            // formatando a data
            val dataGasto = gasto.data
//            val dataGastoFormatada = formatarDataString(dataGasto)

            val tipoMovimento = gasto.tipo_movimento
            val valorGasto = formatarValor(gasto.valor)


            BancoDados(context).salvarGasto(nomeGasto, tipoMovimento, valorGasto, dataGasto, IDusuario, idGasto)

        }

        listaNovosGastos.clear()

        Log.d("RESULTADO GASTOS", "Novos Gastos SQLite")
    }

    fun adicionarNovosRendimentos(listaRendimentos_MySQL: List<OperacaoFinanceira>, listaRendimentos_SQLite: List<OperacaoFinanceira>) {

        Log.d("LISTA MySQL", "$listaRendimentos_MySQL")
        Log.d("LISTA SQLite", "$listaRendimentos_SQLite")

        val IDusuario = DadosUsuario_BD_Debts(context).pegarIdUsuario()

        val listaNovosRendimentos: MutableList<OperacaoFinanceira> = mutableListOf()

        for (rendimento in listaRendimentos_MySQL) {
            // Comparar apenas o ID da meta
            if (listaRendimentos_SQLite.none { it.id == rendimento.id }) {
                listaNovosRendimentos.add(rendimento)
            }
        }

        Log.d("LISTA NOVOS RENDIMENTOS", "$listaNovosRendimentos")

        listaNovosRendimentos.forEach { rendimento ->

            val idRendimento = rendimento.id
            val nomeRendimento = rendimento.descricao

            // formatando a data
            val dataRendimento = rendimento.data
            //val dataRendimentoFormatada = formatarDataString(dataRendimento)

            val tipoMovimento = rendimento.tipo_movimento
            val valorRendimento = formatarValor(rendimento.valor)


            BancoDados(context).salvarRendimento(nomeRendimento, dataRendimento, valorRendimento, IDusuario, idRendimento)

        }

        listaNovosRendimentos.clear()

        Log.d("RESULTADO RENDIMENTOS", "Novos Rendimentos SQLite")
    }

    fun adicionarNovosCartoes(listaCartoes_MySQL: List<OperacaoFinanceira>, listaCartoes_SQLite: List<OperacaoFinanceira>) {

        Log.d("LISTA MySQL", "$listaCartoes_MySQL")
        Log.d("LISTA SQLite", "$listaCartoes_SQLite")

        val IDusuario = DadosUsuario_BD_Debts(context).pegarIdUsuario()

        val listaNovosCartoes: MutableList<OperacaoFinanceira> = mutableListOf()

        for (rendimento in listaCartoes_MySQL) {
            // Comparar apenas o ID do cartao
            if (listaCartoes_SQLite.none { it.id == rendimento.id }) {
                listaNovosCartoes.add(rendimento)
            }
        }

        Log.d("LISTA NOVOS RENDIMENTOS", "$listaNovosCartoes")

        listaNovosCartoes.forEach { cartao ->

            val id = cartao.id
            val nome = cartao.descricao

            // formatando a data
            val data = ""
            //val dataRendimentoFormatada = formatarDataString(dataRendimento)

            val tipoMovimento = cartao.tipo_movimento
            val valor = formatarValor(cartao.valor)

            var credito = 0
            var debito = 0
            var saldo = 0f
            var limite = 0f

            if (tipoMovimento == "Credito"){
                credito = 1
                debito = 0

                limite = valor
                saldo = 0f
            }

            else if (tipoMovimento == "Debito"){
                credito = 0
                debito = 1

                limite = 0f
                saldo = valor
            }

            BancoDados(context).salvarCartao(id, nome, credito, debito, saldo, limite, IDusuario)

        }

        listaNovosCartoes.clear()

        Log.d("RESULTADO Cartoes", "Novos Cartoes SQLite")
    }
}