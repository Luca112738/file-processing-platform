package Controller;

import Model.Carrinho;
import Model.Produto;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ProdutoCardController implements Initializable {

    @FXML private Label  lblNome;
    @FXML private Label  lblDescricao;
    @FXML private Label  lblPreco;
    @FXML private Button btnAdicionar;

    private Produto produto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnAdicionar.setOnAction(e -> {
            if (produto != null) {
                Carrinho.getInstance().adicionar(produto);
            }
        });
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
        lblNome.setText(produto.getNome());
        lblDescricao.setText(produto.getDescricao());
        lblPreco.setText(produto.getPrecoFormatado());
    }
}
