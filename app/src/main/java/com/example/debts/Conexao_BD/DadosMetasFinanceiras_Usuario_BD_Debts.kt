package com.example.debts.Conexao_BD


import com.example.debts.layout_Item_lista.MyData
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import com.example.debts.lista_DebtMap.dados_listaMeta_Item_DebtMap

class DadosMetasFinanceiras_Usuario_BD_Debts {

    fun converter_Lista_MetasFinanceiras(listaMetas: List<String>): List<dados_listaMeta_Item_DebtMap> {

        var listaMetasItens: MutableList<dados_listaMeta_Item_DebtMap> = mutableListOf()

        listaMetas.forEach { meta ->
            // Adiciona um novo item à lista 'items' com os valores formatados
            listaMetasItens.add(
                dados_listaMeta_Item_DebtMap(
                    meta
                )
            )
        }

        return listaMetasItens
    }

    fun criarItemDebtMap(nomeMeta: String, dataCriacaoMeta:String, listaMetas_Item: List<dados_listaMeta_Item_DebtMap>): List<dados_listaMeta_DebtMap> {
        val itemDebtMap: MutableList<dados_listaMeta_DebtMap> = mutableListOf()

        itemDebtMap.add(
            dados_listaMeta_DebtMap(nomeMeta, dataCriacaoMeta, listaMetas_Item)
        )

        // Convertendo MutableList para List
        val itemDebtMapList: List<dados_listaMeta_DebtMap> = itemDebtMap.toList()

        return itemDebtMapList
    }

}