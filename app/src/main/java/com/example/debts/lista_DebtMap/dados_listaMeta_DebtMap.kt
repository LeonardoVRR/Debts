package com.example.debts.lista_DebtMap

//define o formato dos dados dos items do DebtMap
data class dados_listaMeta_DebtMap (
    val idMeta: String,
    val nomeMeta: String,
    val progressoMeta: Float,
    val dataCriacaoMeta:String,
    val listaMetas_Item: List<dados_listaMeta_Item_DebtMap>
)