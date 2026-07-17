import exceptions.InvalidCustomerIdException;
import exceptions.LoanProcessingException;
import interfaces.LoanRules;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static final LoanRules LOAN_RULES = DefaultLoanRules.getInstance();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Simple fixed-size in-memory storage for this console app.
        Loan[] loans = new Loan[5];
        int loanCount = 0;
        int choice;

        do {
            try {
                System.out.println("\n===== Loan Repayment Calculator =====");
                System.out.println("1. Add Loan Customer");
                System.out.println("2. Make Repayment");
                System.out.println("3. Display All Loans");
                System.out.println("4. Exit");
                choice = readIntInRange(scanner, "Choose option: ", 1, 4);

                // Route menu choices to the corresponding action.
                if (choice == 1) {
                    if (loanCount >= loans.length) {
                        System.out.println("Database is full. Cannot add more customers.");
                    } else {
                        String name = readValidName(scanner);
                        BigDecimal loanAmount = readPositiveAmount(scanner, "Enter loan amount: ");
                        BigDecimal paidAmount = readNonNegativeAmount(scanner);

                        try {
                            loans[loanCount] = new Loan(name, loanAmount, paidAmount);
                            if (loans[loanCount].isInitialOverpaymentAdjusted()) {
                                System.out.println("Excess payment has been refunded.");
                            }
                            System.out.println("Customer loan added successfully. Customer ID: " + loans[loanCount].getLoanId());
                            loanCount++;
                        } catch (LoanProcessingException ex) {
                            System.out.println("Error: " + ex.getMessage());
                        }
                    }
                } else if (choice == 2) {
                    if (loanCount == 0) {
                        System.out.println("No customers available. Add a loan first.");
                    } else {
                        System.out.println("Available customers for repayment (ID - Name):");
                        boolean hasOutstandingLoan = false;
                        for (int i = 0; i < loanCount; i++) {
                            if (loans[i].calculateRemainingBalance().compareTo(BigDecimal.ZERO) > 0) {
                                System.out.println(loans[i].getLoanId() + " - " + loans[i].getCustomerName());
                                hasOutstandingLoan = true;
                            }
                        }

                        if (!hasOutstandingLoan) {
                            System.out.println("No outstanding loans available for repayment.");
                            continue;
                        }

                        try {
                            int customerId = readPositiveInt(scanner, "Enter customer ID: ");
                            int index = findLoanIndexById(loans, loanCount, customerId);
                            BigDecimal payment = readRepaymentAmountByRegex(scanner, "Enter repayment amount: ");

                            boolean wasRefunded = loans[index].makePayment(payment);
                            if (wasRefunded) {
                                System.out.println("Excess payment has been refunded.");
                            }
                            System.out.println("Repayment updated successfully.");
                        } catch (LoanProcessingException ex) {
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
                            displayLoanInfo(loans[i]);
                        }
                    }
                } else if (choice == 4) {
                    System.out.println("Exiting program...");
                } else {
                    System.out.println("Invalid option. Please try again.");
                }
            } catch (LoanProcessingException ex) {
                System.out.println("Error: " + ex.getMessage());
                choice = 0;
            } catch (NoSuchElementException ex) {
                System.out.println("Input stream ended. Exiting program...");
                choice = 4;
            } catch (RuntimeException ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
                choice = 0;
            }
        } while (choice != 4);

        scanner.close();
    }

    private static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        // Shared validator for menu-like choices with a bounded integer range.
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

    private static int readPositiveInt(Scanner scanner, String prompt) {
        // Used for IDs and other integer-only positive input.
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Keep prompting until user enters a valid integer.
            }
            System.out.println("Invalid input. Please enter a positive integer.");
        }
    }

    private static BigDecimal readPositiveAmount(Scanner scanner, String prompt) {
        // Used for loan amounts that must be strictly greater than zero.
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Keep prompting until user enters a valid number.
            }
            System.out.println("Invalid amount. Please enter a positive number.");
        }
    }

    private static BigDecimal readNonNegativeAmount(Scanner scanner) {
        // Allows starting with 0 paid, but never a negative paid amount.
        while (true) {
            System.out.print("Enter initial paid amount: ");
            String input = scanner.nextLine().trim();
            try {
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) >= 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Keep prompting until user enters a valid number.
            }
            System.out.println("Invalid amount. Please enter a non-negative number.");
        }
    }

    private static String readValidName(Scanner scanner) {
        // Keep prompting until the name matches the configured regex rule.
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
        return LOAN_RULES.isValidCustomerName(name);
    }

    private static BigDecimal readRepaymentAmountByRegex(Scanner scanner, String prompt) {
        // Regex-driven parsing keeps repayment validation consistent with business rules.
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (LOAN_RULES.isValidRepaymentInput(input)) {
                return new BigDecimal(input);
            }
            System.out.println("Invalid amount. Repayment must be a positive number.");
        }
    }

    private static int findLoanIndexById(Loan[] loans, int loanCount, int loanId) {
        // Linear scan is enough for the small fixed-size in-memory list.
        for (int i = 0; i < loanCount; i++) {
            if (loans[i].getLoanId() == loanId) {
                return i;
            }
        }
        throw new InvalidCustomerIdException("Invalid customer ID.");
    }

    private static void displayLoanInfo(Loan loan) {
        System.out.println("Customer ID  : " + loan.getLoanId());
        System.out.println("Customer Name: " + loan.getCustomerName());
        System.out.println("Loan Amount  : " + loan.getLoanAmount());
        System.out.println("Paid Amount  : " + loan.getPaidAmount());
        System.out.println("Remaining    : " + loan.calculateRemainingBalance());
        System.out.println("Status       : " + loan.getLoanStatus());
    }
}

