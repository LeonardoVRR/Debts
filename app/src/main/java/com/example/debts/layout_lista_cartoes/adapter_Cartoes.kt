package com.example.debts.layout_lista_cartoes

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
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.debts.API_Flask.Flask_Consultar_MySQL
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.ConsultaBD_MySQL.CompararListas_MySQL_SQLite
import com.example.debts.CustomToast
import com.example.debts.MsgCarregando.MensagemCarregando
import com.example.debts.R
import com.example.debts.lista_DebtMap.adapter_DebtMap
import com.example.debts.lista_DebtMap.adapter_DebtMap.MyViewHolder
import com.example.debts.tela_RelatorioGastos
import org.threeten.bp.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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
        holder.cd_cartao.id = item.cd_cartao

        holder.btn_extratoCartao.setOnClickListener {
            var cartaoHabilitado: Boolean = false
            var resposta = ""

            val msgCarregando = MensagemCarregando(context)

            msgCarregando.mostrarMensagem()

            // Usando ExecutorService para tarefas em segundo plano
            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {

                    val numeroCartao = holder.cd_cartao.id

                    DadosUsuario_BD_Debts.cartaoSelecionado.numeroCartao = numeroCartao

                    val cpf_usuario = DadosUsuario_BD_Debts(context).pegarCPFUsuario()

                    // Fazendo uma nova consulta ao banco de dados
                    val cartaoHabilitado_Op_fin =
                        Flask_Consultar_MySQL(context).habilitado_open_finance(cpf_usuario, numeroCartao)

                    Log.d("Open Finance Verificar", "CPF: $cpf_usuario, Num. Cartao: ${holder.cd_cartao.id}, Habilitado Open Finance: $cartaoHabilitado_Op_fin")

                    // Verifica se há novas metas no BD MySQL
                    if (cartaoHabilitado_Op_fin) {
                        cartaoHabilitado = true

                        DadosUsuario_BD_Debts.listas_MySQL.gastosUsuario = Flask_Consultar_MySQL(context).extratoCartao(numeroCartao, DadosUsuario_BD_Debts(context).pegarCPFUsuario())
                    } else {
                        cartaoHabilitado = false

                        resposta = "Cartão não habilitado no Open Finance!"
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(
                        "Open Finance Verificar",
                        "Erro ao executar consulta: ${e.message}"
                    )
                } finally {

                    msgCarregando.ocultarMensagem()

                    // Handler para rodar na UI thread
                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        // Atualizações de UI ou Toast devem ocorrer aqui
                        if (cartaoHabilitado) {
                            val navegarTelaRelatorioGastos = Intent(context, tela_RelatorioGastos::class.java)
                            context.startActivity(navegarTelaRelatorioGastos)

                            // Se context for Activity, chama finish()
                            if (context is Activity) {
                                context.finish()
                            }

                        } else {
                            CustomToast().showCustomToast(
                                context,
                                resposta
                            )
                        }
                    }
                    executorService.shutdown()
                }
            }
        }
    }

    //Este método retorna o número total de itens na lista, o que informa ao RecyclerView quantos itens ele deve exibir.
    override fun getItemCount(): Int = items.size

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val txt_dsOperadora: TextView = view.findViewById(R.id.ds_operadora)
        val txt_tpCartao: TextView = view.findViewById(R.id.tp_cartao)
        val btn_extratoCartao: Button = view.findViewById(R.id.btn_extratoCartao)
        val cd_cartao: TextView = view.findViewById(R.id.cd_cartao)
    }
}