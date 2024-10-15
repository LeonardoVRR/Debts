package com.example.debts.API_Flask

class FormatarNumero {

    fun formatarValorParaNumero(valorFormatado: String): Double {
        // Remove os símbolos de moeda e outros caracteres não numéricos
        val valorLimpo = valorFormatado
            .replace("R$", "")   // Remove o símbolo de real
            .replace(".", "")    // Remove separadores de milhar
            .replace(",", ".")   // Substitui a vírgula pelo ponto para a parte decimal
            .trim()

        // Converte a string limpa para um valor numérico
        return valorLimpo.toDoubleOrNull() ?: 0.0
    }

}