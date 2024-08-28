package com.example.debts.lista_DebtMap

import java.io.Serializable

//define o formato dos dados dos items do DebtMap
data class dados_listaMeta_DebtMap (
    val nomeMeta: String,
    val dataCriacaoMeta:String,
    val listaMetas_Item: List<dados_listaMeta_Item_DebtMap>
): Serializable