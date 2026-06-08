package Exception;

public class CarrinhoVazioException extends Exception {
    public CarrinhoVazioException() {
        super("Adicione pelo menos um item ao carrinho antes de finalizar.");
    }
}
