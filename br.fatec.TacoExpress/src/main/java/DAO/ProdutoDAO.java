package DAO;

import Exception.BancoDadosException;
import Model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void inserir(Produto p) {
        String sql = "INSERT INTO produto (id_categoria, nome, descricao, preco) VALUES (?,?,?,?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getIdCategoria());
            ps.setString(2, p.getNome());
            ps.setString(3, p.getDescricao());
            ps.setDouble(4, p.getPreco());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new BancoDadosException("inserir produto", e);
        }
    }

    public void alterar(Produto p) {
        String sql = "UPDATE produto SET id_categoria=?, nome=?, descricao=?, preco=? WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, p.getIdCategoria());
            ps.setString(2, p.getNome());
            ps.setString(3, p.getDescricao());
            ps.setDouble(4, p.getPreco());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("alterar produto", e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("excluir produto", e);
        }
    }

    public Produto buscarPorId(int id) {
        String sql = "SELECT p.*, c.nome AS nome_categoria "
                   + "FROM produto p JOIN categoria c ON c.id = p.id_categoria "
                   + "WHERE p.id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new BancoDadosException("buscar produto por id", e);
        }
        return null;
    }

    public List<Produto> buscarTodos() {
        String sql = "SELECT p.*, c.nome AS nome_categoria "
                   + "FROM produto p JOIN categoria c ON c.id = p.id_categoria "
                   + "ORDER BY c.nome, p.nome";
        return executarLista(sql);
    }

    public List<Produto> buscarPorCategoria(int idCategoria) {
        String sql = "SELECT p.*, c.nome AS nome_categoria "
                   + "FROM produto p JOIN categoria c ON c.id = p.id_categoria "
                   + "WHERE p.id_categoria = ? ORDER BY p.nome";
        List<Produto> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new BancoDadosException("buscar produtos por categoria", e);
        }
        return lista;
    }

    public List<Produto> buscarPorNome(String termo) {
        String sql = "SELECT p.*, c.nome AS nome_categoria "
                   + "FROM produto p JOIN categoria c ON c.id = p.id_categoria "
                   + "WHERE p.nome LIKE ? ORDER BY p.nome";
        List<Produto> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + termo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new BancoDadosException("buscar produtos por nome", e);
        }
        return lista;
    }

    private List<Produto> executarLista(String sql) {
        List<Produto> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("listar produtos", e);
        }
        return lista;
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setId(rs.getInt("id"));
        p.setIdCategoria(rs.getInt("id_categoria"));
        p.setNomeCategoria(rs.getString("nome_categoria"));
        p.setNome(rs.getString("nome"));
        p.setDescricao(rs.getString("descricao"));
        p.setPreco(rs.getDouble("preco"));
        return p;
    }
}
