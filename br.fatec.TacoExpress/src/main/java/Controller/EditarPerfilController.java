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
