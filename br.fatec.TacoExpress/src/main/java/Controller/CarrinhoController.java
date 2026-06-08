package Controller;

import DAO.PedidoDAO;
import Exception.CarrinhoVazioException;
import Exception.CampoVazioException;
import Model.Carrinho;
import Model.ItemPedido;
import Model.Pedido;
import com.mycompany.br.fatec.tacoexpress.App;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class CarrinhoController implements Initializable {

    @FXML private Label              lblTotal;
    @FXML private Button             btnFinalizar;
    @FXML private ListView<ItemPedido> listaCarrinho;
    @FXML private TextField          txtEndereco;
    @FXML private ComboBox<String>   cmbPagamento;

    private final Carrinho   carrinho   = Carrinho.getInstance();
    private final PedidoDAO  pedidoDAO  = new PedidoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Métodos de pagamento
        cmbPagamento.setItems(FXCollections.observableArrayList(
            "PIX", "Cartão de Crédito", "Dinheiro"
        ));
        cmbPagamento.getSelectionModel().selectFirst();

        atualizarLista();

        btnFinalizar.setOnAction(e -> onFinalizar());
    }

    private void atualizarLista() {
        listaCarrinho.setItems(
            FXCollections.observableArrayList(carrinho.getItens())
        );
        lblTotal.setText(carrinho.getTotalFormatado());
    }

    private void onFinalizar() {
        try {
            if (carrinho.isEmpty()) throw new CarrinhoVazioException();

            String endereco = txtEndereco.getText().trim();
            if (endereco.isEmpty()) throw new CampoVazioException("Endereço de entrega");

            String metodo = cmbPagamento.getValue();

            Pedido pedido = new Pedido(
                LoginController.getUsuarioLogado().getId(),
                metodo,
                endereco,
                carrinho.getTotal(),
                carrinho.getItens()
            );

            pedidoDAO.inserir(pedido);
            carrinho.limpar();

            alerta(Alert.AlertType.INFORMATION, "Pedido realizado com sucesso!");
            App.setRoot("Cardapio");

        } catch (CarrinhoVazioException | CampoVazioException ex) {
            alerta(Alert.AlertType.WARNING, ex.getMessage());
        } catch (IOException ex) {
            alerta(Alert.AlertType.ERROR, "Erro ao redirecionar.");
        }
    }

    private void alerta(Alert.AlertType tipo, String msg) {
        Alert a = new Alert(tipo, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
