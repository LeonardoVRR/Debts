package com.example.debts.lista_DebtMap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.debts.R
import com.example.debts.layoutExpandivel.criarListaItems
import com.example.debts.layoutExpandivel.removerListaItems
import com.example.debts.layout_Item_lista.MyConstraintAdapter
import com.example.debts.layout_Item_lista.MyConstraintAdapter.MyViewHolder
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class adapter_DebtMap(private val items: List<dados_listaMeta_DebtMap>, private val context: Context): RecyclerView.Adapter<adapter_DebtMap.MyViewHolder>(){

    private var listaMeta_isExpanded = false
    var existeRecyclerView = false

    //Este método é chamado quando o RecyclerView precisa criar uma nova ViewHolder. Ele infla o layout do item e cria um novo ViewHolder.
    //Isso cria a estrutura de View que será usada para cada item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adapter_DebtMap.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lyt_item_debtmap, parent, false)
        return com.example.debts.lista_DebtMap.adapter_DebtMap.MyViewHolder(view)
    }

    //Este método é chamado pelo RecyclerView para vincular os dados do item (na posição position) às Views no ViewHolder.
    //Este método é chamado pelo RecyclerView para vincular os dados do item às Views no activity_item_layout.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.txt_NomeMeta.text = item.nomeMeta
        holder.txt_dtCriacaoMeta.text = item.dataCriacaoMeta

        // Obtém os LayoutParams do ConstraintLayout
        val lyt_Item_DebtMap_Params = holder.lyt_Item_DebtMap.layoutParams

        //configurando o layout manager
        holder.lista_Metas_Item_DebtMap.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        holder.lista_Metas_Item_DebtMap.setHasFixedSize(true)
        holder.lista_Metas_Item_DebtMap.isNestedScrollingEnabled = false // Desative o scroll aninhado

        // Crie o adaptador para o RecyclerView
        val adapter = adapter_Item_DebtMap(item.listaMetas_Item, holder.circularProgressBar_ItemDebtMap, holder.txt_IndicadorProgresso_ItemDebtMap)

        //adicionando os items na lista
        holder.lista_Metas_Item_DebtMap.adapter = adapter

        holder.btn_Detalhes_ItemDebtMap.setOnClickListener {

            if (listaMeta_isExpanded) {
                // mostra a lista de items do campo
                holder.lista_Metas_Item_DebtMap.visibility = View.VISIBLE

            } else {
                // escode a lista de items do campo
                holder.lista_Metas_Item_DebtMap.visibility = View.GONE
            }

            holder.lista_Metas_Item_DebtMap.requestLayout()
            holder.lyt_Item_DebtMap.requestLayout()

            listaMeta_isExpanded = !listaMeta_isExpanded
        }
    }

    //Este método retorna o número total de itens na lista, o que informa ao RecyclerView quantos itens ele deve exibir.
    override fun getItemCount(): Int = items.size

    //O construtor do ViewHolder recebe uma View (o layout do item) e armazena as referências aos componentes dentro do layout
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_NomeMeta: TextView = view.findViewById(R.id.txt_NomeMeta)
        val txt_dtCriacaoMeta: TextView = view.findViewById(R.id.txt_dtCriacaoMeta)
        val btn_Detalhes_ItemDebtMap: Button = view.findViewById(R.id.btn_Detalhes_ItemDebtMap)
        val lyt_Item_DebtMap: ConstraintLayout = view.findViewById(R.id.lyt_Item_DebtMap)
        val circularProgressBar_ItemDebtMap: CircularProgressBar = view.findViewById(R.id.circularProgressBar_ItemDebtMap)
        val txt_IndicadorProgresso_ItemDebtMap: TextView = view.findViewById(R.id.txt_IndicadorProgresso_ItemDebtMap)
        val lista_Metas_Item_DebtMap: RecyclerView = view.findViewById(R.id.lista_Metas_Item_DebtMap)
    }
}