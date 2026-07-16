public class Loan {
    private String customerName;
    private double loanAmount;
    private double paidAmount;

    public Loan(String customerName, double loanAmount, double paidAmount) {
        this.customerName = customerName;
        this.loanAmount = loanAmount;
        this.paidAmount = paidAmount;
    }

    public String getCustomerName() {
        return customerName;
    }

    // Returns how much is still unpaid.
    public double calculateRemainingBalance() {
        double remaining = loanAmount - paidAmount;
        if (remaining < 0) {
            remaining = 0;
            System.out.println("Excess payment has been refunded.");
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
        if (amount > 0) {
            paidAmount = paidAmount + amount;
            if (paidAmount > loanAmount) {
                paidAmount = loanAmount;
            }
        }
    }

    public void displayLoanInfo() {
        System.out.println("Customer Name: " + customerName);
        System.out.println("Loan Amount  : " + loanAmount);
        System.out.println("Paid Amount  : " + paidAmount);
        System.out.println("Remaining    : " + calculateRemainingBalance());
        System.out.println("Status       : " + getLoanStatus());
    }
}
