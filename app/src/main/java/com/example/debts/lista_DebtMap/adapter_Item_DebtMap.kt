package com.example.debts.lista_DebtMap

//import android.content.Context
//import android.graphics.Color
//import android.graphics.Paint
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.CheckBox
//import android.widget.ImageButton
//import android.widget.TextView
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.recyclerview.widget.RecyclerView
//import com.example.debts.BD_SQLite_App.BancoDados
//import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
//import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts.listaMetaEstados
//import com.example.debts.R
//import com.mikhaellopez.circularprogressbar.CircularProgressBar

//class adapter_Item_DebtMap(private val items: List<dados_listaMeta_Item_DebtMap> = emptyList(), var indicadorProgresso: CircularProgressBar, var txt_IndicadorProgresso: TextView, val context: Context, val lista_Meta_ID:String, val btn_ExcluirMeta: ImageButton): RecyclerView.Adapter<adapter_Item_DebtMap.MyViewHolder>() {
//
//    private val IDusuario = DadosUsuario_BD_Debts(context).pegarIdUsuario()
//    private var progressoAtual_IndicadorProgresso: Float = BancoDados(context).pegarProgressoAtualMeta(IDusuario, lista_Meta_ID)
//    private var listaEstadoMetas = DadosUsuario_BD_Debts(context).pegarListaEstadosMetas(IDusuario, lista_Meta_ID)
//
//    init {
//        // Atualiza o gráfico circular logo após o adaptador ser inicializado
//        obterIndicadorProgressoCircular()
//    }
//
//    fun obterIndicadorProgressoCircular() {
//        txt_IndicadorProgresso.text = "${String.format("%.0f", progressoAtual_IndicadorProgresso)}%" //formatado o texto do indicador de progresso
//        indicadorProgresso.apply {
//            progressMax = 100f //define o tamanho max do indicador de progresso
//            setProgressWithAnimation(progressoAtual_IndicadorProgresso, 1000) //indica o progresso
//        }
//    }
//
//    fun riscarMeta(item: CheckBox, checked: Boolean) {
//        if (checked) {
//            //risca o texto se o checkBox tiver sido marcado
//            item.paintFlags = item.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//            //colorindo o texto de preto com 50% de tranparencia da cor
//            item.setTextColor(Color.argb(128, 0, 0, 0))
//        }
//
//        else {
//            //colorindo o texto de preto
//            item.setTextColor(Color.argb(255, 0, 0, 0))
//            //tira o risco do texto
//            item.paintFlags = item.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
//        }
//    }
//
//
//    //Este método é chamado quando o RecyclerView precisa criar uma nova ViewHolder. Ele infla o layout do item e cria um novo ViewHolder.
//    //Isso cria a estrutura de View que será usada para cada item.
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adapter_Item_DebtMap.MyViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.lyt_item_lista_meta_debtmap, parent, false)
//        return com.example.debts.lista_DebtMap.adapter_Item_DebtMap.MyViewHolder(view)
//    }
//
//    //Este método é chamado pelo RecyclerView para vincular os dados do item (na posição position) às Views no ViewHolder.
//    //Este método é chamado pelo RecyclerView para vincular os dados do item às Views no activity_item_layout.
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val item = items[position]
//
//        holder.checkBox_Meta_Item.text = item.nomeMeta
//        holder.checkBox_Meta_Item.isChecked = listaEstadoMetas.getOrElse(position) {false}
//
//        // Risca a meta se a checkbox estiver marcada
//        riscarMeta(holder.checkBox_Meta_Item, holder.checkBox_Meta_Item.isChecked)
//
//        //configurando o click no checkBox
//        holder.checkBox_Meta_Item.setOnCheckedChangeListener { _, isChecked ->
//            listaEstadoMetas[position] = isChecked
//
//            riscarMeta(holder.checkBox_Meta_Item, listaEstadoMetas[position])
//
//            if (holder.checkBox_Meta_Item.isChecked) {
//                //faz o indicador de progresso aumentar conforme os checkBox forem marcados
//                progressoAtual_IndicadorProgresso += 100f / getItemCount().toFloat()
//            }
//
//            else {
//                //faz o indicador de progresso diminuir conforme os checkBox forem desmarcados
//                progressoAtual_IndicadorProgresso -= 100f / getItemCount().toFloat()
//            }
//
//            //configurando a visibilidade do botão excluir meta
//            if (progressoAtual_IndicadorProgresso == 100f) {
//
//                //quando a meta chegar a 100% vai aparecer o botão excluir meta
//                btn_ExcluirMeta.visibility = View.VISIBLE
//            }
//
//            else {
//                //quando a meta for menor que 100% vai desaparecer o botão excluir meta
//                btn_ExcluirMeta.visibility = View.GONE
//            }
//
//            //Log.d("EstadoCheckBox", "$listaEstadoMetas")
//
//            obterIndicadorProgressoCircular()
//
//            BancoDados(context).salvarEstadoMetas(IDusuario, listaEstadoMetas, lista_Meta_ID, progressoAtual_IndicadorProgresso)
//        }
//
//    }
//
//    //Este método retorna o número total de itens na lista, o que informa ao RecyclerView quantos itens ele deve exibir.
//    override fun getItemCount(): Int = items.size
//
//    //O construtor do ViewHolder recebe uma View (o layout do item) e armazena as referências aos componentes dentro do layout
//    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val checkBox_Meta_Item: CheckBox = view.findViewById(R.id.checkBox_Meta_Item)
//    }
//}