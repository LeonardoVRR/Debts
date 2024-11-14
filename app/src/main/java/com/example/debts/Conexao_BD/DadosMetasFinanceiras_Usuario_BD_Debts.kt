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

    //fun converter_listaGastosCartao ()

    fun criarItemDebtMap(idMeta:String, dt_meta_inicio:String, vlr_inicial: Float, perc_meta: Float): List<dados_listaMeta_DebtMap> {
        val itemDebtMap: MutableList<dados_listaMeta_DebtMap> = mutableListOf()

        itemDebtMap.add(
            dados_listaMeta_DebtMap(idMeta, 0, vlr_inicial, perc_meta, dt_meta_inicio)
        )

        // Convertendo MutableList para List
        val itemDebtMapList: List<dados_listaMeta_DebtMap> = itemDebtMap.toList()

        return itemDebtMapList
    }

}