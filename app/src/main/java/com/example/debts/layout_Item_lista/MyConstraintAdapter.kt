package com.example.debts.layout_Item_lista

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.debts.R

class MyConstraintAdapter(private val items: List<MyData>) :
    RecyclerView.Adapter<MyConstraintAdapter.MyViewHolder>() {

        //O construtor do ViewHolder recebe uma View (o layout do item) e armazena as referências aos componentes dentro do layout
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val Descr_Compra: TextView = view.findViewById(R.id.Descr_Compra)
        val forma_pagamento: TextView = view.findViewById(R.id.forma_pagamento)
        val valor_compra: TextView = view.findViewById(R.id.valor_compra)
        val data_compra: TextView = view.findViewById(R.id.data_compra)
    }

    //Este método é chamado quando o RecyclerView precisa criar uma nova ViewHolder. Ele infla o layout do item e cria um novo ViewHolder.
    //Isso cria a estrutura de View que será usada para cada item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_lista, parent, false)
        return MyViewHolder(view)
    }

    //Este método é chamado pelo RecyclerView para vincular os dados do item (na posição position) às Views no ViewHolder.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.Descr_Compra.text = item.Descr_Compra                // Define o texto da Descrição da Compra
        holder.forma_pagamento.text = item.forma_pagamento          // Define o texto da forma de pagamento
        holder.valor_compra.text = item.valor_compra                // Define o texto do valor compra
        holder.data_compra.text = item.data_compra                  // Define o texto da data da compra
    }

    //Este método retorna o número total de itens na lista, o que informa ao RecyclerView quantos itens ele deve exibir.
    override fun getItemCount(): Int = items.size
}
