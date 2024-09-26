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
import com.example.debts.Config_Notificacoes.NotificationHelper
import com.example.debts.ConsultaBD_MySQL.AgendarConsulta_MySQL
import com.example.debts.ConsultaBD_MySQL.CompararListas_MySQL_SQLite
import com.example.debts.FormatarNome.FormatarNome
import com.example.debts.MsgCarregando.MensagemCarregando
import org.threeten.bp.LocalDateTime
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

        //BancoDados(this).acessarBancoDados()

        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        var resultado = ""

        if (!estadoLogin){
            DadosUsuario_BD_Debts(this).salvarEstadoLogin(true)

            val msgCarregando = MensagemCarregando(this)

            msgCarregando.mostrarMensagem()

            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {

                    //salva o tempo da ultima consulta a lista metas do BD MySQL
                    val salvarConsultaListaMetas_MySQL: LocalDateTime = Metodos_BD_MySQL().getUltimaAtualizacaoListas_MySQL(IDusuario, "metas_financeiras")
                    DadosUsuario_BD_Debts(this).setLastUpdateTimestamp_ListaMySQL(salvarConsultaListaMetas_MySQL, "Metas")

                    //salva o tempo da ultima consulta a lista gastos do BD MySQL
                    val salvarConsultaListaGasto_MySQL: LocalDateTime = Metodos_BD_MySQL().getUltimaAtualizacaoListas_MySQL(IDusuario, "gastos")
                    DadosUsuario_BD_Debts(this).setLastUpdateTimestamp_ListaMySQL(salvarConsultaListaGasto_MySQL, "Gastos")

                    //salva o tempo da ultima consulta a lista rendimentos do BD MySQL
                    val salvarConsultaListaRendimento_MySQL: LocalDateTime = Metodos_BD_MySQL().getUltimaAtualizacaoListas_MySQL(IDusuario, "rendimentos")
                    DadosUsuario_BD_Debts(this).setLastUpdateTimestamp_ListaMySQL(salvarConsultaListaRendimento_MySQL, "Rendimentos")


                    //salvando a lista de metas
                    DadosUsuario_BD_Debts.listas_MySQL.metasUsuario = Metodos_BD_MySQL().listarMetas(IDusuario, this)
                    //salvando a lista de gastos
                    DadosUsuario_BD_Debts.listas_MySQL.gastosUsuario = Metodos_BD_MySQL().listaGastos(IDusuario)
                    //salvando a lista de rendimentos
                    DadosUsuario_BD_Debts.listas_MySQL.rendimentosUsuario = Metodos_BD_MySQL().listaRendimentos(IDusuario)

                    Log.d("Lista Rendimentos SQLite", "${BancoDados(this).listaRendimentosMes(IDusuario)}")
                    Log.d("Lista Rendimentos MySQL", "${DadosUsuario_BD_Debts.listas_MySQL.rendimentosUsuario}")


                    resultado = "Dados carregados com sucesso!"

                } catch (e: Exception) {
                    e.printStackTrace()
                    resultado = "Erro ao se conectar: ${e.message}"
                    Log.d("Erro ao se conectar", "${e.message}")
                } finally {

                    // Atualizar a UI no thread principal
                    runOnUiThread {
                        msgCarregando.ocultarMensagem()

                        // ativa um alarme para fazer consultas ao BD na Tabela metas de 1 min em 1 min
                        AgendarConsulta_MySQL(this).agendarAlarmeConsultaLista("listaMetas", 1, 60)

                        // ativa um alarme para fazer consultas ao BD na Tabela Gastos de 30seg em 30seg
                        AgendarConsulta_MySQL(this).agendarAlarmeConsultaLista("listaGastos", 2, 30)

                        // ativa um alarme para fazer consultas ao BD na Tabela Gastos de 30seg em 30seg
                        AgendarConsulta_MySQL(this).agendarAlarmeConsultaLista("listaRendimentos", 3, 30)


                        //lista Metas
                        CompararListas_MySQL_SQLite(this).adicionarNovasMetas(DadosUsuario_BD_Debts.listas_MySQL.metasUsuario, BancoDados(this).listarMetas(IDusuario))

                        //lista Gastos
                        CompararListas_MySQL_SQLite(this).adicionarNovosGastos(DadosUsuario_BD_Debts.listas_MySQL.gastosUsuario, BancoDados(this).listaGastosMes(IDusuario))

                        //lista Rendimentos
                        CompararListas_MySQL_SQLite(this).adicionarNovosRendimentos(DadosUsuario_BD_Debts.listas_MySQL.rendimentosUsuario, BancoDados(this).listaRendimentosMes(IDusuario))

                        CustomToast().showCustomToast(this, resultado)
                    }

                    executorService.shutdown()
                }
            }
        }

        //----------------------- config. os botões p/ navegação nas telas -----------------------------------//

        val btn_DebtMap: Button = findViewById(R.id.btn_DebtMap)

        btn_DebtMap.setOnClickListener { telaDebtMap() }

        val btn_RelatorioGastos: Button = findViewById(R.id.btn_RelatorioGastos)

        //btn_RelatorioGastos.setOnClickListener { teleRelatorioGastos() }

        btn_RelatorioGastos.setOnClickListener {
            NotificationHelper(this).criarCanal()
            NotificationHelper(this).enviarNotificacao("Cuidado", "Você está gastando muito!")
        }

    }

    //configurando o evento de click no botão do Questionario
    fun telaBalanco(v: View){
        val navegartelaBalanco = Intent(this, telaAdicionarRendimentos::class.java)
        startActivity(navegartelaBalanco)
        finish()
    }

    //configurando o evento de click no botão do DebtMap
    fun telaDebtMap() {
//        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()
//        val listaMeta_SQLite = BancoDados(this).listarMetas(IDusuario)

        val navegartelaDebtMap = Intent(this, tela_DebtMap::class.java)
        startActivity(navegartelaDebtMap)
        finish()
    }

    //configurando o evento de click no botão do perfil do usuario
    fun telaPerfilUsuario(v: View) {
        val navegartelaPerfilUsuario = Intent(this, telaPerfilUsuario::class.java)
        startActivity(navegartelaPerfilUsuario)
        finish()
    }

    //configurando o evento de click no botão do Relatorio Gastos
    fun teleRelatorioGastos() {
        val navegarTelaRelatorioGastos = Intent(this, tela_RelatorioGastos::class.java)
        startActivity(navegarTelaRelatorioGastos)
        finish()
    }


}