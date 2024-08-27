package com.example.debts.layoutExpandivel

import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout

class removerListaItems {
    companion object {
        //função que remove a lista de items do campo quando ele for fechado
        fun removerListaItems(campo: ConstraintLayout) {
            // Verificar se há um LinearLayout dentro do parentLayout
            var existeLinearLayout = false

            //procura no layout pai por um linear layout
            for (i in 0 until campo.childCount) {
                val child = campo.getChildAt(i)
                if (child is LinearLayout) {
                    existeLinearLayout = true
                    break // Se encontrou, não precisa continuar procurando
                }
            }

            if (existeLinearLayout) {
                campo.removeViewAt(campo.childCount - 1)
            }
        }
    }
}