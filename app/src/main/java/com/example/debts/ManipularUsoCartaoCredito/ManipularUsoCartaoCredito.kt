package com.example.debts.ManipularUsoCartaoCredito

import android.content.Context
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.debts.CustomToast

class ManipularUsoCartaoCredito(private val painel: EditText, private val context: Context) {

    private val currentNumber = painel.text.toString().toIntOrNull() ?: 0
    private var valorUso: Int = currentNumber

    init {
        painel.addTextChangedListener {
            // Atualiza valorUso com o texto atual do painel
            valorUso = painel.text.toString().toIntOrNull() ?: 0
        }
    }

    fun AumentarUso(btn_aumentar: ImageButton) {

        // Atualiza o valor do painel e do valorUso na thread principal
        painel.post {

            valorUso += 1
            painel.setText(valorUso.toString())
            painel.setSelection(painel.text.length) // Move o cursor para o final
        }

    }

    fun DiminuirUso(btn_diminuir: ImageButton) {
        // Atualiza o valor do painel e do valorUso na thread principal
        painel.post {
            val currentNumber = painel.text.toString().toIntOrNull() ?: 0
            if (valorUso > 0) {
                valorUso -= 1
                painel.setText(valorUso.toString())
                painel.setSelection(painel.text.length) // Move o cursor para o final
            } else {
                // Exibir mensagem se a tentativa de diminuir não for permitida
                CustomToast().showCustomToast(context, "Não é possível diminuir mais.")
            }
        }
    }
}