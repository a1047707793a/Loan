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
            choice = readIntInRange(scanner, "Choose option: ", 1, 4);

            if (choice == 1) {
                if (loanCount >= loans.length) {
                    System.out.println("Database is full. Cannot add more customers.");
                } else {
                    String name = readValidName(scanner);
                    double loanAmount = readPositiveDouble(scanner, "Enter loan amount: ");
                    double paidAmount = readNonNegativeDouble(scanner);

                    try {
                        loans[loanCount] = new Loan(name, loanAmount, paidAmount);
                        loanCount++;
                        System.out.println("Customer loan added successfully.");
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
            } else if (choice == 2) {
                if (loanCount == 0) {
                    System.out.println("No customers available. Add a loan first.");
                } else {
                    System.out.println("Available customer indexes:");
                    for (int i = 0; i < loanCount; i++) {
                        System.out.println(i + " - " + loans[i].getCustomerName());
                    }

                    int index = readIntInRange(scanner,
                            "Enter customer index (0 to " + (loanCount - 1) + "): ", 0, loanCount - 1);
                    double payment = readPositiveDouble(scanner, "Enter repayment amount: ");

                    try {
                        loans[index].makePayment(payment);
                        System.out.println("Repayment updated successfully.");
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Error: " + ex.getMessage());
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

    private static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Keep prompting until user enters a valid integer.
            }
            System.out.println("Invalid input. Please enter a number from " + min + " to " + max + ".");
        }
    }

    private static double readPositiveDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (Double.isFinite(value) && value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Keep prompting until user enters a valid number.
            }
            System.out.println("Invalid amount. Please enter a positive number.");
        }
    }

    private static double readNonNegativeDouble(Scanner scanner) {
        while (true) {
            System.out.print("Enter initial paid amount: ");
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (Double.isFinite(value) && value >= 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Keep prompting until user enters a valid number.
            }
            System.out.println("Invalid amount. Please enter a non-negative number.");
        }
    }

    private static String readValidName(Scanner scanner) {
        while (true) {
            System.out.print("Enter customer name: ");
            String name = scanner.nextLine().trim();
            if (isValidName(name)) {
                return name;
            }
            System.out.println("Invalid name. Use letters and spaces only.");
        }
    }

    private static boolean isValidName(String name) {
        if (name.isEmpty()) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetter(c) && c != ' ') {
                return false;
            }
        }
        return true;
    }
}

