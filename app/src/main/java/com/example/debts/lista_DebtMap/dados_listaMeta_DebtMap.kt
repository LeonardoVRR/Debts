package com.example.debts.lista_DebtMap

//define o formato dos dados dos items do DebtMap
data class dados_listaMeta_DebtMap (
    val idMeta: String,
    val cartao: Int = 0,
    val vlr_inicial: Float,
    val perc_meta: Float,
    val dt_meta_inicio:String,
    var dt_meta_conclusao: String = "",
    val ramo_meta: Int = 0
)