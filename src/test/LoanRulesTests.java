package test;

import exceptions.InvalidAmountException;
import exceptions.InvalidCustomerNameException;
import exceptions.LoanAlreadyCompletedException;

import java.math.BigDecimal;

final class LoanRulesTests {
    void runAll() throws Exception {
        testValidCustomerNameNormalCase();
        testInvalidRepaymentInputInvalidCase();
        testNormalizePaidAmountBoundaryCase();
        testValidateCustomerNameThrowsException();
        testApplyRepaymentRejectsZeroAmount();
        testApplyRepaymentRejectsCompletedLoan();
    }

    private void testValidCustomerNameNormalCase() throws Exception {
        Object rules = ReflectionSupport.getDefaultLoanRules();
        Object result = ReflectionSupport.invoke(rules, "isValidCustomerName", new Class<?>[]{String.class}, "Alice Smith");

        TestAssertions.assertEquals(true, result, "Valid customer names should be accepted.");
    }

    private void testInvalidRepaymentInputInvalidCase() throws Exception {
        Object rules = ReflectionSupport.getDefaultLoanRules();
        Object result = ReflectionSupport.invoke(rules, "isValidRepaymentInput", new Class<?>[]{String.class}, "0");

        TestAssertions.assertEquals(false, result, "Zero repayment input should be rejected.");
    }

    private void testNormalizePaidAmountBoundaryCase() throws Exception {
        Object rules = ReflectionSupport.getDefaultLoanRules();
        Object normalized = ReflectionSupport.invoke(rules, "normalizePaidAmount",
                new Class<?>[]{BigDecimal.class, BigDecimal.class}, new BigDecimal("1000"), new BigDecimal("1200"));

        TestAssertions.assertBigDecimalEquals("1000", normalized,
                "normalizePaidAmount should cap paid amount at the loan amount.");
    }

    private void testValidateCustomerNameThrowsException() throws Exception {
        Object rules = ReflectionSupport.getDefaultLoanRules();

        TestAssertions.assertThrows(InvalidCustomerNameException.class,
                () -> ReflectionSupport.invoke(rules, "validateCustomerName", new Class<?>[]{String.class}, " "),
                "Invalid names must throw InvalidCustomerNameException.");
    }

    private void testApplyRepaymentRejectsZeroAmount() throws Exception {
        Object rules = ReflectionSupport.getDefaultLoanRules();

        TestAssertions.assertThrows(InvalidAmountException.class,
                () -> ReflectionSupport.invoke(rules, "applyRepayment",
                        new Class<?>[]{BigDecimal.class, BigDecimal.class, BigDecimal.class},
                        new BigDecimal("1000"), new BigDecimal("100"), BigDecimal.ZERO),
                "Zero repayment should throw InvalidAmountException.");
    }

    private void testApplyRepaymentRejectsCompletedLoan() throws Exception {
        Object rules = ReflectionSupport.getDefaultLoanRules();

        TestAssertions.assertThrows(LoanAlreadyCompletedException.class,
                () -> ReflectionSupport.invoke(rules, "applyRepayment",
                        new Class<?>[]{BigDecimal.class, BigDecimal.class, BigDecimal.class},
                        new BigDecimal("1000"), new BigDecimal("1000"), new BigDecimal("1")),
                "Completed loans should throw LoanAlreadyCompletedException when repaid again.");
    }
}

