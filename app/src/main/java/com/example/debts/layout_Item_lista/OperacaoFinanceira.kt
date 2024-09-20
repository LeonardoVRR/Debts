package com.example.debts.layout_Item_lista

//define o formato dos dados dos items de um RecyclerView
data class OperacaoFinanceira(
    val id: Int,
    val descricao: String,
    val tipo_movimento: String,
    val valor: String,
    val data: String
)
