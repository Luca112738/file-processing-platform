package Exception;

public class AutenticacaoException extends Exception {
    public AutenticacaoException() {
        super("E-mail ou senha incorretos.");
    }
}
