package com.example.debts.Conexao_BD

import com.example.debts.layout_Item_lista.MyData

class DadosFinanceiros_Usuario_BD_Debts {

    fun pegarListaEntradasMes(): List<MyData> {
        val listaEntradas = listOf(
            MyData("Salario", "PIX", 1200.toString(), "25/08/2024"),
            MyData("Venda Monitor", "PIX", 2000.toString(), "05/03/2020")
        )

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
        val listaGastos = listOf(
            MyData("Shopping ElDorado", "Crédito", 200.5.toString(), "17/02/2022"),
            MyData("McDonalds", "Crédito", 60.5.toString(), "17/02/2021")
        )

        return listaGastos
    }
}