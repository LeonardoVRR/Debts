package com.example.debts.ManipularData

class ManipularData {

    fun pegarNomeMes(mes: Int): String{
        return when (mes) {
            1 -> "janeiro"
            2 -> "fevereiro"
            3 -> "março"
            4 -> "abril"
            5 -> "maio"
            6 -> "junho"
            7 -> "julho"
            8 -> "agosto"
            9 -> "setembro"
            10 -> "outubro"
            11 -> "novembro"
            12 -> "dezembro"
            else -> throw IllegalArgumentException("Número do mês inválido")
        }
    }

    fun pegarNumeroMes(mes: String): String {
        return when (mes.lowercase()) {
            "janeiro" -> "01"
            "fevereiro" -> "02"
            "março" -> "03"
            "abril" -> "04"
            "maio" -> "05"
            "junho" -> "06"
            "julho" -> "07"
            "agosto" -> "08"
            "setembro" -> "09"
            "outubro" -> "10"
            "novembro" -> "11"
            "dezembro" -> "12"
            else -> throw IllegalArgumentException("Nome do mês inválido")
        }
    }
}