package com.example.debts.layout_Item_lista

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.debts.R

class MyConstraintAdapter(private val items: List<OperacaoFinanceira>):
    RecyclerView.Adapter<MyConstraintAdapter.MyViewHolder>() {

    //Este método é chamado quando o RecyclerView precisa criar uma nova ViewHolder. Ele infla o layout do item e cria um novo ViewHolder.
    //Isso cria a estrutura de View que será usada para cada item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lyt_item_lista, parent, false)
        return MyViewHolder(view)
    }

    //Este método é chamado pelo RecyclerView para vincular os dados do item (na posição position) às Views no ViewHolder.
    //Este método é chamado pelo RecyclerView para vincular os dados do item às Views no activity_item_layout.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.txt_infoGasto.text = item.descricao                // Define o texto da Descrição da Compra
        holder.txt_tpTransacao.text = item.tipo_movimento          // Define o texto da forma de pagamento
        holder.txt_ValorGasto.text = item.valor                // Define o texto do valor compra
        holder.txt_dtGasto.text = item.data                  // Define o texto da data da compra
    }

    //Este método retorna o número total de itens na lista, o que informa ao RecyclerView quantos itens ele deve exibir.
    override fun getItemCount(): Int = items.size

    //O construtor do ViewHolder recebe uma View (o layout do item) e armazena as referências aos componentes dentro do layout
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_infoGasto: TextView = view.findViewById(R.id.txt_infoGasto)
        val txt_tpTransacao: TextView = view.findViewById(R.id.txt_tpTransacao)
        val txt_ValorGasto: TextView = view.findViewById(R.id.txt_ValorGasto)
        val txt_dtGasto: TextView = view.findViewById(R.id.txt_dtGasto)
    }
}
