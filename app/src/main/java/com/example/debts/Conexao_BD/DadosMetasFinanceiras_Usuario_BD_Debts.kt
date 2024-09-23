package com.example.debts.Conexao_BD


import android.util.Log
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import com.example.debts.lista_DebtMap.dados_listaMeta_Item_DebtMap

class DadosMetasFinanceiras_Usuario_BD_Debts {

    fun converter_Lista_MetasFinanceiras(listaMetas: List<String>, listaMetasConcluidas: List<Boolean>): List<dados_listaMeta_Item_DebtMap> {

        val listaMetasItens: MutableList<dados_listaMeta_Item_DebtMap> = mutableListOf()

        // Verifica se as duas listas têm o mesmo tamanho
        if (listaMetas.size == listaMetasConcluidas.size) {
            // Itera pelas duas listas simultaneamente
            for (i in listaMetas.indices) {
                listaMetasItens.add(
                    dados_listaMeta_Item_DebtMap(
                        listaMetas[i],
                        listaMetasConcluidas[i]
                    )
                )
            }
        } else {
            // Lida com o caso de listas de tamanhos diferentes (se necessário)
            Log.e("Erro", "As listas de metas e metas concluídas têm tamanhos diferentes.")
        }

        return listaMetasItens
    }


    fun criarItemDebtMap(idMeta:String, nomeMeta: String, progressoMeta: Float, dataCriacaoMeta:String, listaMetas_Item: List<dados_listaMeta_Item_DebtMap>): List<dados_listaMeta_DebtMap> {
        val itemDebtMap: MutableList<dados_listaMeta_DebtMap> = mutableListOf()

        itemDebtMap.add(
            dados_listaMeta_DebtMap(idMeta, nomeMeta, progressoMeta, dataCriacaoMeta, listaMetas_Item)
        )

        // Convertendo MutableList para List
        val itemDebtMapList: List<dados_listaMeta_DebtMap> = itemDebtMap.toList()

        return itemDebtMapList
    }

}