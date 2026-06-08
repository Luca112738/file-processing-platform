package Exception;

public class BancoDadosException extends RuntimeException {
    public BancoDadosException(String operacao, Throwable causa) {
        super("Erro ao executar \"" + operacao + "\": " + causa.getMessage(), causa);
    }
}
