import exceptions.InvalidAmountException;
import exceptions.InvalidCustomerNameException;
import exceptions.LoanAlreadyCompletedException;
import interfaces.LoanRules;

import java.math.BigDecimal;

public final class DefaultLoanRules implements LoanRules {
    private static final DefaultLoanRules INSTANCE = new DefaultLoanRules();

    private DefaultLoanRules() {
        // Singleton keeps one reusable default rules implementation.
    }

    public static LoanRules getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValidCustomerName(String name) {
        return name != null
                && !name.trim().isEmpty()
                && ValidationPatterns.NAME_PATTERN.matcher(name.trim()).matches();
    }

    @Override
    public boolean isValidRepaymentInput(String input) {
        return input != null && ValidationPatterns.POSITIVE_AMOUNT_PATTERN.matcher(input.trim()).matches();
    }

    @Override
    public void validateCustomerName(String name) {
        if (!isValidCustomerName(name)) {
            throw new InvalidCustomerNameException("Customer name must contain letters and spaces only.");
        }
    }

    @Override
    public void validateLoanAmount(BigDecimal loanAmount) {
        if (loanAmount == null || loanAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Loan amount must be a positive number.");
        }
    }

    @Override
    public void validateInitialPaidAmount(BigDecimal paidAmount) {
        if (paidAmount == null || paidAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Initial paid amount must be a non-negative number.");
        }
    }

    @Override
    public BigDecimal normalizePaidAmount(BigDecimal loanAmount, BigDecimal paidAmount) {
        validateLoanAmount(loanAmount);
        validateInitialPaidAmount(paidAmount);
        if (paidAmount.compareTo(loanAmount) > 0) {
            return loanAmount;
        }
        return paidAmount;
    }

    @Override
    public BigDecimal calculateRemainingBalance(BigDecimal loanAmount, BigDecimal paidAmount) {
        validateLoanAmount(loanAmount);
        validateInitialPaidAmount(paidAmount);
        BigDecimal remaining = loanAmount.subtract(paidAmount);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return remaining;
    }

    @Override
    public String determineLoanStatus(BigDecimal loanAmount, BigDecimal paidAmount) {
        if (calculateRemainingBalance(loanAmount, paidAmount).compareTo(BigDecimal.ZERO) == 0) {
            return "Completed";
        }
        return "Outstanding";
    }

    @Override
    public BigDecimal applyRepayment(BigDecimal loanAmount, BigDecimal currentPaidAmount, BigDecimal repaymentAmount) {
        validateLoanAmount(loanAmount);
        validateInitialPaidAmount(currentPaidAmount);
        if (repaymentAmount == null || repaymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Repayment amount must be a positive number.");
        }
        if (calculateRemainingBalance(loanAmount, currentPaidAmount).compareTo(BigDecimal.ZERO) == 0) {
            throw new LoanAlreadyCompletedException("This loan is already completed.");
        }

        BigDecimal updatedPaidAmount = currentPaidAmount.add(repaymentAmount);
        return normalizePaidAmount(loanAmount, updatedPaidAmount);
    }
}

