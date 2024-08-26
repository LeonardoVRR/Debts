package com.example.debts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast

class CustomToast {
    fun showCustomToast(context: Context, message: String) {
        // Obtém o LayoutInflater
        val inflater = LayoutInflater.from(context)

        // Infla o layout personalizado do Toast
        val layout: View = inflater.inflate(R.layout.toast_layout, null)

        // Personaliza o texto do Toast
        val textView: TextView = layout.findViewById(R.id.toast_text)
        textView.text = message

        // Personaliza o ícone do Toast
        //val imageView: ImageView = layout.findViewById(R.id.toast_icon)
        //imageView.setImageResource(iconResId)  // Define o ícone com o recurso passado

        // Cria e configura o Toast
        val toast = Toast(context.applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout  // Define o layout personalizado
        toast.show()
    }

}