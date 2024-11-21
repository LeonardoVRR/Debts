package com.example.debts.lista_DebtMap

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.example.debts.API_Flask.Flask_Consultar_MySQL
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
//import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts.listaMetaEstados
import com.example.debts.ConsultaBD_MySQL.CompararListas_MySQL_SQLite
import com.example.debts.CustomToast
import com.example.debts.FormatarMoeda.formatarReal
import com.example.debts.MainActivity
import com.example.debts.R
import com.example.debts.aviso_Deletar_Meta.avisoDeletarMeta
import com.example.debts.layoutExpandivel.criarListaItems
import com.example.debts.layoutExpandivel.removerListaItems
import com.example.debts.layout_Item_lista.MyConstraintAdapter
import com.example.debts.layout_Item_lista.MyConstraintAdapter.MyViewHolder
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import org.threeten.bp.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class adapter_DebtMap(private val items: List<dados_listaMeta_DebtMap>, private val context: Context): RecyclerView.Adapter<adapter_DebtMap.MyViewHolder>(){

    private val IDusuario = DadosUsuario_BD_Debts(context).pegarIdUsuario()
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
        holder.txt_dtCriacaoMeta.text = item.dt_meta_inicio
        holder.lista_Meta_ID.text = item.idMeta
        holder.txt_perc_meta.text = formatarReal().formatarParaReal(item.perc_meta)
        holder.txt_valor_inicial.text = formatarReal().formatarParaReal(item.vlr_inicial)
        holder.txt_meta_conclusao.text = item.dt_meta_conclusao.split(" ")[1]

        // Obtém os LayoutParams do ConstraintLayout
        val lyt_Item_DebtMap_Params = holder.lyt_Item_DebtMap.layoutParams

        //configurando o layout manager
//        holder.lista_Metas_Item_DebtMap.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        holder.lista_Metas_Item_DebtMap.setHasFixedSize(true)
//        holder.lista_Metas_Item_DebtMap.isNestedScrollingEnabled = false // Desative o scroll aninhado

        if (holder.txt_meta_conclusao.text != "00:00:00") {
            holder.btn_ExcluirMeta.visibility = View.VISIBLE
            holder.btnConcluirMeta.visibility = View.GONE
        }

        else {
            holder.btnConcluirMeta.visibility = View.VISIBLE
            holder.btn_ExcluirMeta.visibility = View.GONE
        }

        //configurando o click do botão excluir meta
        holder.btn_ExcluirMeta.setOnClickListener {
            avisoDeletarMeta(context, holder.lista_Meta_ID.text.toString(), IDusuario).AvisoDeletarMeta()
        }

        holder.btnConcluirMeta.setOnClickListener {

            var resultado = ""
            var hr_meta_conclusao = ""

            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {

                    val statusMeta = Flask_Consultar_MySQL(context).atualizarMeta(IDusuario, holder.lista_Meta_ID.text.toString().toInt())

                    Log.d("Meta Status", statusMeta["status"].toString())

                    if (statusMeta["status"].toString() == "Metas atualizadas com sucesso.") {
                        hr_meta_conclusao = statusMeta["hr_conclusaoMeta"].toString()
                        resultado = "Metas atualizadas com sucesso."
                    }

                    else {
                        hr_meta_conclusao = "00:00:00"
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(
                        "Atualizar Meta",
                        "Erro ao executar consulta: ${e.message}"
                    )
                } finally {
                    // Handler para rodar na UI thread
                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        CustomToast().showCustomToast(
                            context,
                            resultado
                        )

                        if (resultado == "Metas atualizadas com sucesso.") {
                            BancoDados(context).MetasConcluidas(IDusuario, holder.lista_Meta_ID.text.toString(), hr_meta_conclusao)

                            // Isso faz com que a atividade atual seja destruída e recriada, essencialmente funcionando como um "refresh" completo da atividade.
                            context as Activity
                            context.recreate()
                        }
                    }
                    executorService.shutdown()
                }
            }
        }

        //configurando o click do botão detalhes
//        holder.btn_Detalhes_ItemDebtMap.setOnClickListener {
//
//            //obtendo o progresso atual da meta
//            var progressoAtual_IndicadorProgresso: Float = BancoDados(context).pegarProgressoAtualMeta(IDusuario, holder.lista_Meta_ID.text.toString())
//
//
//            //Log.d("ID Metas", "ID antigo: $id_meta_antigo, ID atual: $id_meta_atual")
//
//            if (listaMeta_isExpanded) {
//                // mostra a lista de items do campo
//                holder.lista_Metas_Item_DebtMap.visibility = View.VISIBLE
//
//                if (progressoAtual_IndicadorProgresso == 100f) {
//                    holder.btn_ExcluirMeta.visibility = View.VISIBLE
//                }
//
//                else {
//                    holder.btn_ExcluirMeta.visibility = View.GONE
//                }
//
//            } else {
//                Log.d("Lista de Estados Metas", "${listaMetaEstados.estados}")
//
//                // escode a lista de items do campo
//                holder.lista_Metas_Item_DebtMap.visibility = View.GONE
//                holder.btn_ExcluirMeta.visibility = View.GONE
//            }
//
//            holder.lista_Metas_Item_DebtMap.requestLayout()
//            holder.lyt_Item_DebtMap.requestLayout()
//
//            listaMeta_isExpanded = !listaMeta_isExpanded
//        }

        // Crie o adaptador para o RecyclerView
        //val adapter = adapter_Item_DebtMap(item.listaMetas_Item, holder.circularProgressBar_ItemDebtMap, holder.txt_IndicadorProgresso_ItemDebtMap, context, holder.lista_Meta_ID.text.toString(), holder.btn_ExcluirMeta)

//        //adicionando os items na lista
//        holder.lista_Metas_Item_DebtMap.adapter = adapter
    }

    //Este método retorna o número total de itens na lista, o que informa ao RecyclerView quantos itens ele deve exibir.
    override fun getItemCount(): Int = items.size

    //O construtor do ViewHolder recebe uma View (o layout do item) e armazena as referências aos componentes dentro do layout
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_dtCriacaoMeta: TextView = view.findViewById(R.id.txt_dtCriacaoMeta)
        val lyt_Item_DebtMap: ConstraintLayout = view.findViewById(R.id.lyt_Item_DebtMap)
        val lista_Meta_ID: TextView = view.findViewById(R.id.txt_id_Meta)
        val btnConcluirMeta: ImageButton = view.findViewById(R.id.btn_concluirMeta)
        val btn_ExcluirMeta: ImageButton = view.findViewById(R.id.btn_ExcluirMeta)
        val txt_valor_inicial: TextView = view.findViewById(R.id.txt_gastoInicial)
        val txt_perc_meta: TextView = view.findViewById(R.id.txt_perc_meta)
        val txt_meta_conclusao: TextView = view.findViewById(R.id.txt_dt_meta_conclusao)
    }
}