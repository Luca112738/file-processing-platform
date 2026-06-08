package Model;

public class Usuario {

    private int id;
    private String email;
    private String senha;
    private String endereco;

    public Usuario() {}

    public Usuario(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public Usuario(int id, String email, String senha) {
        this.id    = id;
        this.email = email;
        this.senha = senha;
    }

    public int    getId()              { return id; }
    public void   setId(int id)        { this.id = id; }

    public String getEmail()           { return email; }
    public void   setEmail(String e)   { this.email = e; }

    public String getSenha()           { return senha; }
    public void   setSenha(String s)   { this.senha = s; }

    public String getEndereco()           { return endereco; }
    public void   setEndereco(String e)   { this.endereco = e; }

    @Override
    public String toString()           { return email; }
}
