package Controller;



import DAO.UsuarioDAO;
import Exception.AutenticacaoException;
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

public class LoginController implements Initializable {

    @FXML private TextField     txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private Button        btnEntrar;
    @FXML private Button        btnCadastro;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Guarda o usuário logado para uso em outras telas
    private static Usuario usuarioLogado;

    public static Usuario getUsuarioLogado()         { return usuarioLogado; }
    public static void    setUsuarioLogado(Usuario u) { usuarioLogado = u; }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnEntrar.setOnAction(e -> onEntrar());
        btnCadastro.setOnAction(e -> onCadastro());
    }

    private void onEntrar() {
        try {
            String email = txtEmail.getText().trim();
            String senha = txtSenha.getText().trim();

            if (email.isEmpty()) throw new CampoVazioException("Email");
            if (senha.isEmpty()) throw new CampoVazioException("Senha");

            Usuario u = usuarioDAO.autenticar(email, senha);
            if (u == null) throw new AutenticacaoException();

            usuarioLogado = u;
            App.setRoot("Cardapio");

        } catch (CampoVazioException | AutenticacaoException ex) {
            alerta(Alert.AlertType.WARNING, ex.getMessage());
        } catch (IOException ex) {
            alerta(Alert.AlertType.ERROR, "Erro ao carregar o cardápio.");
        }
    }

    private void onCadastro() {
        try {
            App.setRoot("Cadastro");
        } catch (IOException ex) {
            alerta(Alert.AlertType.ERROR, "Erro ao abrir o cadastro.");
        }
    }

    private void alerta(Alert.AlertType tipo, String msg) {
        Alert a = new Alert(tipo, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
