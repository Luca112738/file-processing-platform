package DAO;

import Exception.BancoDadosException;
import Model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> buscarTodas() {
        String sql = "SELECT * FROM categoria ORDER BY nome";
        List<Categoria> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Categoria(rs.getInt("id"), rs.getString("nome")));
            }
        } catch (SQLException e) {
            throw new BancoDadosException("buscar categorias", e);
        }
        return lista;
    }
}
