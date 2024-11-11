package com.example.debts.layout_lista_cartoes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.R
import com.example.debts.lista_DebtMap.adapter_DebtMap
import com.example.debts.lista_DebtMap.adapter_DebtMap.MyViewHolder

class adapter_Cartoes(private val items: List<dados_listaCartao>, private val context: Context): RecyclerView.Adapter<adapter_Cartoes.MyViewHolder>() {

    private val IDusuario = DadosUsuario_BD_Debts(context).pegarIdUsuario()

    //Este método é chamado quando o RecyclerView precisa criar uma nova ViewHolder. Ele infla o layout do item e cria um novo ViewHolder.
    //Isso cria a estrutura de View que será usada para cada item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adapter_Cartoes.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lyt_item_lista_cartoes, parent, false)
        return com.example.debts.layout_lista_cartoes.adapter_Cartoes.MyViewHolder(view)
    }

    //Este método é chamado pelo RecyclerView para vincular os dados do item (na posição position) às Views no ViewHolder.
    //Este método é chamado pelo RecyclerView para vincular os dados do item às Views no activity_item_layout.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.txt_dsOperadora.text = item.ds_operadora
        holder.txt_tpCartao.text = item.tp_cartao

        holder.btn_extratoCartao.setOnClickListener {

        }
    }

    //Este método retorna o número total de itens na lista, o que informa ao RecyclerView quantos itens ele deve exibir.
    override fun getItemCount(): Int = items.size

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val txt_dsOperadora: TextView = view.findViewById(R.id.ds_operadora)
        val txt_tpCartao: TextView = view.findViewById(R.id.tp_cartao)
        val btn_extratoCartao: Button = view.findViewById(R.id.btn_extratoCartao)
    }
}