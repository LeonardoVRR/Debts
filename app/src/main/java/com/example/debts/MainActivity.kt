package com.example.debts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.Conexao_BD.conexaoBD_Debts
import com.example.debts.Conexao_BD.conexaoBanco_Debts
import com.example.debts.visibilidadeSenha.AlterarVisibilidade
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {


    lateinit var connect: Connection
    lateinit var connectionResult: String

    var context: Context = this

    //teste
    lateinit var conexaoBanco_Debts: conexaoBanco_Debts
    var conexao: Connection? = null
    lateinit var db: String
    lateinit var str: String

    //configurando o crud no BD
    private var userDB = "Leonardo"
    private var senhaDB = "123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //db = "DEBTS"
        //conexaoBanco_Debts = conexaoBanco_Debts()
        //conexao = conexaoBanco_Debts.conectar()
        //conectar()

        //configurando o botão de icone da senha para mudar quando for clicado
        val btn_IconeSenha: ImageButton = findViewById(R.id.btn_visibilidadeSenhaLogin)
        val campoSenha: EditText = findViewById(R.id.input_senhaLogin)

        btn_IconeSenha.setOnClickListener { AlterarVisibilidade(campoSenha, btn_IconeSenha).verSenha() }

    }

    //configurando a navegação para a tela principal
    public fun login(v: View) {
//        val conexao = conexaoBanco_Debts()
//
//        //faz a chamada no banco de dados
//        runBlocking {
//            // Instanciar a classe de conexão
//            Toast.makeText(context, "Iniciando Conexão ao Banco", Toast.LENGTH_SHORT).show()
//
//            // Lançar uma coroutine para realizar a conexão
//            launch {
//                val connection = conexao.conectar()
//                if (connection != null) {
//                    Log.v("Conexão Banco", "Sucesso")
//                    // Use a conexão aqui
//                    Toast.makeText(context, "Conetado ao Banco", Toast.LENGTH_SHORT).show()
//                    connection.close()
//                } else {
//                    Log.v("Conexão Banco", "Falha")
//                    Toast.makeText(context, "Erro na Conexão ao Banco", Toast.LENGTH_SHORT).show()
//                }
//                Toast.makeText(context, "Conexão ao Banco Finalizada", Toast.LENGTH_SHORT).show()
//            }
//        }

        val inputNome: EditText = findViewById(R.id.input_nomeUsuarioLogin)
        val inputSenha: EditText = findViewById(R.id.input_senhaLogin)

        val limparEntradaNome = inputNome.text.toString().lowercase().trim()
        val limparEntradaSenha = inputSenha.text.toString().trim()

        val limparNomeBD = userDB.toString().lowercase().trim() //tira os espaços no inicio e no fim da string e deixa tudo em minusculo
        val limparSenhaBD = senhaDB.toString().trim()

        //verificando se o nome de usuario e senha conferem com os que foram salvos no BD
        if (limparEntradaSenha.isEmpty() && limparEntradaNome.isEmpty()) {

            CustomToast().showCustomToast(this, "Preencha todos os campos.")
        }

        else {
            if (limparEntradaNome == limparNomeBD && limparEntradaSenha == limparSenhaBD){
                val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
                startActivity(navegarTelaPrincipal)
                finish()
            }

            else {
                CustomToast().showCustomToast(this, "Usuario ou Senha incorreto.")
            }
        }

    }

    public fun criarConta(v: View) {
        val navegarCadastrarConta = Intent(this, criarConta::class.java)
        startActivity(navegarCadastrarConta)
        finish()
    }

    public fun conectar() {
        val executorService: ExecutorService = Executors.newSingleThreadExecutor()
        executorService.execute {
            try {
                if (conexao == null){
                    str = "Error"
                }

                else {
                    str = "Connected with SQL server"
                }
            }

            catch (e: Exception){
                throw RuntimeException(e)
            }

            runOnUiThread {
                try {
                    Thread.sleep(1000)
                }
                catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }

                Toast.makeText(this, str, Toast.LENGTH_LONG).show()
            }
        }
    }

    public fun autenticacaoLogin(v: View) {
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

    public fun GetTextFromSQL(v: View) {
        val inputNome: EditText = findViewById(R.id.input_nomeUsuarioLogin)
        val limparEntradaNome = inputNome.text.toString().lowercase().trim()

        try {
            var conexaoBD_Debts = conexaoBD_Debts()
            connect = conexaoBD_Debts.connectionClass()!!

            if (connect != null) {
                if (!connect.isClosed()){
                    var query: String = "SELECT * FROM cadastroUsuarios"
                    var st: Statement = connect.createStatement()
                    var rs: ResultSet = st.executeQuery(query)

                    while (rs.next()){
                        Log.v("Consulta BD", "${rs.getString(2)}")
                    }
                }

                else {
                    Log.v("Conexao BD", "Conexão encerrada")
                }
            }

            else {
                connectionResult="Check Connection"
                Log.v("DB", connectionResult)
            }
        }

        catch (ex: Exception) {
            Log.e("Error 2 ", "${ex.message}")
        }
    }
}

//suspend fun conectarBanco() = withContext(Dispatchers.IO) {
//    // Lança uma Coroutine no escopo de `runBlocking`
//    try {
//        // Estabelece a conexão com o SQL Server
//        var conn = conexaoBanco_Debts().conectar()
//
//        Log.v("Conectado Banco", "Sucesso")
//
//        // Utilize a conexão aqui...
//
//        // Feche a conexão após o uso
//        conn?.close()
//    } catch (e: Exception) {
//        e.printStackTrace()
//        Log.e("Estado Conexão Banco", "${e.message}")
//    }
//}