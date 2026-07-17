package exceptions;

public class LoanAlreadyCompletedException extends LoanProcessingException {
    public LoanAlreadyCompletedException(String message) {
        super(message);
    }
}

