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

        else if (nomeAlarme == "listaGastos") {

            // Ação a ser executada quando o alarme for disparado
            CustomToast().showCustomToast(context, "Consulta Gasto realizada!")

            // Pegando o timestamp da última consulta
            val ultimaConsultaListaGasto: LocalDateTime = DadosUsuario_BD_Debts(context).getLastUpdateTimestamp_ListaMySQL("Gastos")

            var gastosNovos: Boolean = false

            // Usando ExecutorService para tarefas em segundo plano
            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {
                    // Fazendo uma nova consulta ao banco de dados
                    val novaConsultaListaGastos: LocalDateTime = Metodos_BD_MySQL().getUltimaAtualizacaoListas_MySQL(IDusuario, "Gastos")

                    // Verifica se há novas metas no BD MySQL
                    if (novaConsultaListaGastos > ultimaConsultaListaGasto) {
                        // Atualiza a lista de metas
                        DadosUsuario_BD_Debts.listas_MySQL.gastosUsuario = Metodos_BD_MySQL().listaGastos(IDusuario)

                        // Atualiza o timestamp da última consulta
                        DadosUsuario_BD_Debts(context).setLastUpdateTimestamp_ListaMySQL(novaConsultaListaGastos, "Gastos")

                        gastosNovos = true

                        // Log para indicar sucesso
                        Log.d("BroadcastReceiver_Gastos", "Lista de gastos atualizada com sucesso.")
                    } else {
                        gastosNovos = false

                        Log.d("BroadcastReceiver_Gastos", "Nenhuma novo gasto encontrada.")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("BroadcastReceiver_Gastos", "Erro ao executar consulta: ${e.message}")
                } finally {
                    // Handler para rodar na UI thread
                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        // Atualizações de UI ou Toast devem ocorrer aqui
                        if (gastosNovos) {
                            CompararListas_MySQL_SQLite(context).adicionarNovosGastos(DadosUsuario_BD_Debts.listas_MySQL.gastosUsuario, BancoDados(context).listaGastosMes(IDusuario))
                            CustomToast().showCustomToast(context, "Novos gastos disponíveis!")
                        } else {
                            CustomToast().showCustomToast(context, "Nenhuma novo gasto.")
                        }
                    }
                    executorService.shutdown()
                }
            }
        }

        else if (nomeAlarme == "listaRendimentos") {

            // Ação a ser executada quando o alarme for disparado
            CustomToast().showCustomToast(context, "Consulta Rendimento realizada!")

            // Pegando o timestamp da última consulta
            val ultimaConsultaListaRendimentos: LocalDateTime = DadosUsuario_BD_Debts(context).getLastUpdateTimestamp_ListaMySQL("Rendimentos")

            var rendimentosNovos: Boolean = false

            // Usando ExecutorService para tarefas em segundo plano
            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {
                    // Fazendo uma nova consulta ao banco de dados
                    val novaConsultaListaRendimentos: LocalDateTime = Metodos_BD_MySQL().getUltimaAtualizacaoListas_MySQL(IDusuario, "Rendimentos")

                    // Verifica se há novas metas no BD MySQL
                    if (novaConsultaListaRendimentos > ultimaConsultaListaRendimentos) {
                        // Atualiza a lista de metas
                        DadosUsuario_BD_Debts.listas_MySQL.rendimentosUsuario = Metodos_BD_MySQL().listaRendimentos(IDusuario)

                        // Atualiza o timestamp da última consulta
                        DadosUsuario_BD_Debts(context).setLastUpdateTimestamp_ListaMySQL(novaConsultaListaRendimentos, "Rendimentos")

                        rendimentosNovos = true

                        // Log para indicar sucesso
                        Log.d("BroadcastReceiver_Rendimentos", "Lista de rendimentos atualizada com sucesso.")
                    } else {
                        rendimentosNovos = false

                        Log.d("BroadcastReceiver_Rendimentos", "Nenhuma novo rendimento encontrado.")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("BroadcastReceiver_Rendimentos", "Erro ao executar consulta: ${e.message}")
                } finally {
                    // Handler para rodar na UI thread
                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        // Atualizações de UI ou Toast devem ocorrer aqui
                        if (rendimentosNovos) {
                            CompararListas_MySQL_SQLite(context).adicionarNovosRendimentos(DadosUsuario_BD_Debts.listas_MySQL.rendimentosUsuario, BancoDados(context).listaRendimentosMes(IDusuario))
                            CustomToast().showCustomToast(context, "Novos Rendimentos disponíveis!")
                        } else {
                            CustomToast().showCustomToast(context, "Nenhuma novo Rendimentos.")
                        }
                    }
                    executorService.shutdown()
                }
            }
        }

    }
}