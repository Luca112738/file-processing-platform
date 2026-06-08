package Controller;

import DAO.UsuarioDAO;
import Exception.CampoVazioException;
import Exception.SenhasNaoCoincidemException;
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

public class CadastroController implements Initializable {

    @FXML private TextField     txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private PasswordField txtConfirmarSenha;
    @FXML private Button        btnCadastrar;
    @FXML private Button        btnVoltar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnCadastrar.setOnAction(e -> onCadastrar());
        btnVoltar.setOnAction(e -> onVoltar());
    }

    private void onCadastrar() {
        try {
            String email    = txtEmail.getText().trim();
            String senha    = txtSenha.getText().trim();
            String confirma = txtConfirmarSenha.getText().trim();

            if (email.isEmpty())    throw new CampoVazioException("Email");
            if (senha.isEmpty())    throw new CampoVazioException("Senha");
            if (confirma.isEmpty()) throw new CampoVazioException("Confirmar Senha");
            if (!senha.equals(confirma)) throw new SenhasNaoCoincidemException();

            if (usuarioDAO.emailExiste(email)) {
                alerta(Alert.AlertType.WARNING, "Este e-mail já está cadastrado.");
                return;
            }

            Usuario novo = new Usuario(email, senha);
            usuarioDAO.inserir(novo);

            alerta(Alert.AlertType.INFORMATION, "Cadastro realizado! Faça o login.");
            App.setRoot("Login");

        } catch (CampoVazioException | SenhasNaoCoincidemException ex) {
            alerta(Alert.AlertType.WARNING, ex.getMessage());
        } catch (IOException ex) {
            alerta(Alert.AlertType.ERROR, "Erro ao redirecionar.");
        }
    }

    private void onVoltar() {
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
