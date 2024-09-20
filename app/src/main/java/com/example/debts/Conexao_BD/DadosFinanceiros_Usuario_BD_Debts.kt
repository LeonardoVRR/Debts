package com.example.debts.Conexao_BD

import android.content.Context
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.layout_Item_lista.OperacaoFinanceira

class DadosFinanceiros_Usuario_BD_Debts(private val context: Context, private val IDusuario: Int) {

    fun pegarListaEntradasMes(): List<OperacaoFinanceira> {
        val listaEntradas = BancoDados(context).listaRendimentosMes(IDusuario)

        return listaEntradas
    }

//    fun pegarListaDespesasMes(): List<OperacaoFinanceira> {
//        val listaDespesas = listOf(
//            OperacaoFinanceira("Shopping ABC", "Dinheiro", 58.99.toString(), "02/06/2023"),
//            OperacaoFinanceira("Shopping ABC", "Dinheiro", 78.85.toString(), "02/06/2023")
//        )
//
//        return listaDespesas
//    }

    fun pegarListaGastosMes(): List<OperacaoFinanceira> {
        val listaGastos = BancoDados(context).listaGastosMes(IDusuario)

        return listaGastos
    }

    fun pegarListaGastosRecentes(): List<OperacaoFinanceira> {
        val listaGastosRecentes = BancoDados(context).listaGastosRecentes(IDusuario)

        return listaGastosRecentes
    }
}