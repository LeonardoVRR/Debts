package com.example.debts.FormatarNome

class FormatarNome {

    //função para formatar o nome do usuario
    fun formatar(nome: String): String {
        return nome.trim()
            .split("\\s+".toRegex())  // Divide a string em palavras, ignorando múltiplos espaços
            .joinToString(" ") { palavra ->
                palavra.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
    }

}