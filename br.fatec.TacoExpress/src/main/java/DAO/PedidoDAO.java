package DAO;

import Exception.BancoDadosException;
import Model.ItemPedido;
import Model.Pedido;
import Model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    /** Insere pedido e seus itens em uma única transação. */
    public void inserir(Pedido pedido) {
        String sqlPedido = "INSERT INTO pedido (id_usuario, data_hora, metodo_pagamento, endereco, total, status) "
                         + "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlItem   = "INSERT INTO item_pedido (id_pedido, id_produto, quantidade, preco_unit) "
                         + "VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Inserir pedido
                try (PreparedStatement ps = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, pedido.getIdUsuario());
                    ps.setTimestamp(2, Timestamp.valueOf(pedido.getDataHora()));
                    ps.setString(3, pedido.getMetodoPagamento());
                    ps.setString(4, pedido.getEndereco());
                    ps.setDouble(5, pedido.getTotal());
                    ps.setInt(6, pedido.getStatus());
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) pedido.setId(rs.getInt(1));
                    }
                }

                // 2. Inserir itens
                try (PreparedStatement ps = conn.prepareStatement(sqlItem)) {
                    for (ItemPedido item : pedido.getItens()) {
                        ps.setInt(1, pedido.getId());
                        ps.setInt(2, item.getProduto().getId());
                        ps.setInt(3, item.getQuantidade());
                        ps.setDouble(4, item.getProduto().getPreco());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw new BancoDadosException("inserir pedido (rollback)", e);
            }
        } catch (SQLException e) {
            throw new BancoDadosException("inserir pedido", e);
        }
    }

    public void atualizarStatus(int idPedido, int novoStatus) {
        String sql = "UPDATE pedido SET status = ? WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, novoStatus);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("atualizar status pedido", e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM pedido WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("excluir pedido", e);
        }
    }

    public Pedido buscarPorId(int id) {
        String sql = "SELECT * FROM pedido WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pedido p = mapear(rs);
                    p.setItens(buscarItens(id));
                    return p;
                }
            }
        } catch (SQLException e) {
            throw new BancoDadosException("buscar pedido por id", e);
        }
        return null;
    }

    public List<Pedido> buscarPorUsuario(int idUsuario) {
        String sql = "SELECT * FROM pedido WHERE id_usuario = ? ORDER BY data_hora DESC";
        List<Pedido> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new BancoDadosException("buscar pedidos por usuario", e);
        }
        return lista;
    }

    /** Consulta avançada com filtros opcionais (null = ignorar filtro). */
    public List<Pedido> buscarComFiltros(Integer idUsuario, Integer status, String metodoPagamento) {
        StringBuilder sql = new StringBuilder("SELECT * FROM pedido WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (idUsuario != null)                              { sql.append("AND id_usuario = ? ");        params.add(idUsuario); }
        if (status != null)                                 { sql.append("AND status = ? ");             params.add(status); }
        if (metodoPagamento != null && !metodoPagamento.isBlank()) { sql.append("AND metodo_pagamento = ? "); params.add(metodoPagamento); }

        sql.append("ORDER BY data_hora DESC");

        List<Pedido> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new BancoDadosException("buscar pedidos com filtros", e);
        }
        return lista;
    }

    private List<ItemPedido> buscarItens(int idPedido) throws SQLException {
        String sql = "SELECT ip.*, p.nome, p.descricao, p.preco, p.id_categoria "
                   + "FROM item_pedido ip JOIN produto p ON p.id = ip.id_produto "
                   + "WHERE ip.id_pedido = ?";
        List<ItemPedido> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produto prod = new Produto();
                    prod.setId(rs.getInt("id_produto"));
                    prod.setNome(rs.getString("nome"));
                    prod.setDescricao(rs.getString("descricao"));
                    prod.setPreco(rs.getDouble("preco"));
                    prod.setIdCategoria(rs.getInt("id_categoria"));

                    ItemPedido item = new ItemPedido(prod, rs.getInt("quantidade"));
                    item.setId(rs.getInt("id"));
                    item.setIdPedido(idPedido);
                    lista.add(item);
                }
            }
        }
        return lista;
    }

    private Pedido mapear(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getInt("id"));
        p.setIdUsuario(rs.getInt("id_usuario"));
        p.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        p.setMetodoPagamento(rs.getString("metodo_pagamento"));
        p.setEndereco(rs.getString("endereco"));
        p.setTotal(rs.getDouble("total"));
        p.setStatus(rs.getInt("status"));
        return p;
    }
}
