package interfaces;

import java.math.BigDecimal;

public interface LoanRules {
    boolean isValidCustomerName(String name);

    boolean isValidRepaymentInput(String input);

    void validateCustomerName(String name);

    void validateLoanAmount(BigDecimal loanAmount);

    void validateInitialPaidAmount(BigDecimal paidAmount);

    BigDecimal normalizePaidAmount(BigDecimal loanAmount, BigDecimal paidAmount);

    BigDecimal calculateRemainingBalance(BigDecimal loanAmount, BigDecimal paidAmount);

    String determineLoanStatus(BigDecimal loanAmount, BigDecimal paidAmount);

    BigDecimal applyRepayment(BigDecimal loanAmount, BigDecimal currentPaidAmount, BigDecimal repaymentAmount);
}

