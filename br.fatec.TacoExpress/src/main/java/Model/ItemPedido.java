package Model;

public class ItemPedido {

    private int    id;
    private int    idPedido;
    private Produto produto;
    private int    quantidade;

    public ItemPedido() {}

    public ItemPedido(Produto produto, int quantidade) {
        this.produto    = produto;
        this.quantidade = quantidade;
    }

    public int     getId()                       { return id; }
    public void    setId(int id)                 { this.id = id; }

    public int     getIdPedido()                 { return idPedido; }
    public void    setIdPedido(int idPedido)     { this.idPedido = idPedido; }

    public Produto getProduto()                  { return produto; }
    public void    setProduto(Produto produto)   { this.produto = produto; }

    public int     getQuantidade()               { return quantidade; }
    public void    setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double  getSubtotal() {
        return produto != null ? produto.getPreco() * quantidade : 0;
    }

    @Override
    public String toString() {
        return produto.getNome() + " x" + quantidade
             + " - " + String.format("R$ %.2f", getSubtotal()).replace(".", ",");
    }
}
