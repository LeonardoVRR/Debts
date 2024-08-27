package com.example.debts.layoutExpandivel

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.debts.R
import com.example.debts.layout_Item_lista.ItemSpacingDecoration
import com.example.debts.layout_Item_lista.MyConstraintAdapter

class criarListaItems(private val context: Context) {

    //função que cria a lista de items para cada campo
    // Cria um novo LinearLayout que vai conter o RecyclerView que sera a lista de items dos campos
    fun criarListaItems(campo: ConstraintLayout, adapter: RecyclerView.Adapter<*>){

        //Cria um novo RecyclerView que vai ser a lista dos items
        val recyclerView = RecyclerView(context).apply {

            id = View.generateViewId() // Gera um ID único para o RecyclerView

            //----------------------- config. Criação dos Itens nas Listas -----------------------//

            // Configurar o RecyclerView

            layoutManager = LinearLayoutManager(context)
            // Define o layout manager do RecyclerView. O LinearLayoutManager organiza os itens da lista
            // de forma linear, um abaixo do outro (ou horizontalmente, se configurado), neste caso, verticalmente.

            //configurando o espaçamento entre os itens
            this.addItemDecoration(ItemSpacingDecoration())

            if (campo.id == R.id.lytExp_Entradas){
                this.adapter = adapter
                // Define o adaptador para o RecyclerView. O adaptador é responsável por conectar os dados (neste caso, a lista de "items")
                // com o layout de cada item na lista. O MyConstraintAdapter recebe a lista "items" e vincula os dados aos elementos de interface de cada item.
            }

            else if (campo.id == R.id.lytExp_Despesas){
                this.adapter = adapter
                // Define o adaptador para o RecyclerView. O adaptador é responsável por conectar os dados (neste caso, a lista de "items")
                // com o layout de cada item na lista. O MyConstraintAdapter recebe a lista "items" e vincula os dados aos elementos de interface de cada item.
            }

            else {
                this.adapter = adapter
                // Define o adaptador para o RecyclerView. O adaptador é responsável por conectar os dados (neste caso, a lista de "items")
                // com o layout de cada item na lista. O MyConstraintAdapter recebe a lista "items" e vincula os dados aos elementos de interface de cada item.
            }

            //--------------------- fim config. Criação dos Itens nas Listas ---------------------//

            // Define as LayoutParams para o RecyclerView
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,  // Largura igual à largura do pai
                ConstraintLayout.LayoutParams.MATCH_PARENT   // Altura igual à altura do pai
            ).apply {
                //definindo as margins do RecyclerView
                setMargins(20, 10, 20, 10)

                height = 0
            }

        }

        // Adiciona o LinearLayout ao ConstraintLayout do campo ativo
        campo.addView(recyclerView)

        // Configura as constraints para o LinearLayout dentro do ConstraintLayout
        val constraintSet = ConstraintSet() // Cria um ConstraintSet para definir e aplicar as constraints
        constraintSet.clone(campo) // Clona o campo(ConstraintLayout) atual para aplicar novas constraints
        constraintSet.connect(recyclerView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 20)
        constraintSet.connect(recyclerView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 20)
        constraintSet.connect(recyclerView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 10)

        if (campo.id == R.id.lytExp_Entradas){
            constraintSet.connect(recyclerView.id, ConstraintSet.TOP, R.id.txt_valorEntradas, ConstraintSet.BOTTOM, 10)
        }

        else if (campo.id == R.id.lytExp_Despesas){
            constraintSet.connect(recyclerView.id, ConstraintSet.TOP, R.id.txt_valorDespesas, ConstraintSet.BOTTOM, 10)
        }

        else {
            constraintSet.connect(recyclerView.id, ConstraintSet.TOP, R.id.txt_valorGastos, ConstraintSet.BOTTOM, 10)
        }

        constraintSet.applyTo(campo) // Aplica as constraints ao campo(ConstraintLayout) atual

    }
}