package com.example.debts.SomarValoresCampo

import com.example.debts.layout_Item_lista.OperacaoFinanceira

class Somar {
    //função para somar o valor total dos gastos ou custos do campo
    fun valoresCampo(listaItems: List<OperacaoFinanceira> = emptyList()): Float {

        // Mapeia cada item para o valor numérico após remover caracteres de formatação
        val valorTotal = listaItems
            .map {
                it.valor
                    .replace("R$", "")  // Remove símbolo de moeda
                    .replace(".", "")   // Remove separador de milhares
                    .replace(",", ".") // Substitui vírgula por ponto decimal
                    .trim()
                    .toFloat()
            }
            .sum()  // Soma todos os valores

        return valorTotal
    }
}