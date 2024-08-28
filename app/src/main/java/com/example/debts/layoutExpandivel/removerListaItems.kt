package com.example.debts.layoutExpandivel

import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class removerListaItems {
    companion object {
        //função que remove a lista de items do campo quando ele for fechado
        fun removerListaItems(campo: ConstraintLayout) {
            // Verificar se há um RecyclerView dentro do parentLayout
            var existeRecyclerView = false

            //procura no layout pai por um RecyclerView
            for (i in 0 until campo.childCount) {
                val child = campo.getChildAt(i)
                if (child is RecyclerView) {
                    existeRecyclerView = true
                    break // Se encontrou, não precisa continuar procurando
                }
            }

            if (existeRecyclerView) {
                campo.removeViewAt(campo.childCount - 1)
            }
        }
    }
}