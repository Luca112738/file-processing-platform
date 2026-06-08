package Controller;

import com.mycompany.br.fatec.tacoexpress.App;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

public class ConfiguracoesController implements Initializable {

    @FXML private Button btnEditarPerfil;
    @FXML private Button btnEditarEndereco;
    @FXML private Button btnSair;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnEditarPerfil.setOnAction(e   -> navegar("EditarPerfil"));
        btnEditarEndereco.setOnAction(e -> navegar("EditarEndereco"));
        btnSair.setOnAction(e           -> onSair());
    }

    private void navegar(String fxml) {
        try {
            App.setRoot(fxml);
        } catch (IOException ex) {
            alerta("Erro ao abrir " + fxml + ".");
        }
    }

    private void onSair() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Deseja realmente sair?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                LoginController.setUsuarioLogado(null);
                try {
                    App.setRoot("Login");
                } catch (IOException ex) {
                    alerta("Erro ao voltar ao login.");
                }
            }
        });
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
