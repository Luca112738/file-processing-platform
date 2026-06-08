package Exception;

public class SenhasNaoCoincidemException extends Exception {
    public SenhasNaoCoincidemException() {
        super("A senha e a confirmação não coincidem.");
    }
}
