package exceptions;

public class InvalidCustomerNameException extends LoanProcessingException {
    public InvalidCustomerNameException(String message) {
        super(message);
    }
}

