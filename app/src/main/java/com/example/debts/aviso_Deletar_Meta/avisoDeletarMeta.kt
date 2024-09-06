package com.example.debts.aviso_Deletar_Meta

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.CustomToast
import com.example.debts.R

class avisoDeletarMeta(private val context: Context, private val lista_Meta_ID:String, private val IDusuario: Int, private val nomeMeta: String) {
    // Configurando a função que vai exibir a mensagem de aviso ao clicar em "Deletar Meta"
    @SuppressLint("MissingInflatedId")
    fun AvisoDeletarMeta() {

        // Inflar o layout personalizado usando o LayoutInflater do contexto
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.activity_layout_aviso_exclusao_meta, null)

        // Constroi o dialog/pop-up
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        // Criar o dialog/pop-up
        val dialog: AlertDialog = builder.create()

        // Acessar os botões do layout inflado usando dialogView.findViewById
        val btnConfirmarExclusao: Button = dialogView.findViewById(R.id.btn_ConfirmarExclusaoMeta)
        val btnCancelarExclusao: Button = dialogView.findViewById(R.id.btn_CancelarExclusaoMeta)
        val txt_nomeMetaEcluir: TextView = dialogView.findViewById(R.id.txt_nomeMetaEcluir)

        txt_nomeMetaEcluir.text = nomeMeta

        // Configurar ações para os botões
        btnConfirmarExclusao.setOnClickListener {
            //CustomToast().showCustomToast(context, "Meta excluída com sucesso.")
            dialog.dismiss()

            BancoDados(context).excluirMeta(IDusuario, lista_Meta_ID)

            // Isso faz com que a atividade atual seja destruída e recriada, essencialmente funcionando como um "refresh" completo da atividade.
            context as Activity
            context.recreate()
        }

        btnCancelarExclusao.setOnClickListener {
            //CustomToast().showCustomToast(context, "Exclusão de meta cancelada.")
            dialog.dismiss()
        }

        // Exibir o diálogo
        dialog.show()
    }
}