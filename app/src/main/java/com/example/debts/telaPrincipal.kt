package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.BD_MySQL_App.Metodos_BD_MySQL
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.FormatarNome.FormatarNome
import com.example.debts.MsgCarregando.MensagemCarregando
import com.example.debts.databinding.ActivityCadastrarBinding
import com.example.debts.databinding.ActivityTelaPrincipalBinding
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

        var login: Boolean = DadosUsuario_BD_Debts(this@telaPrincipal).verificarEstadoLogin()

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

        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        var resultado = ""

        if (!login){
            DadosUsuario_BD_Debts(this).salvarEstadoLogin(true)

            val msgCarregando = MensagemCarregando(this)

            msgCarregando.mostrarMensagem()

            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {

                    resultado = Metodos_BD_MySQL().clonarUsuario_MySQL_para_SQLite(IDusuario, this)

                } catch (e: Exception) {
                    e.printStackTrace()
                    resultado = "Erro ao se conectar: ${e.message}"
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


    }

    //configurando o evento de click no botão do Questionario
    fun telaBalanco(v: View){
        val navegartelaBalanco = Intent(this, telaAdicionarRendimentos::class.java)
        startActivity(navegartelaBalanco)
        finish()
    }

    //configurando o evento de click no botão do DebtMap
    fun telaDebtMap(v: View) {

        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        var resultado = ""

        val msgCarregando = MensagemCarregando(this)

        msgCarregando.mostrarMensagem()

        val executorService: ExecutorService = Executors.newSingleThreadExecutor()
        executorService.execute {
            try {

                resultado = Metodos_BD_MySQL().clonarListaMetas_MySQL_para_SQLite(IDusuario, this)

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

    //configurando o evento de click no botão do perfil do usuario
    fun telaPerfilUsuario(v: View) {
        val navegartelaPerfilUsuario = Intent(this, telaPerfilUsuario::class.java)
        startActivity(navegartelaPerfilUsuario)
        finish()
    }

    //configurando o evento de click no botão do Relatorio Gastos
    fun teleRelatorioGastos(v: View) {
        val navegarTelaRelatorioGastos = Intent(this, tela_RelatorioGastos::class.java)
        startActivity(navegarTelaRelatorioGastos)
        finish()
    }


}