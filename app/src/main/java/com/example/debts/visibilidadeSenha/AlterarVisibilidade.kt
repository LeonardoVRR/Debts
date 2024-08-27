package com.example.debts.visibilidadeSenha

import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import com.example.debts.R

class AlterarVisibilidade(private val campoSenha: EditText, private val iconeSenha: ImageButton) {

    private var visibilidadeSenha = false

    init {
        // Configura o clique no ícone para alternar a visibilidade
        iconeSenha.setOnClickListener {
            verSenha()
        }
    }

    //configurando o botão de icone da senha para mudar quando for clicado
    fun verSenha() {
        if (visibilidadeSenha) {
            // Se a senha estiver visível, ocultar
            visibilidadeSenha = false
            iconeSenha.setImageResource(R.drawable.visibility_off)
            campoSenha.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            // Se a senha estiver oculta, mostrar
            visibilidadeSenha = true
            iconeSenha.setImageResource(R.drawable.visibility)
            campoSenha.inputType = InputType.TYPE_CLASS_TEXT
        }

        // Move o cursor para o final do texto
        campoSenha.setSelection(campoSenha.text.length)
    }

}