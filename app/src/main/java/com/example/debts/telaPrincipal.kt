package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.BD_MySQL_App.Metodos_BD_MySQL
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.FormatarNome.FormatarNome
import com.example.debts.MsgCarregando.MensagemCarregando
import com.example.debts.databinding.ActivityCadastrarBinding
import com.example.debts.databinding.ActivityTelaPrincipalBinding
import com.example.debts.layout_Item_lista.MyData
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class telaPrincipal : AppCompatActivity() {

    //private lateinit var telaPrincipalApp: ActivityTelaPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var estadoLogin: Boolean = DadosUsuario_BD_Debts(this@telaPrincipal).verificarEstadoLogin()

        //configurando a mensagem de saudações para o usuário
        val txt_nomeUsuario: TextView = findViewById(R.id.txt_saudacaoUsuario)

        val nomeUsuario: String = FormatarNome().formatar(DadosUsuario_BD_Debts(this).pegarNomeUsuario())

        txt_nomeUsuario.text = "Bem-Vindo, ${nomeUsuario}"

        //configurando o botão voltar do celular quando for prescionado p/ voltar na tela de login
        val voltarTelaLogin = Intent(this, MainActivity::class.java)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //Log.v("Voltar", "Botão voltar Presscionado")

                startActivity(voltarTelaLogin)
                finish()
            }
        })

        //------------ config. salvar dados do usurio do MySQL p/ o SQLite -----------------------//

        var listaMeta_MySQL: List<dados_listaMeta_DebtMap> = listOf()
        var listaRendimentos_MySQL: List<MyData> = listOf()
        var listaGastos_MySQL: List<MyData> = listOf()

        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        var resultado = ""

        if (!estadoLogin){
            DadosUsuario_BD_Debts(this).salvarEstadoLogin(true)

            val msgCarregando = MensagemCarregando(this)

            msgCarregando.mostrarMensagem()

            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {

                    var salvarConsultaListaMetas_MySQL: LocalDateTime = Metodos_BD_MySQL().getUltimaAtualizacaoMetas(IDusuario)

                    DadosUsuario_BD_Debts(this).setLastUpdateTimestamp_Metas(salvarConsultaListaMetas_MySQL)

                    listaMeta_MySQL = Metodos_BD_MySQL().listarMetas(IDusuario, this)
//                    listaRendimentos_MySQL = Metodos_BD_MySQL().listaRendimentos(IDusuario)
//                    listaGastos_MySQL = Metodos_BD_MySQL().listaGastos(IDusuario)

                    resultado = "Dados carregados com sucesso!"

                } catch (e: Exception) {
                    e.printStackTrace()
                    resultado = "Erro ao se conectar: ${e.message}"
                    Log.d("Erro ao se conectar", "${e.message}")
                } finally {

                    // Atualizar a UI no thread principal
                    runOnUiThread {
                        msgCarregando.ocultarMensagem()
                        CustomToast().showCustomToast(this, resultado)
                    }

                    executorService.shutdown()
                }
            }
        }

        else {
            var ultimaConsultaListaMetas: LocalDateTime = DadosUsuario_BD_Debts(this).getLastUpdateTimestamp_Metas()

            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {

                    val novaConsultaListaMetas: LocalDateTime = Metodos_BD_MySQL().getUltimaAtualizacaoMetas(IDusuario)

                    // Verifica se há novas metas no BD MySQL
                    if (novaConsultaListaMetas > ultimaConsultaListaMetas) {
                        listaMeta_MySQL = Metodos_BD_MySQL().listarMetas(IDusuario, this)

                        var salvarConsultaListaMetas_MySQL: LocalDateTime = Metodos_BD_MySQL().getUltimaAtualizacaoMetas(IDusuario)
                        DadosUsuario_BD_Debts(this).setLastUpdateTimestamp_Metas(salvarConsultaListaMetas_MySQL)
                    }

//                    else if (listaRendimentos_MySQL.size < Metodos_BD_MySQL().listaRendimentos(IDusuario).size) {
//                        listaRendimentos_MySQL = Metodos_BD_MySQL().listaRendimentos(IDusuario)
//                    }
//
//                    else if (listaGastos_MySQL.size < Metodos_BD_MySQL().listaGastos(IDusuario).size) {
//                        listaGastos_MySQL = Metodos_BD_MySQL().listaGastos(IDusuario)
//                    }


                    resultado = "Dados atualizados com sucesso!"

                } catch (e: Exception) {
                    e.printStackTrace()
                    resultado = "Erro ao se conectar: ${e.message}"
                } finally {

                    // Atualizar a UI no thread principal
                    runOnUiThread {
                        CustomToast().showCustomToast(this, resultado)
                    }

                    executorService.shutdown()
                }
            }
        }

        //----------------------- config. os botões p/ navegação nas telas -----------------------------------//

        val btn_DebtMap: Button = findViewById(R.id.btn_DebtMap)

        btn_DebtMap.setOnClickListener { telaDebtMap(listaMeta_MySQL) }

        val btn_RelatorioGastos: Button = findViewById(R.id.btn_RelatorioGastos)

        btn_RelatorioGastos.setOnClickListener { teleRelatorioGastos(listaRendimentos_MySQL, listaGastos_MySQL) }

    }

    //configurando o evento de click no botão do Questionario
    fun telaBalanco(v: View){
        val navegartelaBalanco = Intent(this, telaAdicionarRendimentos::class.java)
        startActivity(navegartelaBalanco)
        finish()
    }

    //configurando o evento de click no botão do DebtMap
    fun telaDebtMap(listaMeta_MySQL: List<dados_listaMeta_DebtMap>) {

        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        val listaMeta_SQLite = BancoDados(this).listarMetas(IDusuario)

        if ((listaMeta_SQLite.size < listaMeta_MySQL.size || listaMeta_SQLite.size > listaMeta_MySQL.size) && listaMeta_MySQL.isNotEmpty()) {

            var resultado = ""

            val msgCarregando = MensagemCarregando(this)

            msgCarregando.mostrarMensagem()

            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {

                    Metodos_BD_MySQL().clonarListaMetas_MySQL_para_SQLite(IDusuario, this)

                    resultado = "Metas clonadas com sucesso!"

                } catch (e: Exception) {
                    e.printStackTrace()

                    Log.e("Erro conexão MySQL", "${e.message}")
                    resultado = "Erro ao se conectar: ${e.message}"
                } finally {

                    // Atualizar a UI no thread principal
                    runOnUiThread {
                        msgCarregando.ocultarMensagem()
                        //CustomToast().showCustomToast(this, resultado)

                        val navegartelaDebtMap = Intent(this, tela_DebtMap::class.java)
                        startActivity(navegartelaDebtMap)
                        finish()
                    }

                    executorService.shutdown()
                }
            }
        }

        else {
            val navegartelaDebtMap = Intent(this, tela_DebtMap::class.java)
            startActivity(navegartelaDebtMap)
            finish()
        }

    }

    //configurando o evento de click no botão do perfil do usuario
    fun telaPerfilUsuario(v: View) {
        val navegartelaPerfilUsuario = Intent(this, telaPerfilUsuario::class.java)
        startActivity(navegartelaPerfilUsuario)
        finish()
    }

    //configurando o evento de click no botão do Relatorio Gastos
    fun teleRelatorioGastos(listaEntradas_MySQL: List<MyData>, listaGastos_MySQL: List<MyData>) {

        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        val listaEntradas_SQLite = BancoDados(this).listaRendimentosMes(IDusuario)
        val listaGastos_SQLite = BancoDados(this).listaGastosMes(IDusuario)

        if ((listaEntradas_SQLite.size < listaEntradas_MySQL.size && listaEntradas_MySQL.isNotEmpty()) || (listaGastos_SQLite.size < listaGastos_MySQL.size && listaGastos_MySQL.isNotEmpty())) {

            var resultado = ""

            val msgCarregando = MensagemCarregando(this)

            msgCarregando.mostrarMensagem()

            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {

                    if (listaEntradas_SQLite.size < listaEntradas_MySQL.size) {
                        Metodos_BD_MySQL().clonarListaRendimentos_MySQL_para_SQLite(IDusuario, this)

                        resultado = "Rendimentos atualizados com sucesso!"
                    }

                    else if (listaGastos_SQLite.size < listaGastos_MySQL.size) {
                        Metodos_BD_MySQL().clonarListaGastos_MySQL_para_SQLite(IDusuario, this)

                        resultado = "Gastos atualizados com sucesso!"
                    }


                } catch (e: Exception) {
                    e.printStackTrace()

                    Log.e("Erro conexão MySQL", "${e.message}")
                    resultado = "Erro ao se conectar: ${e.message}"
                } finally {

                    // Atualizar a UI no thread principal
                    runOnUiThread {
                        msgCarregando.ocultarMensagem()
                        CustomToast().showCustomToast(this, resultado)

                        val navegarTelaRelatorioGastos = Intent(this, tela_RelatorioGastos::class.java)
                        startActivity(navegarTelaRelatorioGastos)
                        finish()
                    }

                    executorService.shutdown()
                }
            }
        }

        else {
            val navegarTelaRelatorioGastos = Intent(this, tela_RelatorioGastos::class.java)
            startActivity(navegarTelaRelatorioGastos)
            finish()
        }
    }


}