package com.example.debts.lista_DebtMap

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.debts.R
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class adapter_Item_DebtMap(private val items: List<dados_listaMeta_Item_DebtMap> = emptyList(), var indicadorProgresso: CircularProgressBar, var txt_IndicadorProgresso: TextView): RecyclerView.Adapter<adapter_Item_DebtMap.MyViewHolder>() {

    private var progressoAtual_IndicadorProgresso: Float = 0f

    fun obterIndicadorProgressoCircular() {
        txt_IndicadorProgresso.text = "${String.format("%.0f", progressoAtual_IndicadorProgresso)}%" //formatado o texto do indicador de progresso
        indicadorProgresso.apply {
            progressMax = 100f //define o tamanho max do indicador de progresso
            setProgressWithAnimation(progressoAtual_IndicadorProgresso, 1000) //indica o progresso
        }
    }

    //Este método é chamado quando o RecyclerView precisa criar uma nova ViewHolder. Ele infla o layout do item e cria um novo ViewHolder.
    //Isso cria a estrutura de View que será usada para cada item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adapter_Item_DebtMap.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lyt_item_lista_meta_debtmap, parent, false)
        return com.example.debts.lista_DebtMap.adapter_Item_DebtMap.MyViewHolder(view)
    }

    //Este método é chamado pelo RecyclerView para vincular os dados do item (na posição position) às Views no ViewHolder.
    //Este método é chamado pelo RecyclerView para vincular os dados do item às Views no activity_item_layout.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]

        holder.checkBox_Meta_Item.text = item.nomeMeta

        //configurando o click no checkBox
        holder.checkBox_Meta_Item.setOnClickListener{

            if(holder.checkBox_Meta_Item.isChecked){
                //risca o texto se o checkBox tiver sido marcado
                holder.checkBox_Meta_Item.paintFlags = holder.checkBox_Meta_Item.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                //colorindo o texto de preto com 50% de tranparencia da cor
                holder.checkBox_Meta_Item.setTextColor(Color.argb(128, 0, 0, 0))

                //faz o indicador de progresso aumentar conforme os checkBox forem marcados
                progressoAtual_IndicadorProgresso += 100f / getItemCount().toFloat()
            }

            else {
                //faz o indicador de progresso diminuir conforme os checkBox forem desmarcados
                progressoAtual_IndicadorProgresso -= 100f / getItemCount().toFloat()

                //colorindo o texto de preto
                holder.checkBox_Meta_Item.setTextColor(Color.argb(255, 0, 0, 0))
                //tira o risco do texto
                holder.checkBox_Meta_Item.paintFlags = holder.checkBox_Meta_Item.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            obterIndicadorProgressoCircular()
        }
    }

    //Este método retorna o número total de itens na lista, o que informa ao RecyclerView quantos itens ele deve exibir.
    override fun getItemCount(): Int = items.size

    //O construtor do ViewHolder recebe uma View (o layout do item) e armazena as referências aos componentes dentro do layout
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox_Meta_Item: CheckBox = view.findViewById(R.id.checkBox_Meta_Item)
    }
}