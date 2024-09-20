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
import com.example.debts.ConsultaBD_MySQL.AgendarConsulta_MySQL
import com.example.debts.ConsultaBD_MySQL.CompararListas_MySQL_SQLite
import com.example.debts.FormatarNome.FormatarNome
import com.example.debts.MsgCarregando.MensagemCarregando
import com.example.debts.layout_Item_lista.OperacaoFinanceira
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

                AgendarConsulta_MySQL(this@telaPrincipal).cancelarAlarme()

                startActivity(voltarTelaLogin)
                finish()
            }
        })

        //------------ config. salvar dados do usurio do MySQL p/ o SQLite -----------------------//

        BancoDados(this).acessarBancoDados()

        //var listaMeta_MySQL: List<dados_listaMeta_DebtMap> = listOf()
        var listaRendimentos_MySQL: List<OperacaoFinanceira> = listOf()
        var listaGastos_MySQL: List<OperacaoFinanceira> = listOf()

        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        var resultado = ""

        if (!estadoLogin){
            DadosUsuario_BD_Debts(this).salvarEstadoLogin(true)

            val msgCarregando = MensagemCarregando(this)

            msgCarregando.mostrarMensagem()

            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {

                    // ativa um alarme para fazer consultas ao BD na Tabela metas de 30seg em 30seg
                    AgendarConsulta_MySQL(this).agendarAlarmeConsultaLista("listaMetas", 1, 60)

                    //salva o tempo da ultima consulta a lista metas do BD MySQL
                    var salvarConsultaListaMetas_MySQL: LocalDateTime = Metodos_BD_MySQL().getUltimaAtualizacaoListas_MySQL(IDusuario, "metas_financeiras")

                    DadosUsuario_BD_Debts(this).setLastUpdateTimestamp_ListaMySQL(salvarConsultaListaMetas_MySQL, "Metas")

                    //salvando a lista de metas
                    DadosUsuario_BD_Debts.listas_MySQL.metasUsuario = Metodos_BD_MySQL().listarMetas(IDusuario, this)
//                    listaRendimentos_MySQL = Metodos_BD_MySQL().listaRendimentos(IDusuario)
//                    listaGastos_MySQL = Metodos_BD_MySQL().listaGastos(IDusuario)

                    Log.d("Lista Metas SQLite", "${BancoDados(this).listarMetas(IDusuario)}")
                    Log.d("Lista Metas MySQL", "${DadosUsuario_BD_Debts.listas_MySQL.metasUsuario}")


                    resultado = "Dados carregados com sucesso!"

                } catch (e: Exception) {
                    e.printStackTrace()
                    resultado = "Erro ao se conectar: ${e.message}"
                    Log.d("Erro ao se conectar", "${e.message}")
                } finally {

                    // Atualizar a UI no thread principal
                    runOnUiThread {
                        msgCarregando.ocultarMensagem()

                        CompararListas_MySQL_SQLite(this).adicionarNovasMetas(DadosUsuario_BD_Debts.listas_MySQL.metasUsuario, BancoDados(this).listarMetas(IDusuario))

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

        btn_RelatorioGastos.setOnClickListener { teleRelatorioGastos() }

    }

    //configurando o evento de click no botão do Questionario
    fun telaBalanco(v: View){
        val navegartelaBalanco = Intent(this, telaAdicionarRendimentos::class.java)
        startActivity(navegartelaBalanco)
        finish()
    }

    //configurando o evento de click no botão do DebtMap
    fun telaDebtMap() {
        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()
        val listaMeta_SQLite = BancoDados(this).listarMetas(IDusuario)

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