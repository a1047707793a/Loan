import java.util.regex.Pattern;

public class Loan {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}]+(?: [\\p{L}]+)*$");

    private static int nextLoanId = 1;

    private final int loanId;
    private final String customerName;
    private final double loanAmount;
    private double paidAmount;

    public Loan(String customerName, double loanAmount, double paidAmount) {
        if (customerName == null || customerName.trim().isEmpty() || !isValidName(customerName.trim())) {
            throw new IllegalArgumentException("Customer name must contain letters and spaces only.");
        }
        if (!Double.isFinite(loanAmount) || loanAmount <= 0) {
            throw new IllegalArgumentException("Loan amount must be a positive number.");
        }
        if (!Double.isFinite(paidAmount) || paidAmount < 0) {
            throw new IllegalArgumentException("Initial paid amount must be a non-negative number.");
        }

        this.loanId = nextLoanId++;
        this.customerName = customerName.trim();
        this.loanAmount = loanAmount;
        this.paidAmount = paidAmount;

        if (this.paidAmount > this.loanAmount) {
            this.paidAmount = this.loanAmount;
            System.out.println("Excess payment has been refunded.");
        }
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getLoanId() {
        return loanId;
    }

    // Returns how much is still unpaid.
    public double calculateRemainingBalance() {
        double remaining = loanAmount - paidAmount;
        if (remaining < 0) {
            remaining = 0;
        }
        return remaining;
    }

    // Returns loan status based on remaining balance.
    public String getLoanStatus() {
        if (calculateRemainingBalance() == 0) {
            return "Completed";
        } else {
            return "Outstanding";
        }
    }

    // Adds repayment amount to paid amount.
    public void makePayment(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Repayment amount must be a positive number.");
        }

        paidAmount = paidAmount + amount;
        if (paidAmount > loanAmount) {
            paidAmount = loanAmount;
            System.out.println("Excess payment has been refunded.");
        }
    }

    private static boolean isValidName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    public void displayLoanInfo() {
        System.out.println("Customer ID  : " + loanId);
        System.out.println("Customer Name: " + customerName);
        System.out.println("Loan Amount  : " + loanAmount);
        System.out.println("Paid Amount  : " + paidAmount);
        System.out.println("Remaining    : " + calculateRemainingBalance());
        System.out.println("Status       : " + getLoanStatus());
    }
}
