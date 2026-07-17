package exceptions;

public class InvalidCustomerIdException extends LoanProcessingException {
    public InvalidCustomerIdException(String message) {
        super(message);
    }
}

