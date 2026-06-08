# AUDITORIA TÉCNICA — Correções TacoExpress

> **Observação metodológica:** como o projeto **não existia** no repositório git antes
> (o repo é um projeto Python), para o git *todos* os arquivos são "novos". Portanto a
> comparação real "antes/depois" abaixo é feita **contra o ZIP original que você enviou**,
> e não contra o git. Os diffs mostrados são reais (`diff` entre o ZIP e a versão corrigida).
>
> A compilação `BUILD SUCCESS` que reportei foi feita **com JDK 21** (o que está instalado
> no ambiente remoto), **não** com seu JDK 24 local. Isso é relevante para a seção 7.

---

## 1. Conteúdo COMPLETO e FINAL dos arquivos

### 1.1 `pom.xml`

    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
        <modelVersion>4.0.0</modelVersion>
        <groupId>com.mycompany</groupId>
        <artifactId>br.fatec.TacoExpress</artifactId>
        <version>1.0-SNAPSHOT</version>
        <properties>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <maven.compiler.release>21</maven.compiler.release>
            <javafx.version>21.0.6</javafx.version>
        </properties>
        <dependencies>
            <dependency>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-controls</artifactId>
                <version>${javafx.version}</version>
            </dependency>
            <dependency>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-fxml</artifactId>
                <version>${javafx.version}</version>
            </dependency>
            <dependency>
                <groupId>com.mysql</groupId>
                <artifactId>mysql-connector-j</artifactId>
                <version>8.4.0</version>
            </dependency>
        </dependencies>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.13.0</version>
                    <configuration>
                        <release>${maven.compiler.release}</release>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.openjfx</groupId>
                    <artifactId>javafx-maven-plugin</artifactId>
                    <version>0.0.8</version>
                    <configuration>
                        <mainClass>com.mycompany.br.fatec.tacoexpress.App</mainClass>
                    </configuration>
                    <executions>
                        <execution>
                            <!-- Default configuration for running -->
                            <!-- Usage: mvn clean javafx:run -->
                            <id>default-cli</id>
                        </execution>
                        <execution>
                            <!-- Configuration for manual attach debugging -->
                            <!-- Usage: mvn clean javafx:run@debug -->
                            <id>debug</id>
                            <configuration>
                                <options>
                                    <option>-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=localhost:8000</option>
                                </options>
                            </configuration>
                        </execution>
                        <execution>
                            <!-- Configuration for automatic IDE debugging -->
                            <id>ide-debug</id>
                            <configuration>
                                <options>
                                    <option>-agentlib:jdwp=transport=dt_socket,server=n,address=${jpda.address}</option>
                                </options>
                            </configuration>
                        </execution>
                        <execution>
                            <!-- Configuration for automatic IDE profiling -->
                            <id>ide-profile</id>
                            <configuration>
                                <options>
                                    <option>${profiler.jvmargs.arg1}</option>
                                    <option>${profiler.jvmargs.arg2}</option>
                                    <option>${profiler.jvmargs.arg3}</option>
                                    <option>${profiler.jvmargs.arg4}</option>
                                    <option>${profiler.jvmargs.arg5}</option>
                                </options>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </project>

### 1.2 `module-info.java`

    module com.mycompany.br.fatec.tacoexpress {
        requires javafx.controls;
        requires javafx.fxml;
        requires java.sql;
        requires mysql.connector.j;

        // FXMLLoader usa reflexão sobre os controllers (pacote Controller)
        // e sobre os modelos (pacote Model); ambos precisam ser abertos.
        opens com.mycompany.br.fatec.tacoexpress to javafx.fxml;
        opens Controller to javafx.fxml;
        opens Model to javafx.fxml;

        exports com.mycompany.br.fatec.tacoexpress;
    }

### 1.3 `App.java`

    package com.mycompany.br.fatec.tacoexpress;

    import javafx.application.Application;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.stage.Stage;

    import java.io.IOException;

    public class App extends Application {

        private static Scene scene;

        @Override
        public void start(Stage stage) throws IOException {

            Parent root = loadFXML("Login");

            scene = new Scene(root, 400, 800);

            scene.getStylesheets().add(
                App.class.getResource("/Css/style.css")
                   .toExternalForm()
            );

            stage.setTitle("Taco Express");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }

        public static void setRoot(String fxml) throws IOException {
            scene.setRoot(loadFXML(fxml));
        }

        private static Parent loadFXML(String fxml) throws IOException {

            FXMLLoader fxmlLoader = new FXMLLoader(
                    App.class.getResource("/View/" + fxml + ".fxml")
            );

            return fxmlLoader.load();
        }

        public static void main(String[] args) {
            launch();
        }
    }

---

## 2. Explicação de cada alteração nesses três arquivos

### `pom.xml` — diff lógico
| Antes | Depois | Por quê |
|---|---|---|
| `maven.compiler.source=26` + `target=26` | `maven.compiler.release=21` | Java 26 não existe; `source/target` separados são frágeis. `release` é a forma correta e única. |
| (compiler plugin) `<release>14</release>` | `<release>${...release}</release>` = 21 | Antes o POM dizia 26 nas properties **e** 14 no plugin — contradição direta. Unificado em 21. |
| JavaFX `13` (hardcoded 2x) | `21.0.6` via `${javafx.version}` | Versão única, e 13 é antiga/incompatível com Java 21+. |
| — | **+ dependência `mysql-connector-j:8.4.0`** | O `ConnectionFactory` abre `jdbc:mysql://...` mas o driver **não estava** no POM → "No suitable driver found". |
| `maven-compiler-plugin 3.8.0` | `3.13.0` | 3.8.0 não suporta `release` alto. |
| `javafx-maven-plugin 0.0.4` | `0.0.8` | 0.0.4 é muito antigo p/ JDK/JavaFX recentes. |

Nada além disso mudou no POM (os blocos de `executions` debug/ide-debug/ide-profile estão idênticos ao original).

### `module-info.java` — diff lógico
| Antes | Depois | Por quê |
|---|---|---|
| `requires java.base;` | *(removido)* | `java.base` é **implícito** em todo módulo; declará-lo é redundante. Remoção é inócua. |
| — | `requires java.sql;` | Os DAOs usam `java.sql.*`; num projeto modular isso **tem** que ser declarado, ou não compila. |
| — | `requires mysql.connector.j;` | Coloca o driver no grafo de módulos para o `DriverManager` encontrá-lo em runtime. |
| — | `opens Controller to javafx.fxml;` | O `FXMLLoader` instancia os controllers por reflexão; sem `opens`, **todas** as telas falham (`IllegalAccessException`). |
| — | `opens Model to javafx.fxml;` | Defensivo — caso algum FXML acesse propriedades do model por reflexão. |

### `App.java` — diff lógico (somente 2 linhas)
| Antes | Depois | Por quê |
|---|---|---|
| `getResource("/css/style.css")` | `"/Css/style.css"` | O recurso real é `/Css/` (maiúsculo). No Linux (case-sensitive) o caminho errado retorna `null` → NPE no startup. |
| `static void setRoot(...)` | `public static void setRoot(...)` | Os controllers estão no pacote `Controller` e chamam `App.setRoot`; o método era package-private → **não compilava**. (Erro descoberto só na compilação real.) |

---

## 3. Telas EditarPerfil e EditarEndereco

### 3.1 Arquivos criados (4 novos + nenhum sobrescrito)
1. `src/main/resources/View/EditarPerfil.fxml`
2. `src/main/resources/View/EditarEndereco.fxml`
3. `src/main/java/Controller/EditarPerfilController.java`
4. `src/main/java/Controller/EditarEnderecoController.java`

### 3.2 Código completo dos controllers

#### `EditarPerfilController.java`

    package Controller;

    import DAO.UsuarioDAO;
    import Exception.CampoVazioException;
    import Model.Usuario;
    import com.mycompany.br.fatec.tacoexpress.App;

    import java.io.IOException;
    import java.net.URL;
    import java.util.ResourceBundle;
    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Alert;
    import javafx.scene.control.Button;
    import javafx.scene.control.PasswordField;
    import javafx.scene.control.TextField;

    public class EditarPerfilController implements Initializable {

        @FXML private TextField     txtEmail;
        @FXML private PasswordField txtSenha;
        @FXML private Button        btnSalvar;
        @FXML private Button        btnVoltar;

        private final UsuarioDAO usuarioDAO = new UsuarioDAO();

        @Override
        public void initialize(URL url, ResourceBundle rb) {
            Usuario u = LoginController.getUsuarioLogado();
            if (u == null) {
                alerta(Alert.AlertType.ERROR, "Nenhum usuário logado.");
                voltarLogin();
                return;
            }

            // Pré-preenche com os dados atuais do usuário.
            txtEmail.setText(u.getEmail());
            txtSenha.setText(u.getSenha());

            btnSalvar.setOnAction(e -> onSalvar());
            btnVoltar.setOnAction(e -> navegar("Configuracoes"));
        }

        private void onSalvar() {
            try {
                Usuario u = LoginController.getUsuarioLogado();
                if (u == null) throw new IllegalStateException("Nenhum usuário logado.");

                String email = txtEmail.getText().trim();
                String senha = txtSenha.getText().trim();

                if (email.isEmpty()) throw new CampoVazioException("Email");
                if (senha.isEmpty()) throw new CampoVazioException("Senha");

                // Só valida duplicidade se o e-mail mudou (emailExiste pegaria o próprio usuário).
                if (!email.equalsIgnoreCase(u.getEmail()) && usuarioDAO.emailExiste(email)) {
                    alerta(Alert.AlertType.WARNING, "Este e-mail já está em uso.");
                    return;
                }

                u.setEmail(email);
                u.setSenha(senha);
                usuarioDAO.alterar(u);

                alerta(Alert.AlertType.INFORMATION, "Perfil atualizado com sucesso!");
                navegar("Configuracoes");

            } catch (CampoVazioException ex) {
                alerta(Alert.AlertType.WARNING, ex.getMessage());
            } catch (IllegalStateException ex) {
                alerta(Alert.AlertType.ERROR, ex.getMessage());
                voltarLogin();
            }
        }

        private void navegar(String fxml) {
            try {
                App.setRoot(fxml);
            } catch (IOException ex) {
                alerta(Alert.AlertType.ERROR, "Erro ao abrir " + fxml + ".");
            }
        }

        private void voltarLogin() {
            try {
                App.setRoot("Login");
            } catch (IOException ex) {
                alerta(Alert.AlertType.ERROR, "Erro ao voltar ao login.");
            }
        }

        private void alerta(Alert.AlertType tipo, String msg) {
            Alert a = new Alert(tipo, msg);
            a.setHeaderText(null);
            a.showAndWait();
        }
    }

#### `EditarEnderecoController.java`

    package Controller;

    import DAO.UsuarioDAO;
    import Exception.CampoVazioException;
    import Model.Usuario;
    import com.mycompany.br.fatec.tacoexpress.App;

    import java.io.IOException;
    import java.net.URL;
    import java.util.ResourceBundle;
    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Alert;
    import javafx.scene.control.Button;
    import javafx.scene.control.TextField;

    public class EditarEnderecoController implements Initializable {

        @FXML private TextField txtEndereco;
        @FXML private Button     btnSalvar;
        @FXML private Button     btnVoltar;

        private final UsuarioDAO usuarioDAO = new UsuarioDAO();

        @Override
        public void initialize(URL url, ResourceBundle rb) {
            Usuario u = LoginController.getUsuarioLogado();
            if (u == null) {
                alerta(Alert.AlertType.ERROR, "Nenhum usuário logado.");
                voltarLogin();
                return;
            }

            // Pré-preenche com o endereço atual (pode ser nulo na primeira vez).
            if (u.getEndereco() != null) {
                txtEndereco.setText(u.getEndereco());
            }

            btnSalvar.setOnAction(e -> onSalvar());
            btnVoltar.setOnAction(e -> navegar("Configuracoes"));
        }

        private void onSalvar() {
            try {
                Usuario u = LoginController.getUsuarioLogado();
                if (u == null) throw new IllegalStateException("Nenhum usuário logado.");

                String endereco = txtEndereco.getText().trim();
                if (endereco.isEmpty()) throw new CampoVazioException("Endereço");

                u.setEndereco(endereco);
                usuarioDAO.alterar(u);

                alerta(Alert.AlertType.INFORMATION, "Endereço atualizado com sucesso!");
                navegar("Configuracoes");

            } catch (CampoVazioException ex) {
                alerta(Alert.AlertType.WARNING, ex.getMessage());
            } catch (IllegalStateException ex) {
                alerta(Alert.AlertType.ERROR, ex.getMessage());
                voltarLogin();
            }
        }

        private void navegar(String fxml) {
            try {
                App.setRoot(fxml);
            } catch (IOException ex) {
                alerta(Alert.AlertType.ERROR, "Erro ao abrir " + fxml + ".");
            }
        }

        private void voltarLogin() {
            try {
                App.setRoot("Login");
            } catch (IOException ex) {
                alerta(Alert.AlertType.ERROR, "Erro ao voltar ao login.");
            }
        }

        private void alerta(Alert.AlertType tipo, String msg) {
            Alert a = new Alert(tipo, msg);
            a.setHeaderText(null);
            a.showAndWait();
        }
    }

### 3.3 FXML completos

#### `EditarPerfil.fxml`

    <?xml version="1.0" encoding="UTF-8"?>

    <?import javafx.scene.control.Button?>
    <?import javafx.scene.control.Label?>
    <?import javafx.scene.control.PasswordField?>
    <?import javafx.scene.control.TextField?>
    <?import javafx.scene.layout.AnchorPane?>
    <?import javafx.scene.layout.VBox?>

    <AnchorPane prefHeight="506.0" prefWidth="348.0" stylesheets="@../Css/style.css" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1" fx:controller="Controller.EditarPerfilController">

        <VBox alignment="CENTER" spacing="20" AnchorPane.bottomAnchor="50" AnchorPane.leftAnchor="40" AnchorPane.rightAnchor="40" AnchorPane.topAnchor="50">

            <Label styleClass="titulo" text="Editar Perfil" />

            <TextField fx:id="txtEmail" promptText="Email" styleClass="campo" />

            <PasswordField fx:id="txtSenha" promptText="Senha" styleClass="campo" />

            <Button fx:id="btnSalvar" styleClass="botao-principal" text="Salvar" />

            <Button fx:id="btnVoltar" styleClass="botao-secundario" text="Voltar" />

        </VBox>

    </AnchorPane>

#### `EditarEndereco.fxml`

    <?xml version="1.0" encoding="UTF-8"?>

    <?import javafx.scene.control.Button?>
    <?import javafx.scene.control.Label?>
    <?import javafx.scene.control.TextField?>
    <?import javafx.scene.layout.AnchorPane?>
    <?import javafx.scene.layout.VBox?>

    <AnchorPane prefHeight="506.0" prefWidth="348.0" stylesheets="@../Css/style.css" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1" fx:controller="Controller.EditarEnderecoController">

        <VBox alignment="CENTER" spacing="20" AnchorPane.bottomAnchor="50" AnchorPane.leftAnchor="40" AnchorPane.rightAnchor="40" AnchorPane.topAnchor="50">

            <Label styleClass="titulo" text="Editar Endereço" />

            <TextField fx:id="txtEndereco" promptText="Endereço de entrega" styleClass="campo" />

            <Button fx:id="btnSalvar" styleClass="botao-principal" text="Salvar" />

            <Button fx:id="btnVoltar" styleClass="botao-secundario" text="Voltar" />

        </VBox>

    </AnchorPane>

### 3.4 Por que foi necessário alterar `Usuario`, `UsuarioDAO` e o banco

A tela **EditarPerfil** sozinha **não exigia** nada disso — ela edita `email`/`senha`, que já
existiam. O motivo das três alterações é **exclusivamente a tela EditarEndereco**:

- O modelo de dados original **não tinha o conceito de endereço do usuário**. A tabela
  `usuario` só tinha `id, email, senha`; o `Model.Usuario` idem. (Existe `endereco` na tabela
  `pedido`, mas é o endereço de cada pedido, não um cadastro do usuário.)
- Para "telas completas com DAO" (sua escolha), o endereço precisa ser **persistido**. Logo:
  - `Usuario.java`: novo campo `endereco` + getter/setter (sem isso, nada para guardar).
  - `taco_express_db.sql`: nova coluna `endereco VARCHAR(300) DEFAULT NULL` (sem a coluna, o `UPDATE` falha).
  - `UsuarioDAO.java`: `alterar()` passou a gravar `endereco`, e `mapear()` passou a ler `endereco`.

Diffs exatos (mínimos):

    # Usuario.java
    +    private String endereco;
    +    public String getEndereco()           { return endereco; }
    +    public void   setEndereco(String e)   { this.endereco = e; }

    # UsuarioDAO.java  (alterar)
    -  "UPDATE usuario SET email = ?, senha = ? WHERE id = ?"
    +  "UPDATE usuario SET email = ?, senha = ?, endereco = ? WHERE id = ?"
    +  ps.setString(3, u.getEndereco());   ps.setInt(4, u.getId());

    # UsuarioDAO.java  (mapear)
    -  return new Usuario(id, email, senha);
    +  Usuario u = new Usuario(id, email, senha); u.setEndereco(rs.getString("endereco")); return u;

    # taco_express_db.sql
    +    endereco VARCHAR(300) DEFAULT NULL,

> Se você optasse por **"desabilitar os botões"** em vez de "telas completas",
> `Usuario`/`UsuarioDAO`/SQL **não** precisariam mudar. Essas três alterações são
> consequência direta da sua escolha de implementar a edição de endereço de verdade.

---

## 4. Arquivos existentes modificados ALÉM de App.java / pom.xml / module-info.java

Comparando contra o ZIP original, foram modificados (além dos três):

1. `src/main/java/Model/Usuario.java` — campo `endereco`
2. `src/main/java/DAO/UsuarioDAO.java` — `alterar()` e `mapear()`
3. `src/main/java/com/.../taco_express_db.sql` — coluna `endereco`
4. `src/main/resources/View/Cadastro.fxml` — **apenas namespace** `/26→/21`
5. `src/main/resources/View/Cardapio.fxml` — **apenas namespace**
6. `src/main/resources/View/Carrinho.fxml` — **apenas namespace**
7. `src/main/resources/View/Configuracoes.fxml` — **apenas namespace**
8. `src/main/resources/View/Login.fxml` — **apenas namespace**
9. `src/main/resources/View/ProdutoCard.fxml` — **apenas namespace**

E **criados**: 2 controllers + 2 FXML (seção 3.1) + `.gitignore`.

Confirmo que **nenhum outro arquivo** (demais DAOs, Models, Exceptions, CSS, demais
controllers) foi tocado.

---

## 5. Tabela consolidada

| Arquivo | Tipo de alteração | Motivo | Necessária p/ COMPILAR? | Necessária p/ EXECUTAR? |
|---|---|---|---|---|
| `App.java` (CSS path) | Modificação | Casing `/css`→`/Css` | NÃO | **SIM** (NPE no startup em Linux) |
| `App.java` (`setRoot` public) | Modificação | Chamado de outro pacote | **SIM** | SIM (consequência) |
| `module-info.java` (`requires java.sql`) | Modificação | DAOs usam `java.sql` | **SIM** | SIM |
| `module-info.java` (`requires mysql.connector.j`) | Modificação | Driver no grafo de módulos | NÃO¹ | **SIM** (conexão MySQL) |
| `module-info.java` (`opens Controller`) | Modificação | FXMLLoader via reflexão | NÃO | **SIM** (telas falham sem isso) |
| `module-info.java` (`opens Model`) | Modificação | Defensivo (reflexão em model) | NÃO | NÃO² |
| `module-info.java` (remove `java.base`) | Modificação | Redundante | NÃO | NÃO |
| `pom.xml` (mysql-connector-j) | Modificação | Driver ausente | NÃO¹ | **SIM** |
| `pom.xml` (versões Java/JavaFX/plugins) | Modificação | Versões inconsistentes/inválidas | **SIM** | SIM |
| `Usuario.java` (`endereco`) | Modificação | Suporte a EditarEndereco | **SIM**³ | SIM³ |
| `UsuarioDAO.java` (`alterar`/`mapear`) | Modificação | Persistir endereço | **SIM**³ | SIM³ |
| `taco_express_db.sql` (`endereco`) | Modificação | Coluna p/ persistência | NÃO | **SIM**³ |
| 6× `*.fxml` (namespace `/26→/21`) | Modificação | Alinhar à versão JavaFX | NÃO | NÃO⁴ |
| `EditarPerfil.fxml` + controller | **Criação** | Botão apontava p/ tela inexistente | NÃO⁵ | SIM⁵ |
| `EditarEndereco.fxml` + controller | **Criação** | Botão apontava p/ tela inexistente | NÃO⁵ | SIM⁵ |
| `.gitignore` | Criação | Ignorar `target/` | NÃO | NÃO |

Notas:
1. ¹ Compila sem o driver (é runtime). O `requires mysql.connector.j` só compila **se** a dependência existir no POM — por isso os dois andam juntos.
2. ² `opens Model` não é estritamente necessário hoje (nenhum FXML acessa o model por reflexão); é defensivo. Pode ser removido sem impacto.
3. ³ "Necessária" **apenas porque** as telas de edição foram criadas. Se removidas, deixam de ser necessárias.
4. ⁴ Funcionalmente inócuo — o `FXMLLoader` ignora o número de versão do namespace. É cosmético.
5. ⁵ Não afetam a compilação do resto; sem eles o projeto compila e roda, mas os botões "Editar Perfil/Endereço" dão erro ao clicar.

---

## 6. Classificação das alterações

### A) Obrigatórias para COMPILAR e EXECUTAR (o núcleo do conserto)
- `App.java`: `setRoot` público (compilação) + caminho `/Css` (execução).
- `module-info.java`: `requires java.sql`, `requires mysql.connector.j`, `opens Controller`.
- `pom.xml`: versões Java/JavaFX/plugins coerentes + dependência `mysql-connector-j`.

> Sem **qualquer** um destes, o projeto não compila ou não roda/conecta.

### B) Melhorias opcionais (não mudam comportamento; podem ser revertidas)
- `module-info.java`: `opens Model` (defensivo) e remoção de `requires java.base`.
- 6× FXML: troca de namespace `/26→/21` (puramente cosmética).
- `.gitignore`.

### C) Alterações FUNCIONAIS (mudam o comportamento do sistema)
- **Novas telas** EditarPerfil e EditarEndereco (novo fluxo de UI).
- **Novo campo de dados `endereco`** no `Usuario`, no `UsuarioDAO` e no **banco**
  (mudança no modelo de dados compartilhado).
- **Mudança de comportamento de `UsuarioDAO.alterar()`**: agora também grava a coluna
  `endereco`. ATENÇÃO: se algum código de colega chamar `alterar()` com um `Usuario` cujo
  `endereco` esteja `null`, o endereço gravado será sobrescrito para `NULL`. Comunicar ao grupo.

---

## 7. Compatibilidade

### Java 24 (seu ambiente local)
- **Compilação:** `--release 21` é suportado pelo `javac` do JDK 24 → compila normal,
  gerando bytecode nível 21. OK.
- **Atenção:** validei aqui com **JDK 21**, não 24. Em teoria roda igual, mas rodar
  **JavaFX 21** sobre **JDK 24** funciona na prática, porém o ideal seria casar as versões.
  Se quiser zero atrito, suba `javafx.version` para `24` (combina com seu JDK). Mantive 21
  (LTS) por estabilidade e porque foi o que validei.
- `maven-compiler-plugin 3.13.0` funciona com JDK 24; se aparecer aviso, use `3.14.0`.

### Maven 4.0.0-rc-5
- `modelVersion 4.0.0` continua suportado pelo Maven 4 (retrocompatível). OK.
- Por ser **RC**, pode emitir *warnings* (ex.: sugerir migrar o modelo p/ 4.1.0); não bloqueia.
- Recomendação prática: para a entrega do grupo, **Maven 3.9.x é mais seguro** que um RC.

### MySQL do XAMPP
- ATENÇÃO: o XAMPP normalmente embarca **MariaDB**, não o MySQL da Oracle. O
  `mysql-connector-j 8.4.0` **costuma** conectar em MariaDB 10.4+ e a URL `jdbc:mysql://...`
  com `allowPublicKeyRetrieval`/`useSSL=false` é aceita. Mas em algumas combinações de versão
  pode haver atrito no handshake.
- Se ocorrer erro de driver/handshake, a alternativa é usar o **driver MariaDB**
  (`org.mariadb.jdbc:mariadb-java-client`) e URL `jdbc:mariadb://...` — porém isso exigiria
  mudar o `ConnectionFactory` e o `module-info`. Como o código usa `jdbc:mysql://`, mantive
  o conector MySQL (compatível na maioria dos casos).

### Projeto dos demais integrantes
- MAIOR RISCO: a mudança do **modelo de dados** (`endereco`). Se os colegas usam o **mesmo
  schema/banco**, eles precisarão rodar o `ALTER TABLE` (seção 8); caso contrário o
  `UsuarioDAO.alterar()` quebra com `Unknown column 'endereco'`.
- **Versão do Java/JavaFX:** subi para 21. Se algum colega ainda usa JDK 13/17, o build
  passará a **exigir JDK 21+** deles. Precisa de alinhamento no grupo.
- **Efeito colateral de `alterar()`** (ver seção 6C).
- **Namespaces FXML:** se um colega editar uma tela no SceneBuilder, ele regenera com `/26`,
  criando **conflito de merge** trivial nos 6 FXML.

---

## 8. O que você NÃO deveria aplicar imediatamente (risco de conflito com o grupo)

**Aplicar já** (baixo risco, destravam o projeto e são locais à sua execução):
- `App.java`, `module-info.java`, `pom.xml` (itens da seção 6A).

**NÃO aplicar sem combinar com o grupo antes:**
1. **Mudança de schema + campo `endereco`** (`taco_express_db.sql`, `Usuario.java`,
   `UsuarioDAO.java`) e, por consequência, a **tela EditarEndereco**. Isso altera o **modelo
   de dados compartilhado**. Se aplicar sozinho e os colegas tiverem o banco antigo, quebra
   para eles. → Combinar o `ALTER TABLE` e fazer todos migrarem juntos.
2. **Bump de versão Java/JavaFX no `pom.xml`** se nem todos estão em JDK 21+. → Definir a
   versão-alvo do grupo primeiro.
3. **Troca de namespace nos 6 FXML** (`/26→/21`): cosmético, mas gera ruído/conflito de
   merge. Se quiser evitar atrito, pode **reverter essa parte** — não afeta funcionamento.

A tela **EditarPerfil** (sem o campo endereço) é de baixo risco — só depende de
`email`/`senha` já existentes; pode entrar junto com o bloco 6A.

### Resumo de risco por bloco
| Bloco | Aplicar agora? | Risco p/ o grupo |
|---|---|---|
| 6A (App, module-info, pom-core) | SIM | Baixo |
| EditarPerfil | SIM | Baixo |
| Campo `endereco` + EditarEndereco + SQL | Combinar antes | **Alto** (schema compartilhado) |
| Bump Java/JavaFX | Combinar antes | Médio (exige JDK 21+ p/ todos) |
| Namespaces FXML | Opcional/reverter | Baixo (merge noise) |

### Comandos úteis (migração do banco já existente)

    -- Rodar uma única vez em quem já tem o banco criado:
    ALTER TABLE usuario ADD COLUMN endereco VARCHAR(300) DEFAULT NULL;
