package Controller;

import DAO.ProdutoDAO;
import Model.Carrinho;
import Model.Produto;
import com.mycompany.br.fatec.tacoexpress.App;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class CardapioController implements Initializable {

    @FXML private Button btnTodos;
    @FXML private Button btnTacos;
    @FXML private Button btnBurritos;
    @FXML private Button btnQuesadillas;
    @FXML private Button btnHome;
    @FXML private Button btnCarrinho;
    @FXML private Button btnConfig;
    @FXML private VBox   containerProdutos;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    // IDs fixos das categorias (conforme banco.sql)
    private static final int ID_TACOS       = 1;
    private static final int ID_BURRITOS    = 2;
    private static final int ID_QUESADILLAS = 3;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnTodos.setOnAction(e       -> carregarProdutos(null));
        btnTacos.setOnAction(e       -> carregarProdutos(ID_TACOS));
        btnBurritos.setOnAction(e    -> carregarProdutos(ID_BURRITOS));
        btnQuesadillas.setOnAction(e -> carregarProdutos(ID_QUESADILLAS));

        btnHome.setOnAction(e    -> carregarProdutos(null));
        btnCarrinho.setOnAction(e -> navegar("Carrinho"));
        btnConfig.setOnAction(e  -> navegar("Configuracoes"));

        carregarProdutos(null);
    }

    private void carregarProdutos(Integer idCategoria) {
        containerProdutos.getChildren().clear();

        List<Produto> lista = idCategoria == null
            ? produtoDAO.buscarTodos()
            : produtoDAO.buscarPorCategoria(idCategoria);

        for (Produto p : lista) {
            try {
                FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("/View/ProdutoCard.fxml")
                );
                javafx.scene.Node card = loader.load();
                ProdutoCardController ctrl = loader.getController();
                ctrl.setProduto(p);
                containerProdutos.getChildren().add(card);
            } catch (IOException ex) {
                alerta("Erro ao carregar card do produto: " + p.getNome());
            }
        }
    }

    private void navegar(String fxml) {
        try {
            App.setRoot(fxml);
        } catch (IOException ex) {
            alerta("Erro ao navegar para " + fxml);
        }
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
