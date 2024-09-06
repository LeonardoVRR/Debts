package com.example.debts.Conexao_BD

import android.content.Context
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.layout_Item_lista.MyData

class DadosFinanceiros_Usuario_BD_Debts(private val context: Context, private val IDusuario: Int) {

    fun pegarListaEntradasMes(): List<MyData> {
        val listaEntradas = BancoDados(context).listaRendimentosMes(IDusuario)

        return listaEntradas
    }

    fun pegarListaDespesasMes(): List<MyData> {
        val listaDespesas = listOf(
            MyData("Shopping ABC", "Dinheiro", 58.99.toString(), "02/06/2023"),
            MyData("Shopping ABC", "Dinheiro", 78.85.toString(), "02/06/2023")
        )

        return listaDespesas
    }

    fun pegarListaGastosMes(): List<MyData> {
        val listaGastos = BancoDados(context).listaGastosMes(IDusuario)

        return listaGastos
    }

    fun pegarListaGastosRecentes(): List<MyData> {
        val listaGastosRecentes = BancoDados(context).listaGastosRecentes(IDusuario)

        return listaGastosRecentes
    }
}