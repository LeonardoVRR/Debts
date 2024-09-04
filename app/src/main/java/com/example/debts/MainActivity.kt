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
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.ConexaoBD
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.Conexao_BD.conexaoBD_Debts
import com.example.debts.Conexao_BD.conexaoBanco_Debts
import com.example.debts.visibilidadeSenha.AlterarVisibilidade
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    var context: Context = this
    lateinit var usuarioLogado: String

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

        //chamando a função para acessar o BD
        //BancoDados(this).copyDatabase()
        //BancoDados(this).acessarBancoDados()

        //configurando o click do botão logar

        val btn_logar: Button = findViewById(R.id.btn_login)

        btn_logar.setOnClickListener { login() }

        //configurando o botão de icone da senha para mudar quando for clicado
        val btn_IconeSenha: ImageButton = findViewById(R.id.btn_visibilidadeSenhaLogin)
        val campoSenha: EditText = findViewById(R.id.input_senhaLogin)

        btn_IconeSenha.setOnClickListener { AlterarVisibilidade(campoSenha, btn_IconeSenha).verSenha() }

    }

    //configurando a navegação para a tela principal
    fun login() {

        val inputNome: EditText = findViewById(R.id.input_nomeUsuarioLogin)
        val inputSenha: EditText = findViewById(R.id.input_senhaLogin)

        val limparEntradaNome = inputNome.text.toString().lowercase().trim()
        val limparEntradaSenha = inputSenha.text.toString().trim()

        //verificando se o nome de usuario e senha conferem com os que foram salvos no BD
        if (limparEntradaSenha.isEmpty() && limparEntradaNome.isEmpty()) {

            CustomToast().showCustomToast(this, "Preencha todos os campos.")
        }

        else {

            //verifica se a conta existe para fazer o login
            if (BancoDados(this).validarLogin(limparEntradaNome, limparEntradaSenha)){

                //nome do usuario logado
                usuarioLogado = limparEntradaNome

                DadosUsuario_BD_Debts(this).salvarUsuarioLogado(limparEntradaNome)

                val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
                startActivity(navegarTelaPrincipal)
                finish()
            }

            else {
                CustomToast().showCustomToast(this, "Usuario ou Senha incorreto.")
            }
        }

    }

    fun criarConta(v: View) {
        val navegarCadastrarConta = Intent(this, criarConta::class.java)
        startActivity(navegarCadastrarConta)
        finish()
    }

    fun autenticacaoLogin(v: View) {
        val inputNome: EditText = findViewById(R.id.input_nomeUsuarioLogin)
        val inputSenha: EditText = findViewById(R.id.input_senhaLogin)

        val limparEntradaNome = inputNome.text.toString().lowercase().trim()
        val limparEntradaSenha = inputSenha.text.toString().trim()

        //var usu = usuarioDAO().selecionarUsuario(limparEntradaNome, limparEntradaSenha)
//
//        if (usu != null) {
//            Log.v("conexão BD", "Sucesso")
//        }
//
//        else {
//            Log.v("conexão BD", "Fracasso")
//        }
    }

}