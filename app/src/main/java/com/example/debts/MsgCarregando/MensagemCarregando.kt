package com.example.debts.MsgCarregando

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import com.example.debts.R

class MensagemCarregando(private val context: Context) {
    private var msgCarregando: Dialog? = null

    fun mostrarMensagem() {
        if (msgCarregando == null) {
            msgCarregando = Dialog(context, R.style.TransparentDialog)
            msgCarregando?.setContentView(R.layout.activity_tela_carregamento)

            msgCarregando?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            msgCarregando?.setCancelable(false) // Evita que o usuário feche o dialog ao clicar fora dele
            msgCarregando?.show()
        }
    }

    fun ocultarMensagem() {
        msgCarregando?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
        msgCarregando = null // Libera o recurso ao finalizar o uso
    }
}