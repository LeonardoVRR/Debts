package com.example.debts.FormatarMoeda

import java.text.NumberFormat
import java.util.Locale

class formatarReal {

    fun formatarParaReal(valor: Float): String {
        val formatoBrasileiro = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return formatoBrasileiro.format(valor)
    }
}
