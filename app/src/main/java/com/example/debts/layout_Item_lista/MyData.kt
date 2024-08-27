package com.example.debts.layout_Item_lista

import java.io.Serializable

//define o formato dos dados dos items de um RecyclerView
data class MyData(
    val Descr_Compra: String,
    val forma_pagamento: String,
    val valor_compra: String,
    val data_compra: String
):Serializable
