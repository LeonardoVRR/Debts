package com.example.debts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.BD_MySQL_App.ConnectionClass
import com.example.debts.BD_MySQL_App.Metodos_BD_MySQL
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.ConsultaBD_MySQL.AgendarConsulta_MySQL
import com.example.debts.MsgCarregando.MensagemCarregando
import com.example.debts.visibilidadeSenha.AlterarVisibilidade
import java.sql.Connection
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    var context: Context = this
    lateinit var usuarioLogado: String

    lateinit var connectionClass: ConnectionClass

    var con: Connection? = null

    lateinit var str: String

    lateinit var msg_login: String

    //configurando o crud no BD
//    private var userDB = DadosUsuario_BD_Debts().pegarNomeUsuario()
//    private var senhaDB = DadosUsuario_BD_Debts().pegarSenhaUsuario()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cancela os alarmes
        AgendarConsulta_MySQL(this).cancelarAlarme("listaMetas", 1)
        AgendarConsulta_MySQL(this).cancelarAlarme("listaGastos", 2)
        AgendarConsulta_MySQL(this).cancelarAlarme("listaRendimentos", 3)

        //definindo o estado do login do usuário
        DadosUsuario_BD_Debts(this).salvarEstadoLogin(false)

        connectionClass = ConnectionClass()

        //chamando a função para acessar o BD
        BancoDados(this).copyDatabase()
        //BancoDados(this).acessarBancoDados()

        //configurando o click do botão logar

        val btn_logar: Button = findViewById(R.id.btn_login)

        //btn_logar.setOnClickListener { login() }

        //configurando o botão de icone da senha para mudar quando for clicado
        val btn_IconeSenha: ImageButton = findViewById(R.id.btn_visibilidadeSenhaLogin)
        val campoSenha: EditText = findViewById(R.id.input_senhaLogin)

        btn_IconeSenha.setOnClickListener { AlterarVisibilidade(campoSenha, btn_IconeSenha).verSenha() }

    }

    fun criarConta(v: View) {
        val navegarCadastrarConta = Intent(this, criarConta::class.java)
        startActivity(navegarCadastrarConta)
        finish()
    }

    fun conectarMySQL(v: View) {
        val executorService: ExecutorService = Executors.newSingleThreadExecutor()
        executorService.execute {
            try {
                con = connectionClass.CONN()

                str = if (con == null) {
                    "Erro ao se conectar ao MySQL server"
                } else {
                    "Conectado ao MySQL server"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                str = "Erro ao se conectar: ${e.message}"
            }

            // Atualizar a UI no thread principal
            runOnUiThread {
                Toast.makeText(this, str, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun verificarCampos(v: View) {
        val inputNome: EditText = findViewById(R.id.input_nomeUsuarioLogin)
        val inputSenha: EditText = findViewById(R.id.input_senhaLogin)

        val limparEntradaNome = inputNome.text.toString().lowercase().trim()
        val limparEntradaSenha = inputSenha.text.toString().trim()

        //verificando se o nome de usuario ou senha conferem estão vazios
        if (limparEntradaSenha.isEmpty() || limparEntradaNome.isEmpty()) {

            CustomToast().showCustomToast(this, "Preencha todos os campos.")
        }

        else {
            validarLogin_MySQL(limparEntradaNome, limparEntradaSenha)
        }
    }

    fun validarLogin_MySQL(nome: String, senha: String) {

        val msgCarregando = MensagemCarregando(this)

        msgCarregando.mostrarMensagem()

        val executorService: ExecutorService = Executors.newSingleThreadExecutor()
        executorService.execute {
            try {

                val login = Metodos_BD_MySQL().validarLogin(nome, senha)

                //verifica se a conta existe para fazer o login
                if (login){

                    //nome do usuario logado
                    usuarioLogado = nome

                    //msg_login = "Usuario: $usuarioLogado logado."

                    msg_login = "Usuário logado com sucesso."

                    //salva o nome do usuario logado
                    DadosUsuario_BD_Debts(this).salvarUsuarioLogado(usuarioLogado)
                    //conexao.salvarDadosUsuario(usuarioLogado)

                    val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

                    val questionarioPreenchido = Metodos_BD_MySQL().verificarQuestionario(IDusuario)

                    if (questionarioPreenchido) {
                        val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
                        startActivity(navegarTelaPrincipal)
                        finish()
                    }

                    else {
                        val navegarTelaQuestionario = Intent(this, tela_Consulta_IA::class.java)
                        startActivity(navegarTelaQuestionario)
                        finish()
                    }
                }

                else {
                    msg_login = "Usuario ou Senha incorreto."
                }

            } catch (e: Exception) {
                e.printStackTrace()
                str = "Erro ao se conectar: ${e.message}"
            } finally {

                // Atualizar a UI no thread principal
                runOnUiThread {
                    msgCarregando.ocultarMensagem()
                    Log.d("lista dados SALVOS", "${Metodos_BD_MySQL.dadosUsuario.listaDados}")
                    CustomToast().showCustomToast(this, msg_login)
                }

                executorService.shutdown()
            }
        }
    }

    // Cancela os alarmes quando o app for fechado
    override fun onDestroy() {
        super.onDestroy()

        AgendarConsulta_MySQL(this).cancelarAlarme("listaMetas", 1)
        AgendarConsulta_MySQL(this).cancelarAlarme("listaGastos", 2)
        AgendarConsulta_MySQL(this).cancelarAlarme("listaRendimentos", 3)

    }

}