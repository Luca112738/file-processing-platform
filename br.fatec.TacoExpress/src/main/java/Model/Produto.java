package Model;

public class Produto {

    private int    id;
    private int    idCategoria;
    private String nomeCategoria;   // preenchido via JOIN no DAO
    private String nome;
    private String descricao;
    private double preco;

    public Produto() {}

    public Produto(int idCategoria, String nome, String descricao, double preco) {
        this.idCategoria = idCategoria;
        this.nome        = nome;
        this.descricao   = descricao;
        this.preco       = preco;
    }

    public int    getId()                    { return id; }
    public void   setId(int id)              { this.id = id; }

    public int    getIdCategoria()           { return idCategoria; }
    public void   setIdCategoria(int id)     { this.idCategoria = id; }

    public String getNomeCategoria()         { return nomeCategoria; }
    public void   setNomeCategoria(String n) { this.nomeCategoria = n; }

    public String getNome()                  { return nome; }
    public void   setNome(String nome)       { this.nome = nome; }

    public String getDescricao()             { return descricao; }
    public void   setDescricao(String d)     { this.descricao = d; }

    public double getPreco()                 { return preco; }
    public void   setPreco(double preco)     { this.preco = preco; }

    public String getPrecoFormatado() {
        return String.format("R$ %.2f", preco).replace(".", ",");
    }

    @Override
    public String toString() { return nome; }
}
