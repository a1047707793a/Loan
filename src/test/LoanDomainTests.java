package test;

import exceptions.InvalidAmountException;
import exceptions.InvalidCustomerNameException;
import exceptions.LoanAlreadyCompletedException;

import java.math.BigDecimal;

final class LoanDomainTests {
    void runAll() throws Exception {
        testCreateLoanNormalCase();
        testInitialOverpaymentBoundaryCase();
        testBlankNameInvalidInput();
        testNegativeLoanAmountInvalidInput();
        testMakePaymentNormalCase();
        testMakePaymentOverpaymentBoundaryCase();
        testCompletedLoanThrowsException();
    }

    private void testCreateLoanNormalCase() throws Exception {
        Object loan = ReflectionSupport.newLoan("Alice", "1000", "200");

        TestAssertions.assertEquals("Alice", ReflectionSupport.invokeNoArgs(loan, "getCustomerName"),
                "Loan should keep the customer name.");
        TestAssertions.assertBigDecimalEquals("1000", ReflectionSupport.invokeNoArgs(loan, "getLoanAmount"),
                "Loan amount should match constructor input.");
        TestAssertions.assertBigDecimalEquals("200", ReflectionSupport.invokeNoArgs(loan, "getPaidAmount"),
                "Paid amount should match constructor input.");
        TestAssertions.assertBigDecimalEquals("800", ReflectionSupport.invokeNoArgs(loan, "calculateRemainingBalance"),
                "Remaining balance should be calculated correctly.");
        TestAssertions.assertEquals("Outstanding", ReflectionSupport.invokeNoArgs(loan, "getLoanStatus"),
                "Loan status should be Outstanding when balance remains.");
    }

    private void testInitialOverpaymentBoundaryCase() throws Exception {
        Object loan = ReflectionSupport.newLoan("Alice", "1000", "1200");

        TestAssertions.assertBigDecimalEquals("1000", ReflectionSupport.invokeNoArgs(loan, "getPaidAmount"),
                "Initial overpayment should be capped at the loan amount.");
        TestAssertions.assertEquals(true, ReflectionSupport.invokeNoArgs(loan, "isInitialOverpaymentAdjusted"),
                "Initial overpayment should be marked as adjusted.");
        TestAssertions.assertEquals("Completed", ReflectionSupport.invokeNoArgs(loan, "getLoanStatus"),
                "Loan should be completed after capped full payment.");
    }

    private void testBlankNameInvalidInput() {
        TestAssertions.assertThrows(InvalidCustomerNameException.class,
                () -> ReflectionSupport.newLoan("   ", "1000", "0"),
                "Blank customer names must throw InvalidCustomerNameException.");
    }

    private void testNegativeLoanAmountInvalidInput() {
        TestAssertions.assertThrows(InvalidAmountException.class,
                () -> ReflectionSupport.newLoan("Alice", "-1", "0"),
                "Negative loan amount must throw InvalidAmountException.");
    }

    private void testMakePaymentNormalCase() throws Exception {
        Object loan = ReflectionSupport.newLoan("Alice", "1000", "200");

        Object refunded = ReflectionSupport.invoke(loan, "makePayment", new Class<?>[]{BigDecimal.class}, new BigDecimal("100"));

        TestAssertions.assertEquals(false, refunded, "Normal repayment should not trigger refund adjustment.");
        TestAssertions.assertBigDecimalEquals("300", ReflectionSupport.invokeNoArgs(loan, "getPaidAmount"),
                "Repayment should increase paid amount.");
        TestAssertions.assertBigDecimalEquals("700", ReflectionSupport.invokeNoArgs(loan, "calculateRemainingBalance"),
                "Repayment should reduce remaining balance.");
    }

    private void testMakePaymentOverpaymentBoundaryCase() throws Exception {
        Object loan = ReflectionSupport.newLoan("Alice", "1000", "950");

        Object refunded = ReflectionSupport.invoke(loan, "makePayment", new Class<?>[]{BigDecimal.class}, new BigDecimal("100"));

        TestAssertions.assertEquals(true, refunded, "Overpayment should trigger refund adjustment.");
        TestAssertions.assertBigDecimalEquals("1000", ReflectionSupport.invokeNoArgs(loan, "getPaidAmount"),
                "Paid amount should be capped at loan amount after overpayment.");
        TestAssertions.assertBigDecimalEquals("0", ReflectionSupport.invokeNoArgs(loan, "calculateRemainingBalance"),
                "Remaining balance should not go below zero.");
    }

    private void testCompletedLoanThrowsException() throws Exception {
        Object loan = ReflectionSupport.newLoan("Alice", "1000", "1000");

        TestAssertions.assertThrows(LoanAlreadyCompletedException.class,
                () -> ReflectionSupport.invoke(loan, "makePayment", new Class<?>[]{BigDecimal.class}, new BigDecimal("1")),
                "Completed loans must reject further payments.");
    }
}

