package exceptions;

public class InvalidAmountException extends LoanProcessingException {
    public InvalidAmountException(String message) {
        super(message);
    }
}

