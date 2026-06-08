package Model;

import java.time.LocalDateTime;
import java.util.List;

public class Pedido {

    private int           id;
    private int           idUsuario;
    private LocalDateTime dataHora;
    private String        metodoPagamento;
    private String        endereco;
    private double        total;
    private int           status;
    private List<ItemPedido> itens;

    public Pedido() {
        this.dataHora = LocalDateTime.now();
        this.status   = 0;
    }

    public Pedido(int idUsuario, String metodoPagamento,
                  String endereco, double total, List<ItemPedido> itens) {
        this();
        this.idUsuario       = idUsuario;
        this.metodoPagamento = metodoPagamento;
        this.endereco        = endereco;
        this.total           = total;
        this.itens           = itens;
    }

    public int              getId()                       { return id; }
    public void             setId(int id)                 { this.id = id; }

    public int              getIdUsuario()                { return idUsuario; }
    public void             setIdUsuario(int id)          { this.idUsuario = id; }

    public LocalDateTime    getDataHora()                 { return dataHora; }
    public void             setDataHora(LocalDateTime dt) { this.dataHora = dt; }

    public String           getMetodoPagamento()          { return metodoPagamento; }
    public void             setMetodoPagamento(String mp) { this.metodoPagamento = mp; }

    public String           getEndereco()                 { return endereco; }
    public void             setEndereco(String e)         { this.endereco = e; }

    public double           getTotal()                    { return total; }
    public void             setTotal(double total)        { this.total = total; }

    public int              getStatus()                   { return status; }
    public void             setStatus(int status)         { this.status = status; }

    public List<ItemPedido> getItens()                    { return itens; }
    public void             setItens(List<ItemPedido> i)  { this.itens = i; }

    public String getStatusDescricao() {
        return switch (status) {
            case 0  -> "Pedido confirmado";
            case 1  -> "Pagamento aprovado";
            case 2  -> "Preparando pedido...";
            case 3  -> "Saiu para entrega";
            case 4  -> "Pedido entregue";
            default -> "Desconhecido";
        };
    }
}
