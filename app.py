import json
from datetime import date, datetime
import calendar
import locale
from flask import Flask, request, jsonify
import mysql.connector
from mysql.connector import Error

# Definir a localidade para o formato brasileiro
locale.setlocale(locale.LC_ALL, 'pt_BR.UTF-8')

app = Flask(__name__)

# Configurações de conexão com o banco de dados
db_config = {
    'host': 'localhost',
    'user': 'debts_app',
    'password': 'debts2024',
    'database': 'debts'
}

#----------------- Função p/ Validar a existencia do Usuário -----------------------------------------------------------

#função para verificar se existe o usuario
def validarUsuario(IDusuario):
    # Inicializar a conexão com o banco de dados
    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor(dictionary=True)

        # Consulta SQL
        sql = "SELECT * FROM usuarios_debts WHERE id_usuario = %s"
        cursor.execute(sql, (IDusuario,))

        # Processar o resultado
        result = cursor.fetchone()
        if result:
            return True, "Usuário valido."
        else:
            return False, "Esse usuário não existe."

    except mysql.connector.Error as err:
        print(f"Erro na consulta validar usuario: {err}")
        return False, f"Erro ao realizar a consulta validar usuario: {err}"

    finally:
        # Fechar o cursor e a conexão
        if cursor:
            cursor.close()
        if conn:
            conn.close()

#----------------- Função p/ pegar o nome do mês pelo numero -----------------------------------------------------------

def pegar_nome_mes(mes: int) -> str:
    meses = {
        1: "janeiro",
        2: "fevereiro",
        3: "março",
        4: "abril",
        5: "maio",
        6: "junho",
        7: "julho",
        8: "agosto",
        9: "setembro",
        10: "outubro",
        11: "novembro",
        12: "dezembro"
    }

    if mes in meses:
        return meses[mes]
    else:
        raise ValueError("Número do mês inválido")


#----------------- Rota Validar Login ----------------------------------------------------------------------------------

# Função para validar login
def validar_login(nome, senha):
    # Inicializar a conexão com o banco de dados
    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor(dictionary=True)

        # Consulta SQL
        sql = "SELECT * FROM usuarios_debts WHERE nome_usuario = %s AND senha_usuario = %s"
        cursor.execute(sql, (nome, senha))

        # Processar o resultado
        result = cursor.fetchone()
        if result:
            dados_usuario = {
                "nome": result["nome_usuario"],
                "email": result["email_usuario"],
                "cpf": result["cpf_usuario"],
                "senha": result["senha_usuario"],
                "id_usuario": result["id_usuario"]
            }
            return True, dados_usuario
        else:
            return False, {}

    except mysql.connector.Error as err:
        print(f"Erro na consulta validar login: {err}")
        return False, f"Erro ao realizar a consulta validar login: {err}"

    finally:
        # Fechar o cursor e a conexão
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Rota de login
@app.route('/login', methods=['POST'])
def login():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    nome = data.get('nome')
    senha = data.get('senha')

    if not nome or not senha:
        return jsonify({"message": "Nome e senha são obrigatórios!"}), 400

    login_valido, dados_usuario = validar_login(nome, senha)

    if login_valido:
        return jsonify({"message": "Login valido", "dados_usuario": dados_usuario}), 200
    else:
        return jsonify({"message": "Login invalido"}), 401

#----------------- Rota Cadastrar Conta --------------------------------------------------------------------------------

# função para cadastrar uma nova conta
def cadastraConta(nome, email, cpf, senha):

    nomeFormatado = nome.lower()
    emailFormatado = email.lower()

    try:
        # Inicializar a conexão com o banco de dados
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor(dictionary=True)

        # Verificar se o email já está cadastrado
        emailConsulta = "SELECT * FROM usuarios_debts WHERE email_usuario = %s"
        cursor.execute(emailConsulta, (emailFormatado,))
        verificarEmail = cursor.fetchone()

        # Verificar se o CPF já está cadastrado
        cpfConsulta = "SELECT * FROM usuarios_debts WHERE cpf_usuario = %s"
        cursor.execute(cpfConsulta, (cpf,))
        verificarCPF = cursor.fetchone()

        # Se o email ou CPF já existir, retornamos a mensagem de erro
        if verificarEmail or verificarCPF:
            return True, "Essa conta ja existe"

        else:
            # Se o email e o CPF não existirem, inserimos a nova conta
            insert = "INSERT INTO usuarios_debts (nome_usuario, email_usuario, cpf_usuario, senha_usuario) VALUES (%s, %s, %s, %s)"
            cursor.execute(insert, (nomeFormatado, emailFormatado, cpf, senha))
            conn.commit()  # Confirmar a transação

            return False, "Conta criada com sucesso"

    except mysql.connector.Error as err:
        print(f"Erro na consulta cadastrar conta: {err}")
        return False, f"Erro ao realizar a consulta cadastrar conta: {err}"

    finally:
        # Fechar o cursor e a conexão
        if cursor:
            cursor.close()
        if conn:
            conn.close()


# Rota p/ cadastrar uma nova conta
@app.route('/cadastrar_conta', methods=['POST'])
def novaConta():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    nome = data.get('nome')
    email = data.get('email')
    cpf = data.get('cpf')
    senha = data.get('senha')

    if not nome or not email or not cpf or not senha:
        return jsonify({"message": "Nome, email, cpf e senha são obrigatórios!"}), 400

    # Chamar a função para cadastrar a conta
    contaExiste, mensagem = cadastraConta(nome, email, cpf, senha)

    if contaExiste:
        return jsonify({"message": mensagem}), 409  # Usando 409 para conflito (quando a conta já existe)
    else:
        return jsonify({"message": mensagem}), 200  # Mensagem "Conta criada com sucesso"

#----------------- Rota Verificar Questionario -------------------------------------------------------------------------

# função para verificar se o questionário já foi preenchido ou não
def verificarQuestionario(IDusuario):
    # Inicializar a conexão com o banco de dados
    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor(dictionary=True)

        # Consulta SQL
        sql = "SELECT * FROM questionario_usuario WHERE id_user_quest = %s"
        cursor.execute(sql, (IDusuario,))

        # Processar o resultado
        questionarioPreenchido = cursor.fetchone()

        if questionarioPreenchido:
            return True, "Questinario preenchido."
        else:
            return False, "Questionario nao preenchido."

    except mysql.connector.Error as err:
        print(f"Erro na consulta verificar Questionario: {err}")
        return False, f"Erro ao realizar a consulta verificar Questionario: {err}"

    finally:
        # Fechar o cursor e a conexão
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Rota p/ consultar o questionario
@app.route('/verificar_questionario', methods=['POST'])
def consultarQuestionario():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    IDusuario = data.get('id')

    if not IDusuario:
        return jsonify({"message": "id do usuario e obrigatorio!"}), 400

    # Chamar a função para cadastrar a conta
    questionarioPreenchido, mensagem = verificarQuestionario(IDusuario)

    if questionarioPreenchido:
        return jsonify({"message": mensagem}), 200  # Mensagem "Conta criada com sucesso"
    else:
        return jsonify({"message": mensagem}), 404  # Usando 404 para quando o questionario não foi preenchido

#----------------- Rota Salvar Questionário ----------------------------------------------------------------------------

# função para salvar/atualizar o questionário no BD
def salvarQuestionario(nvl_conhecimeto_financ, tps_investimentos, tx_uso_ecommerce, tx_uso_app_transporte, tx_uso_app_entrega, IDusuario):

    try:
        # Inicializar a conexão com o banco de dados
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor(dictionary=True)

        tps_investimentosJSON = json.dumps(tps_investimentos)

        # Verificar se o questionário já foi preenchido
        questionarioPreenchido, _ = verificarQuestionario(IDusuario)

        # Verifica se já existe um questionario salvo se existir ele só atualiza as informações
        if questionarioPreenchido:
            update = """
                        UPDATE questionario_usuario 
                        SET nvl_conhecimeto_financ = %s, 
                            tps_investimentos = %s, 
                            tx_uso_ecommerce = %s, 
                            tx_uso_app_transporte = %s, 
                            tx_uso_app_entrega = %s 
                        WHERE id_user_quest = %s
                        """
            cursor.execute(update, (nvl_conhecimeto_financ, tps_investimentosJSON, tx_uso_ecommerce, tx_uso_app_transporte, tx_uso_app_entrega, IDusuario))
            conn.commit() # Confirmar a transação

            # Verificar se algum dado foi atualizado
            if cursor.rowcount > 0:
                return True, "Questionario atualizado com sucesso"

        else:
            # Se o questionário não existir, ele sera salvo no BD
            insert = """
                        INSERT INTO questionario_usuario 
                        (nvl_conhecimeto_financ, tps_investimentos, tx_uso_ecommerce, tx_uso_app_transporte, tx_uso_app_entrega, id_user_quest) 
                        VALUES (%s, %s, %s, %s, %s, %s)
                        """
            cursor.execute(insert, (nvl_conhecimeto_financ, tps_investimentosJSON, tx_uso_ecommerce, tx_uso_app_transporte, tx_uso_app_entrega, IDusuario))
            conn.commit()  # Confirmar a transação

            return True, "Questionario salvo com sucesso"

    except mysql.connector.Error as err:
        print(f"Erro na consulta salvar questionario: {err}")
        return False, f"Erro ao realizar a consulta salvar questionario: {err}"

    finally:
        # Fechar o cursor e a conexão
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Rota p/ salvar/atualizar o questionário
@app.route('/salvar_questionario', methods=['POST','PUT'])
def atualizarQuestionario():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    nvl_conhecimeto_financ = data.get('nvl_conhecimeto_financ')
    tps_investimentos = data.get('tps_investimentos')
    tx_uso_ecommerce = data.get('tx_uso_ecommerce')
    tx_uso_app_transporte = data.get('tx_uso_app_transporte')
    tx_uso_app_entrega = data.get('tx_uso_app_entrega')
    IDusuario = data.get('id')

    if not nvl_conhecimeto_financ or not tps_investimentos or not tx_uso_ecommerce or not tx_uso_app_transporte or not tx_uso_app_entrega or not IDusuario:
        return jsonify({"message": "Informe todos os dados obrigatorios!"}), 400

    # Chamar a função para salvar/atualizar o questionário
    questionario, mensagem = salvarQuestionario(nvl_conhecimeto_financ, tps_investimentos, tx_uso_ecommerce, tx_uso_app_transporte, tx_uso_app_entrega, IDusuario)

    if questionario:  # `questionario` é True se a operação foi bem-sucedida
        return jsonify({"message": mensagem}), 200  # Código 200 para sucesso
    else:
        return jsonify({"message": mensagem}), 400  # Código 400 para erro ao salvar/atualizar

#----------------- Rota Atualizar Dados do Usuário ---------------------------------------------------------------------

# função para atualizar o nome ou email do usuario no BD
def atualizarDados(novoNome, novoEmail, IDusuario):
    # Inicializar a conexão com o banco de dados
    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor(dictionary=True)

        # Consulta SQL
        sql = "UPDATE usuarios_debts SET nome_usuario = %s, email_usuario = %s WHERE id_usuario = %s"
        cursor.execute(sql, (novoNome, novoEmail, IDusuario))

        conn.commit()  # Confirmar a transação

        # Verificar se algum dado foi atualizado
        if cursor.rowcount > 0:
            return True, "Dados atualizados com sucesso"
        else:
            return False, "Nenhum dado atualizado. Verifique se o ID do usuário esta correto."

    except mysql.connector.Error as err:
        print(f"Erro na consulta atualizar dados: {err}")
        return False, f"Erro ao realizar a consulta atualizar dados: {err}"

    finally:
        # Fechar o cursor e a conexão
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Rota p/ atualizar os dados do usuário
@app.route('/atualizar_dados', methods=['PUT'])
def atualizarDadosUsuario():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    nome = data.get('nome')
    email = data.get('email')
    IDusuario = data.get('id')

    if not IDusuario or not nome or not email:
        return jsonify({"message": "id, nome e email do usuário são obrigatórios!"}), 400

    # Chamar a função para cadastrar a conta
    dadosAtualizados, mensagem = atualizarDados(nome,email,IDusuario)

    if dadosAtualizados:
        return jsonify({"message": mensagem}), 200
    else:
        return jsonify({"message": mensagem}), 404 # Usando 404 para indicar que o ID do usuário não existe ou erro na atualização dos dados

#----------------- Rota Atualizar Senha do Usuário ---------------------------------------------------------------------

# função para atualizar a senha do usuario no BD
def atualizarSenha(novaSenha, IDusuario):
    # Inicializar a conexão com o banco de dados
    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor(dictionary=True)

        # Consulta SQL
        sql = "UPDATE usuarios_debts SET senha_usuario = %s WHERE id_usuario = %s"
        cursor.execute(sql, (novaSenha, IDusuario))

        conn.commit()  # Confirmar a transação

        # Verificar se algum dado foi atualizado
        if cursor.rowcount > 0:
            return True, "Senha atualizada com sucesso"
        else:
            return False, "Senha nao atualizada. Verifique se o ID do usuario esta correto."

    except mysql.connector.Error as err:
        print(f"Erro na consulta atualizar senha: {err}")
        return False, f"Erro ao realizar a atualizar senha: {err}"

    finally:
        # Fechar o cursor e a conexão
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Rota p/ atualizar a senha do usuário
@app.route('/atualizar_senha', methods=['PUT'])
def atualizarSenhaUsuario():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    senhaNova = data.get('senha')
    IDusuario = data.get('id')

    if not IDusuario or not senhaNova:
        return jsonify({"message": "id e a nova senha do usuário são obrigatórios!"}), 400

    # Chamar a função para cadastrar a conta
    senhaAtualizada, mensagem = atualizarSenha(senhaNova,IDusuario)

    if senhaAtualizada:
        return jsonify({"message": mensagem}), 200
    else:
        return jsonify({"message": mensagem}), 404 # Usando 404 para indicar que o ID do usuário não existe ou erro na atualização da senha

#----------------- Rota Deletar Usuário ---------------------------------------------------------------------

# Função para deletar um usuário do banco de dados
def deletar_usuario(IDusuario):
    # Inicializa a conexão com o banco de dados
    try:
        conn = mysql.connector.connect(**db_config)  # Certifique-se de que db_config está definido
        if conn.is_connected():
            # Query para excluir um usuário
            query = "DELETE FROM Usuarios_Debts WHERE id_usuario = %s"

            # Preparar a instrução SQL
            cursor = conn.cursor()
            cursor.execute(query, (IDusuario,))  # Executa a consulta

            # Verificar se alguma linha foi afetada
            if cursor.rowcount > 0:
                conn.commit()  # Commit da transação
                return True, "Usuario deletado com sucesso."
            else:
                return False, "Usuario nao encontrado. Verifique se o ID do usuario está correto."

    except Error as e:
        print(f"Erro ao realizar a consulta deletar usuario: {e}")
        return False, f"Erro ao deletar usuario: {e}"

    finally:
        # Fechar o cursor e a conexão
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Rota p/ deletar um usuário
@app.route('/deletar_usuario', methods=['DELETE'])
def deletarUsuario():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    IDusuario = data.get('id')

    if not IDusuario:
        return jsonify({"message": "id do usuário é obrigatório!"}), 400

    # Chamar a função para cadastrar a conta
    usuarioDeletado, mensagem = deletar_usuario(IDusuario)

    if usuarioDeletado:
        return jsonify({"message": mensagem}), 200
    else:
        return jsonify({"message": mensagem}), 404 # Usando 404 para indicar que o ID do usuário não existe

#----------------- Rotas p/ Manipular as Metas do Usuário --------------------------------------------------------------

#----------------- Rota p/ salvar as Metas do Usuário ----------------------------
# Função para salvar meta no banco de dados
def salvar_meta(IDusuario, nome_meta, dt_meta, lista_metas, metas_concluidas, progresso_meta):
    # Inicializa a conexão com o banco de dados
    try:
        conn = mysql.connector.connect(**db_config)  # Certifique-se de que db_config está definido
        if conn.is_connected():
            # Converter listas para JSON
            listaMetasJSON = json.dumps(lista_metas)
            listaMetasConcluidasJSON = json.dumps(metas_concluidas)

            # Query para inserir a meta
            insert = """
                INSERT INTO metas_financeiras 
                (nome_meta, dt_meta, lista_metas, metas_concluidas, progresso_meta, id_user_meta) 
                VALUES (%s, %s, %s, %s, %s, %s)
            """

            # Preparar a instrução SQL
            cursor = conn.cursor()

            # Executar a consulta com os parâmetros
            cursor.execute(insert,
                           (nome_meta, dt_meta, listaMetasJSON, listaMetasConcluidasJSON, progresso_meta, IDusuario))

            # Verificar se a inserção foi bem-sucedida
            if cursor.rowcount > 0:
                conn.commit()  # Confirmar a transação
                return True, "Meta salva com sucesso!"
            else:
                return False, "Falha ao salvar a meta."

    except Error as e:
        print(f"Erro ao salvar meta: {e}")
        return False, f"Erro ao salvar meta: {e}"

    finally:
        # Fechar o cursor e a conexão
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Rota p/ salvar uma nova meta
@app.route('/salvar_meta', methods=['POST'])
def novaMeta():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    IDusuario = data.get('id')
    nome_meta = data.get('nome_meta')
    dt_meta = data.get('data_meta')
    lista_metas = data.get('lista_metas')
    metas_concluidas = data.get('metas_concluidas')
    progresso_meta = data.get('progresso_meta')

    if not IDusuario or not nome_meta or not dt_meta or not lista_metas or not metas_concluidas or not progresso_meta:
        return jsonify({"message": "informe todos os dados necessários para realizar o salvamento da meta!"}), 400

    # Chamar a função para cadastrar a conta
    metaSalva, mensagem = salvar_meta(IDusuario, nome_meta, dt_meta, lista_metas, metas_concluidas, progresso_meta)

    if metaSalva:
        return jsonify({"message": mensagem}), 200
    else:
        return jsonify({"message": mensagem}), 400

#----------------- Rota p/ atualizar as Metas do Usuário ----------------------------
# Função para atualizar metas no banco de dados
def atualizar_meta(IDusuario, IdMeta, metas_concluidas, progresso_meta):

    try:
        # Inicializa a conexão com o banco de dados
        conn = mysql.connector.connect(**db_config)
        if conn.is_connected():
            # Convertendo a lista para JSON para poder salvar no banco de dados
            listaMetasConcluidasJSON = json.dumps(metas_concluidas)

            # Formatando o progresso_meta com uma casa decimal e trocando vírgula por ponto
            progresso_formatado = round(progresso_meta, 1)

            # Query para atualizar a meta
            query = """
                UPDATE metas_financeiras 
                SET metas_concluidas = %s, progresso_meta = %s 
                WHERE id_user_meta = %s AND id_meta = %s
            """

            # Preparar a instrução SQL
            cursor = conn.cursor()
            cursor.execute(query, (listaMetasConcluidasJSON, progresso_formatado, IDusuario, IdMeta))

            # Executa a consulta e verifica se algo foi atualizado
            if cursor.rowcount > 0:
                conn.commit()  # Confirma a transação
                return True, "Meta MySQL atualizada"
            else:
                return False, "Meta não atualizada. Verifique se o ID do usuário ou da Meta estão corretos."

    except Error as e:
        print(f"Erro ao atualizar meta: {e}")
        return False, f"Erro ao atualizar meta: {e}"

    finally:
        # Fechar o cursor e a conexão
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Rota p/ atualizar as metas do usuário
@app.route('/atualizar_meta', methods=['PUT'])
def atualizarMetaUsuario():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    IdMeta = data.get('id_meta')
    metas_concluidas = data.get('metas_concluidas')
    progresso_meta = data.get('progresso_meta')
    IDusuario = data.get('id')

    if not IDusuario or not IdMeta or not metas_concluidas or not progresso_meta:
        return jsonify({"message": "id_usuario, id_meta, metas_concluidas e progresso_meta do usuário são obrigatórios!"}), 400

    # Chamar a função para cadastrar a conta
    metaAtualizada, mensagem = atualizar_meta(IDusuario, IdMeta, metas_concluidas, progresso_meta)

    if metaAtualizada:
        return jsonify({"message": mensagem}), 200
    else:
        return jsonify({"message": mensagem}), 404 # Usando 404 para indicar que o ID do usuário não existe ou erro na atualização da senha

#----------------- Rota p/ deletar as Metas do Usuário ----------------------------

# Função para deletar uma meta financeira do banco de dados
def deletar_meta(IDusuario, IdMeta):

    try:
        # Inicializa a conexão com o banco de dados
        conn = mysql.connector.connect(**db_config)
        if conn.is_connected():
            # Query para excluir a meta com base no ID da meta e ID do usuário
            query = "DELETE FROM metas_financeiras WHERE id_meta = %s AND id_user_meta = %s"

            # Preparar a instrução SQL
            cursor = conn.cursor()
            cursor.execute(query, (IdMeta, IDusuario))  # Executa a consulta com parâmetros

            if cursor.rowcount > 0:  # Verificar se a meta foi excluída
                conn.commit()  # Confirmar a transação
                return True, "Meta excluida com sucesso."
            else:
                return False, "Meta nao encontrada ou ja excluida."

    except Error as e:
        print(f"Erro ao deletar meta: {e}")
        return False, f"Erro ao deletar meta: {e}"

    finally:
        # Fechar o cursor e a conexão
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Rota p/ deletar uma meta do usuário
@app.route('/deletar_meta', methods=['DELETE'])
def deletarMetaUsuario():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    IDusuario = data.get('id')
    IdMeta = data.get('id_meta')

    if not IDusuario or not IdMeta:
        return jsonify({"message": "id do usuário e da meta são obrigatórios!"}), 400

    # Chamar a função para deletar uma meta do usuario
    metaDeletada, mensagem = deletar_meta(IDusuario, IdMeta)

    if metaDeletada:
        return jsonify({"message": mensagem}), 200
    else:
        return jsonify({"message": mensagem}), 404 # Usando 404 para indicar que o ID do usuário não existe

#----------------- Rota p/ listar todas as Metas do Usuário ----------------------------

# Função para listar metas do usuario
def listar_metas(IDusuario):
    # Inicializa a conexão com o banco de dados
    try:
        conn = mysql.connector.connect(**db_config)  # Certifique-se de que db_config está definido
        if conn.is_connected():
            # Lista para armazenar as metas
            listas_items_metas = []

            # Query para selecionar as metas do usuário
            sql = "SELECT * FROM metas_financeiras WHERE id_user_meta = %s"

            # Preparar a instrução SQL
            cursor = conn.cursor(dictionary=True)
            cursor.execute(sql, (IDusuario,))  # Executar a consulta

            resultado = cursor.fetchall()

            if resultado:

                # Processar os resultados da consulta
                for row in resultado:
                    # Recuperar dados da consulta
                    id_meta = row['id_meta']
                    nome_meta = row['nome_meta']
                    data_meta = row['dt_meta']

                    # Formatar a data no formato yyyy-MM-dd
                    if isinstance(data_meta, datetime):
                        data_meta_formatada = data_meta.strftime("%Y-%m-%d")
                    else:
                        data_meta_formatada = str(data_meta)

                    # Obter as listas de metas e metas concluídas em formato JSON e converter para listas Python
                    lista_metas_json = row['lista_metas']
                    lista_metas_concluidas_json = row['metas_concluidas']

                    lista_metas = json.loads(lista_metas_json)  # Converte de JSON para lista de strings
                    lista_metas_concluidas = json.loads(lista_metas_concluidas_json)  # Converte de JSON para lista de booleanos

                    progresso_meta = row['progresso_meta']

                    nome_formatado = nome_meta.lower()

                    # Criar o item com os dados da meta
                    item_debt_map = {
                        "id_meta": id_meta,
                        "nome_meta": nome_formatado,
                        "data_meta": data_meta_formatada,
                        "lista_metas": lista_metas,
                        "metas_concluidas": lista_metas_concluidas,
                        "progresso_meta": progresso_meta
                    }

                    # Adicionar o item à lista
                    listas_items_metas.append(item_debt_map)

                # Retornar a lista de metas
                return True, listas_items_metas

            else:
                return False, "Erro ao listar metas ou nenhum registro encontrado"

    except Error as e:
        print(f"Erro ao listar metas: {e}")
        return False, "Erro ao listar metas: {e}"

    finally:
        # Fechar o cursor e a conexão
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/listar_metas', methods=['POST'])
def listar_metas_route():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Receber o ID do usuário da requisição (pode vir via query params ou corpo da requisição)
    IDusuario = data.get('id')

    if not IDusuario:
        return jsonify({"message": "ID do usuário é obrigatório"}), 400

    # Chamar a função para listar as metas
    metas, listaMetas = listar_metas(IDusuario)

    if metas:
        return jsonify(listaMetas), 200  # Retorna a lista de metas em JSON
    else:
        return jsonify({"message": listaMetas}), 404

#----------------- Rotas p/ Salvar os Rendimentos do Usuário --------------------------------------------------------------

# Função para salvar rendimento no banco de dados
def salvar_rendimento(tipoMovimento, dataRendimento, valorRendimento, IDusuario):
    try:
        # Inicializa a conexão com o banco de dados
        conn = mysql.connector.connect(**db_config)  # Certifique-se de que db_config está definido
        if conn.is_connected():
            # Formatar a data recebida (dd/MM/yyyy) para o formato (yyyy-MM-dd)
            dia, mes, ano = dataRendimento.split("/")
            dia = dia.strip()
            mes = int(mes.strip())
            ano = ano.strip()

            # Obter o nome do mês usando a biblioteca calendar
            nomeMes = pegar_nome_mes(mes)  # Pega o nome completo do mês (ex: Janeiro)

            # Formatar a data para o formato yyyy-MM-dd
            mes_formatado = f"{mes:02d}"
            data_formatada = f"{ano}-{mes_formatado}-{dia}"

            # Query para salvar um novo rendimento do usuário
            sql = """
                INSERT INTO rendimentos (tp_movimento, dt_rendimento, mes, valor_rendimento, id_user_rendimento)
                VALUES (%s, %s, %s, %s, %s)
            """

            usuarioExiste, mensagem = validarUsuario(IDusuario)

            if usuarioExiste:

                # Preparar a instrução SQL e executar a consulta
                cursor = conn.cursor()
                cursor.execute(sql, (tipoMovimento, data_formatada, nomeMes, valorRendimento, IDusuario))

                if cursor.rowcount > 0:  # Verificar se o rendimento foi salvo
                    conn.commit()  # Confirmar a transação
                    return True, "Rendimento salvo com sucesso."
                else:
                    return False, "Rendimento não salvo. Verifique se o ID do usuário está correto."

                cursor.close()

            else:
                return False, mensagem

    except Error as e:
        print(f"Erro ao realizar a consulta salvar rendimento: {e}")
        return False, f"Erro ao realizar a consulta salvar rendimento: {e}"

    finally:
        # Fechar o cursor e a conexão
        if conn:
            conn.close()

@app.route('/salvar_rendimento', methods=['POST'])
def salvar_rendimento_route():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    tipoMovimento = data.get('tipo_movimento')
    dataRendimento = data.get('data_rendimento')
    valorRendimento = data.get('valor_rendimento')
    IDusuario = data.get('id')

    if not tipoMovimento or not dataRendimento or not valorRendimento or not IDusuario:
        return jsonify({"message": "Informe todos os dados obrigatorios para salvar o rendimento!"}), 400

    # Chamar a função para salvar o rendimento
    rendimetoSalvo, mensagem = salvar_rendimento(tipoMovimento, dataRendimento, valorRendimento, IDusuario)

    if rendimetoSalvo:
        return jsonify({"message": mensagem}), 200  # Código 200 para sucesso
    else:
        return jsonify({"message": mensagem}), 404  # Código 404 para erro


#----------------- Rotas p/ listar os Rendimentos e Gastos do Usuário --------------------------------------------------

#----------------- Rota p/ listar os Rendimentos do Usuário ----------------------------

# Função para listar rendimentos de um usuário
def lista_rendimentos(IDusuario):
    lista_rendimentos = []

    try:
        # Conectar ao banco de dados
        conn = mysql.connector.connect(**db_config)  # Certifique-se de que db_config está definido
        if conn.is_connected():
            cursor = conn.cursor()

            # Defina a consulta SQL
            sql = "SELECT * FROM rendimentos WHERE id_user_rendimento = %s ORDER BY dt_rendimento ASC"
            cursor.execute(sql, (IDusuario,))

            resultado = cursor.fetchall()

            if resultado:

                # Processar os resultados da consulta
                for row in resultado:
                    id_rendimento = row[0]
                    nome_rendimento = row[1]
                    data_rendimento = row[2]
                    valor_rendimento = float(row[4])

                    # Formatar o nome do rendimento
                    nome_rendimento_formatado = nome_rendimento.capitalize()

                    # Formatar a data (yyyy-MM-dd) para "dia de mês de ano"
                    dia = data_rendimento.day
                    mes = data_rendimento.month
                    ano = data_rendimento.year

                    # Obter o nome do mês usando a biblioteca calendar
                    nomeMes = pegar_nome_mes(mes)  # Pega o nome completo do mês (ex: Janeiro)

                    # Formatar a data para "dia de mês de ano"
                    #data_formatada = f"{dia} de {nomeMes} de {ano}"
                    data_formatada = data_rendimento.strftime('%Y-%m-%d')

                    # Formatar o valor como moeda brasileira
                    try:
                        valor_rendimento_formatado = locale.currency(float(valor_rendimento), grouping=True)
                    except ValueError:
                        valor_rendimento_formatado = "Valor inválido"

                    # Criar um dicionário representando a operação financeira
                    item_rendimento = {
                        'id': id_rendimento,
                        'descricao': nome_rendimento_formatado,
                        'tipo_movimento': "",
                        'valor': valor_rendimento,
                        'data': data_formatada
                    }

                    # Adicionar à lista de rendimentos
                    lista_rendimentos.append(item_rendimento)

                return True, lista_rendimentos

            else:
                return False, "Lista de rendimentos vazia. Verifique se o ID do usuário está correto."

            # Fechar o cursor
            cursor.close()


    except Error as e:
        print(f"Erro ao realizar a consulta listar rendimentos: {e}")
        return False, f"Erro ao realizar a consulta listar rendimentos: {e}"

    finally:
        # Fechar o cursor e a conexão
        if conn.is_connected():
            conn.close()

@app.route('/listar_rendimentos', methods=['POST'])
def listar_rendimentos():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    IDusuario = data.get('id')

    if not IDusuario:
        return jsonify({"message": "ID do usuário é obrigatório!"}), 400

    # Chamar a função para salvar o rendimento
    listaRendimentos, mensagem = lista_rendimentos(IDusuario)

    if listaRendimentos:
        return jsonify(mensagem), 200  # Código 200 para sucesso
    else:
        return jsonify({"message": mensagem}), 404  # Código 404 para erro


#----------------- Rota p/ listar os Gastos do Usuário ----------------------------

# Função para listar os gastos de um usuário
def lista_gastos(IDusuario):
    lista_gastos = []

    try:
        # Inicializa a conexão com o banco de dados
        conn = mysql.connector.connect(**db_config)  # Certifique-se de que db_config está definido
        if conn.is_connected():
            # Query para buscar os gastos do usuário
            sql = "SELECT * FROM gastos WHERE id_user_gasto = %s ORDER BY dt_gasto ASC"

            # Preparar a instrução SQL e executar a consulta
            cursor = conn.cursor()
            cursor.execute(sql, (IDusuario,))

            # Obter todos os resultados da consulta
            resultados = cursor.fetchall()

            if resultados:

                # Processar os resultados da consulta
                for row in resultados:
                    id_gasto = row[0]
                    nome_gasto = row[1]
                    tipo_movimento = row[2]
                    valor_gasto = float(row[3])
                    data_gasto = row[4]

                    # Formatar o nome do gasto (você pode criar uma função similar ao FormatarNome se necessário)
                    nome_gasto_formatado = nome_gasto.capitalize()

                    # Formatar o tipo de movimento
                    forma_pagamento_formatada = tipo_movimento.capitalize()

                    # Formatar a data (yyyy-MM-dd) para "dia de mês de ano"
                    dia = data_gasto.day
                    mes = data_gasto.month
                    ano = data_gasto.year

                    # Obter o nome do mês usando a biblioteca calendar
                    nomeMes = pegar_nome_mes(mes)  # Pega o nome completo do mês (ex: Janeiro)

                    # Formatar a data para "dia de mês de ano"
                    #data_formatada = f"{ano}-{mes}-{dia}"
                    data_formatada = data_gasto.strftime('%Y-%m-%d')

                    # Formatar o valor como moeda brasileira
                    try:
                        valor_gasto_formatado = locale.currency(float(valor_gasto), grouping=True)
                    except ValueError:
                        valor_gasto_formatado = "Valor inválido"

                    # Criar um dicionário representando a operação financeira
                    item_gasto = {
                        'id': id_gasto,
                        'descricao': nome_gasto_formatado,
                        'tipo_movimento': forma_pagamento_formatada,
                        'valor': valor_gasto,
                        'data': data_formatada
                    }

                    # Adicionar o item à lista
                    lista_gastos.append(item_gasto)

                return True, lista_gastos

            else:
                return False, "Lista de gastos vazia. Verifique se o ID do usuário está correto."

            cursor.close()


    except Error as e:
        print(f"Erro ao realizar a consulta listar gastos: {e}")
        return False, f"Erro ao realizar a consulta listar gastos: {e}"

    finally:
        # Fechar a conexão
        if conn.is_connected():
            conn.close()

@app.route('/listar_gastos', methods=['POST'])
def listar_gastos():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    IDusuario = data.get('id')

    if not IDusuario:
        return jsonify({"message": "ID do usuário é obrigatório!"}), 400

    # Chamar a função para salvar o rendimento
    listaGastos, mensagem = lista_gastos(IDusuario)

    if listaGastos:
        return jsonify(mensagem), 200  # Código 200 para sucesso
    else:
        return jsonify({"message": mensagem}), 404  # Código 404 para erro

#----------------- Rota para verificar atualizações nas listas do BD ---------------------------------------------------

# Função para buscar a última data de criação das listas no MySQL
def get_ultima_atualizacao_listas_mysql(IDusuario, consultarLista):
    # Define o campo de ID correspondente para cada tabela
    id_user_tabela = {
        "metas_financeiras": "meta",
        "gastos": "gasto",
        "rendimentos": "rendimento"
    }.get(consultarLista.lower(), "")

    if not id_user_tabela:
        return False, "Tabela inválida."

    # Formato da data para análise
    formato = "%Y-%m-%d %H:%M:%S"
    timesTamp = datetime.min

    try:
        # Inicializa a conexão com o banco de dados
        conn = mysql.connector.connect(**db_config)  # Certifique-se de que db_config está definido
        if conn.is_connected():
            cursor = conn.cursor()

            # Consulta SQL para buscar a última data de criação
            sql = f"SELECT data_criacao FROM {consultarLista.lower()} WHERE id_user_{id_user_tabela} = %s ORDER BY data_criacao DESC LIMIT 1"

            cursor.execute(sql, (IDusuario,))

            # Processa o resultado
            result = cursor.fetchone()

            if result:
                data_criacao = result[0]  # Pega o valor da coluna `data_criacao`
                timesTamp = data_criacao.strftime("%Y-%m-%d %H:%M:%S")

                return True, timesTamp

            else:
                return False, "Busca nao realizada. Verifique se o ID do usuario esta correto."

            cursor.close()

    except Error as e:
        print(f"Erro ao realizar a consulta getUltimaAtualizacaoListas_MySQL: {e}")
        return False, f"Erro ao realizar a consulta getUltimaAtualizacaoListas_MySQL: {e}"

    finally:
        # Fechar o cursor
        if conn.is_connected():
            conn.close()

@app.route('/verificar_atualizacao_tabela', methods=['POST'])
def pegarUltimaAtualizacaoTabela():
    # Converte o corpo da requisição JSON em um dicionário Python
    data = request.json

    # Verificar se os campos obrigatórios estão presentes
    IDusuario = data.get('id')
    consultarLista = data.get('nome_tabela')

    if not IDusuario or not consultarLista:
        return jsonify({"message": "ID do usuário e nome da tabela são obrigatórios!"}), 400

    # Chamar a função para salvar o rendimento
    ultimaAtualizacao, mensagem = get_ultima_atualizacao_listas_mysql(IDusuario, consultarLista)

    if ultimaAtualizacao:
        return jsonify({"message": mensagem}), 200  # Código 200 para sucesso
    else:
        return jsonify({"message": mensagem}), 404  # Código 404 para erro

if __name__ == '__main__':
    app.run(debug=True, port=36366, host='0.0.0.0')