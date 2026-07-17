package exceptions;

public class LoanProcessingException extends RuntimeException {
    public LoanProcessingException(String message) {
        super(message);
    }
}

