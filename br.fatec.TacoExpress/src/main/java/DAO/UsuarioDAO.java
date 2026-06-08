package DAO;

import Exception.BancoDadosException;
import Model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void inserir(Usuario u) {
        String sql = "INSERT INTO usuario (email, senha) VALUES (?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getEmail());
            ps.setString(2, u.getSenha());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new BancoDadosException("inserir usuario", e);
        }
    }

    public void alterar(Usuario u) {
        String sql = "UPDATE usuario SET email = ?, senha = ?, endereco = ? WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, u.getEmail());
            ps.setString(2, u.getSenha());
            ps.setString(3, u.getEndereco());
            ps.setInt(4, u.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("alterar usuario", e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("excluir usuario", e);
        }
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new BancoDadosException("buscar usuario por id", e);
        }
        return null;
    }

    public List<Usuario> buscarTodos() {
        String sql = "SELECT * FROM usuario ORDER BY email";
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("buscar todos usuarios", e);
        }
        return lista;
    }

    /** Retorna o usuário se email e senha conferem, ou null caso contrário. */
    public Usuario autenticar(String email, String senha) {
        String sql = "SELECT * FROM usuario WHERE email = ? AND senha = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new BancoDadosException("autenticar usuario", e);
        }
        return null;
    }

    /** Verifica se já existe um cadastro com o e-mail informado. */
    public boolean emailExiste(String email) {
        String sql = "SELECT id FROM usuario WHERE email = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new BancoDadosException("verificar email existente", e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario(rs.getInt("id"), rs.getString("email"), rs.getString("senha"));
        u.setEndereco(rs.getString("endereco"));
        return u;
    }
}
