package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Carrinho de compras — armazenado apenas em memória (coleção).
 * Não persiste dados no banco de dados.
 * Implementado como Singleton para compartilhar estado entre controllers.
 */
public class Carrinho {

    private static Carrinho instancia;
    private final List<ItemPedido> itens;

    private Carrinho() {
        itens = new ArrayList<>();
    }

    public static Carrinho getInstance() {
        if (instancia == null) {
            instancia = new Carrinho();
        }
        return instancia;
    }

    // ── Adicionar item ────────────────────────────────────────────────────────
    // Se o produto já existe no carrinho, incrementa a quantidade
    public void adicionar(Produto produto) {
        Optional<ItemPedido> existente = itens.stream()
            .filter(i -> i.getProduto().getId() == produto.getId())
            .findFirst();

        if (existente.isPresent()) {
            existente.get().setQuantidade(existente.get().getQuantidade() + 1);
        } else {
            itens.add(new ItemPedido(produto, 1));
        }
    }

    // ── Remover item ──────────────────────────────────────────────────────────
    public void remover(Produto produto) {
        itens.removeIf(i -> i.getProduto().getId() == produto.getId());
    }

    // ── Alterar quantidade ────────────────────────────────────────────────────
    public void setQuantidade(Produto produto, int quantidade) {
        if (quantidade <= 0) {
            remover(produto);
            return;
        }
        itens.stream()
            .filter(i -> i.getProduto().getId() == produto.getId())
            .findFirst()
            .ifPresent(i -> i.setQuantidade(quantidade));
    }

    // ── Consultar ─────────────────────────────────────────────────────────────
    public List<ItemPedido> getItens()   { return itens; }

    public int getTotalItens() {
        return itens.stream().mapToInt(ItemPedido::getQuantidade).sum();
    }

    public double getTotal() {
        return itens.stream().mapToDouble(ItemPedido::getSubtotal).sum();
    }

    public String getTotalFormatado() {
        return String.format("Total: R$ %.2f", getTotal()).replace(".", ",");
    }

    public boolean isEmpty()             { return itens.isEmpty(); }

    // ── Limpar (após pedido finalizado) ───────────────────────────────────────
    public void limpar()                 { itens.clear(); }
}
