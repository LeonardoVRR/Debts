package com.example.debts.ConsultaBD_MySQL

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.debts.BD_MySQL_App.Metodos_BD_MySQL
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.CustomToast
import org.threeten.bp.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BroadcastReceiver_ConsultarLista : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val IDusuario = DadosUsuario_BD_Debts(context).pegarIdUsuario()
        val nomeAlarme = intent.getStringExtra("nomeAlarme")

        if (nomeAlarme == "listaMetas") {

            // Ação a ser executada quando o alarme for disparado
            CustomToast().showCustomToast(context, "Consulta Meta realizada!")

            // Pegando o timestamp da última consulta
            val ultimaConsultaListaMetas: LocalDateTime = DadosUsuario_BD_Debts(context).getLastUpdateTimestamp_ListaMySQL("Metas")

            var metasNovas: Boolean = false

            // Usando ExecutorService para tarefas em segundo plano
            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {
                    // Fazendo uma nova consulta ao banco de dados
                    val novaConsultaListaMetas: LocalDateTime = Metodos_BD_MySQL().getUltimaAtualizacaoListas_MySQL(IDusuario, "metas_financeiras")

                    // Verifica se há novas metas no BD MySQL
                    if (novaConsultaListaMetas > ultimaConsultaListaMetas) {
                        // Atualiza a lista de metas
                        DadosUsuario_BD_Debts.listas_MySQL.metasUsuario = Metodos_BD_MySQL().listarMetas(IDusuario, context)

                        // Atualiza o timestamp da última consulta
                        DadosUsuario_BD_Debts(context).setLastUpdateTimestamp_ListaMySQL(novaConsultaListaMetas, "Metas")

                        metasNovas = true

                        // Log para indicar sucesso
                        Log.d("BroadcastReceiver_Metas", "Lista de metas atualizada com sucesso.")
                    } else {
                        metasNovas = false

                        Log.d("BroadcastReceiver_Metas", "Nenhuma nova meta encontrada.")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("BroadcastReceiver_Metas", "Erro ao executar consulta: ${e.message}")
                } finally {
                    // Handler para rodar na UI thread
                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        // Atualizações de UI ou Toast devem ocorrer aqui
                        if (metasNovas) {
                            CompararListas_MySQL_SQLite(context).adicionarNovasMetas(DadosUsuario_BD_Debts.listas_MySQL.metasUsuario, BancoDados(context).listarMetas(IDusuario))
                            CustomToast().showCustomToast(context, "Novas metas disponíveis!")
                        } else {
                            CustomToast().showCustomToast(context, "Nenhuma nova meta.")
                        }
                    }
                    executorService.shutdown()
                }
            }
        }

    }
}