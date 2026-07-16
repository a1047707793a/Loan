import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Loan[] loans = new Loan[5]; // Simulated local database in memory.
        int loanCount = 0;
        int choice;

        do {
            System.out.println("\n===== Loan Repayment Calculator =====");
            System.out.println("1. Add Loan Customer");
            System.out.println("2. Make Repayment");
            System.out.println("3. Display All Loans");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Clear newline after numeric input.

            if (choice == 1) {
                if (loanCount >= loans.length) {
                    System.out.println("Database is full. Cannot add more customers.");
                } else {
                    System.out.print("Enter customer name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter loan amount: ");
                    double loanAmount = scanner.nextDouble();

                    System.out.print("Enter initial paid amount: ");
                    double paidAmount = scanner.nextDouble();
                    scanner.nextLine();

                    loans[loanCount] = new Loan(name, loanAmount, paidAmount);
                    loanCount++;

                    System.out.println("Customer loan added successfully.");
                }
            } else if (choice == 2) {
                if (loanCount == 0) {
                    System.out.println("No customers available. Add a loan first.");
                } else {
                    System.out.println("Available customer indexes:");
                    for (int i = 0; i < loanCount; i++) {
                        System.out.println(i + " - " + loans[i].getCustomerName());
                    }

                    System.out.print("Enter customer index (0 to " + (loanCount - 1) + "): ");
                    int index = scanner.nextInt();

                    if (index >= 0 && index < loanCount) {
                        System.out.print("Enter repayment amount: ");
                        double payment = scanner.nextDouble();
                        scanner.nextLine();

                        loans[index].makePayment(payment);
                        System.out.println("Repayment updated successfully.");
                    } else {
                        System.out.println("Invalid index.");
                        scanner.nextLine();
                    }
                }
            } else if (choice == 3) {
                if (loanCount == 0) {
                    System.out.println("No loan records found.");
                } else {
                    System.out.println("\n--- All Loan Records ---");
                    for (int i = 0; i < loanCount; i++) {
                        System.out.println("\nRecord Index: " + i);
                        loans[i].displayLoanInfo();
                    }
                }
            } else if (choice == 4) {
                System.out.println("Exiting program...");
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        } while (choice != 4);

        scanner.close();
    }
}

