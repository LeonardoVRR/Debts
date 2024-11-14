package com.example.debts.layout_lista_cartoes

import com.example.debts.layout_Item_lista.OperacaoFinanceira

class converter_listaCartoes {
    fun listaConvertida(lista: List<OperacaoFinanceira>): List<dados_listaCartao> {

        var listaCartoes: MutableList<dados_listaCartao> = mutableListOf()

        lista.map { cartao ->
            listaCartoes.add(
                dados_listaCartao(
                    cartao.descricao,
                    cartao.tipo_movimento,
                    cartao.id
                )
            )
        }

        return listaCartoes.toList()
    }
}