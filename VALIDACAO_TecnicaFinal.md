# Validação Técnica Final — TacoExpress

Ambiente alvo: **Windows 10 · VS Code · JDK 24.0.1 · Maven 4.0.0-rc-5 · XAMPP (MariaDB) · JavaFX modular**

> Verificações empíricas feitas no ambiente remoto:
> - `mysql-connector-j-8.4.0.jar` NÃO tem `Automatic-Module-Name` nem `module-info`
>   -> o nome `mysql.connector.j` é DERIVADO do nome do arquivo.
> - `java --list-modules` resolve: `mysql.connector.j@8.4.0 ... automatic` (confirmado correto).
> - Compilação `BUILD SUCCESS` foi feita com JDK 21 (o que existe no ambiente remoto), nao com JDK 24.


## 1. JDK 24 x alteracoes feitas com Java 21
- Sem incompatibilidade de compilacao: `--release 21` e suportado pelo javac do JDK 24
  (gera bytecode 21, roda no 24).
- JavaFX 21 sobre JDK 24: funciona. Para casar versoes, pode-se subir javafx.version para 24
  (permitido porque voce tem JDK 24). Mantive 21 LTS por ser a versao validada.
- maven-compiler-plugin 3.13.0 no JDK 24: compila com --release 21 sem problema; se houver
  aviso de ASM/class file, atualizar para 3.14.0 (prudencia, nao erro).


## 2. O modulo mysql.connector.j esta correto?
- SIM, validado. pom.xml: com.mysql:mysql-connector-j:8.4.0 / module-info: requires mysql.connector.j;
- Atencao: como o nome e derivado do filename (sem Automatic-Module-Name), o JDK pode emitir
  um WARNING ("nome derivado do nome do arquivo"). E so aviso; resolve e roda. Com Maven o nome
  do JAR e estavel.


## 3. XAMPP usa MariaDB? Quebra o driver MySQL 8.4.0?
- SIM, o XAMPP embarca MariaDB (rotulada como "MySQL").
- Connector/J 8.4.0 x MariaDB: oficialmente nao suportado pela Oracle. Para CRUD simples
  normalmente funciona com MariaDB 10.4.x, mas ha casos reais de atrito (parsing de versao,
  "Unknown system variable", handshake). RISCO MEDIO -> testar no seu XAMPP.
- Plano B se der erro de conexao:
    pom.xml ........... org.mariadb.jdbc:mariadb-java-client:3.4.1
    module-info.java .. requires org.mariadb.jdbc;
    ConnectionFactory . URL jdbc:mariadb://localhost:3306/taco_express_db
                        (remover serverTimezone e allowPublicKeyRetrieval, que sao do MySQL)


## 4. Alguma alteracao pode quebrar o codigo dos colegas?
1. Coluna `endereco` no banco: se compartilham o mesmo banco e alguem nao rodar o ALTER TABLE,
   o UsuarioDAO.alterar() quebra com "Unknown column 'endereco'". (MAIOR RISCO)
2. UsuarioDAO.alterar() mudou de comportamento: agora grava endereco; chamado com endereco=null
   zera o campo no banco.
3. Bump Java/JavaFX 13->21 obriga todos a terem JDK 21+.
4. (menor) Namespaces FXML /26->/21 geram conflito de merge no SceneBuilder.


## 5. Usuario/UsuarioDAO/banco sao mesmo necessarios? Existe solucao mais simples?
SIM, existe solucao mais simples e recomendada (elimina os riscos #1 e #2 da secao 4):

- O modelo original nunca teve endereco no usuario (so a tabela `pedido` tem `endereco`, por
  pedido). A coluna nova so existe porque optamos por persistir endereco por usuario.
- Alternativa SEM mexer no schema: manter `endereco` como campo EM MEMORIA no Usuario
  (transiente, sem coluna). A tela EditarEndereco grava no usuarioLogado em memoria, e esse
  endereco alimenta o `pedido` no checkout (que ja tem coluna `endereco`).
    Reverter: taco_express_db.sql, UsuarioDAO.alterar(), UsuarioDAO.mapear()
    Manter:   campo `endereco` no Usuario (em memoria) + as duas telas
    Resultado: zero mudanca de schema, zero risco para colegas, telas funcionando.
    Trade-off: o endereco nao persiste entre logins (reinicia por sessao) -> aceitavel na
    maioria dos trabalhos.
- EditarPerfil NAO tem esse problema: edita email/senha (ja existem); persiste via alterar()
  sem schema novo.


## 6. Compila e executa no seu ambiente?
- Compilar: SIM (JDK 24 + --release 21).
- Executar (UI): SIM (JavaFX 21 roda no JDK 24).
- Executar (banco): depende de (a) MariaDB do XAMPP aceitar o Connector/J 8.4.0 -> testar;
  e (b) a tabela usuario ter a coluna endereco (se mantiver a abordagem atual) -> rodar ALTER.


## 7. O que NAO aplicar
1. Namespaces FXML /26->/21 (cosmetico; so gera ruido de merge) -> pode reverter.
2. Coluna endereco + alteracoes UsuarioDAO/SQL -> preferir abordagem em memoria (secao 5),
   a menos que o grupo combine a migracao do banco.
3. Bump de versao no pom.xml sem alinhar o grupo antes (se nem todos estao em JDK 21+).


## Analise dos erros reais obtidos ao rodar

| Erro real                                                              | Causa                                            | Alteracao que corrige        | Arquivo          |
| --------------------------------------------------------------------- | ------------------------------------------------ | ---------------------------- | ---------------- |
| package java.sql is not visible                                        | Modulo nao le java.sql                           | requires java.sql;           | module-info.java |
| module com.mycompany.br.fatec.tacoexpress does not read java.sql       | Mesma causa (declaracao ausente)                 | requires java.sql;           | module-info.java |
| App.setRoot(String) is not public                                     | Metodo package-private chamado de outro pacote   | public static void setRoot   | App.java         |

Observacao: os dois primeiros erros tem a MESMA causa e sao resolvidos pela MESMA linha
(requires java.sql;). O requires mysql.connector.j; NAO corrige esses dois -> ele resolve o
runtime do driver. Esses erros confirmam que o projeto original nao compilava.


## Tabela de classificacao

| Alteracao                                      | Obrigatoria            | Opcional        | Risco                                  |
| ---------------------------------------------- | ---------------------- | --------------- | -------------------------------------- |
| App.java - setRoot -> public                   | SIM (compilar)         | -               | Nenhum                                 |
| App.java - /css -> /Css                         | SIM (executar)         | -               | Nenhum                                 |
| module-info - requires java.sql                | SIM (compilar)         | -               | Nenhum                                 |
| module-info - requires mysql.connector.j       | SIM (executar)         | -               | Baixo (warning de nome derivado)       |
| module-info - opens Controller                 | SIM (executar)         | -               | Nenhum                                 |
| module-info - opens Model                      | -                      | SIM (melhoria)  | Nenhum                                 |
| module-info - remove requires java.base        | -                      | SIM (melhoria)  | Nenhum                                 |
| pom.xml - versoes Java/JavaFX/plugins          | SIM (compilar)         | -               | Medio (exige JDK 21+ no grupo)         |
| pom.xml - dependencia mysql-connector-j        | SIM (executar)         | -               | Medio (MariaDB do XAMPP)               |
| Usuario.java - campo endereco                  | so p/ EditarEndereco   | evitavel        | Baixo                                  |
| UsuarioDAO.java - alterar/mapear               | so p/ EditarEndereco   | evitavel        | ALTO (quebra colega sem ALTER)         |
| taco_express_db.sql - coluna endereco          | so p/ EditarEndereco   | evitavel        | ALTO (schema compartilhado)            |
| EditarPerfil.fxml + controller                 | funcional              | -               | Baixo                                  |
| EditarEndereco.fxml + controller               | funcional              | -               | Medio (depende do schema)              |
| 6x FXML - namespace /26 -> /21                  | -                      | SIM (cosmetico) | Baixo (merge noise)                    |
| .gitignore                                      | -                      | SIM (melhoria)  | Nenhum                                 |

Classificacao por categoria:
- Necessaria para COMPILAR: setRoot public; requires java.sql; versoes do pom.xml.
- Necessaria para EXECUTAR: /Css; requires mysql.connector.j; opens Controller; dependencia mysql-connector-j.
- Apenas melhoria: opens Model; remover java.base; .gitignore; namespaces FXML.
- Pode causar conflito com o grupo: coluna endereco + UsuarioDAO + SQL (alto); bump de versoes (medio); namespaces FXML (baixo).


## Comando de migracao (se mantiver a coluna endereco)
    ALTER TABLE usuario ADD COLUMN endereco VARCHAR(300) DEFAULT NULL;
