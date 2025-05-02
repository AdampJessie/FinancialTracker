package com.pluralsight;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FinancialTracker {
    private static final ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private static final String FILE_NAME = "transactions.csv";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

    public static void main(String[] args) {
        loadTransactions(FILE_NAME);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("-".repeat(30));
            System.out.println("Welcome to Financial Reality!");
            System.out.println("Please choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D":
                    addDeposit(scanner);
                    break;
                case "P":
                    addPayment(scanner);
                    break;
                case "L":
                    ledgerMenu(scanner);
                    break;
                case "X":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
        scanner.close();
    }

    private static void addDeposit(Scanner scanner) {
        /*
         This method should prompt the user to enter the date, time, description, vendor, and amount of a deposit.
         The user should enter the date and time in the following format: yyyy-MM-dd HH:mm:ss
         The amount should be a positive number.
         After validating the input, a new `Transaction` object should be created with the entered values.
         The new deposit should be added to the `transactions` ArrayList.
        */
        addTransaction(false, scanner);
    }
    private static void addPayment(Scanner scanner) {
        /*
         This method should prompt the user to enter the date, time, description, vendor, and amount of a payment.
         The user should enter the date and time in the following format: yyyy-MM-dd HH:mm:ss
         The amount received should be a positive number then transformed to a negative number.
         After validating the input, a new `Transaction` object should be created with the entered values.
         The new payment should be added to the `transactions` ArrayList.
        */
        addTransaction(true, scanner);
    }
    // The logic of addPayment/Deposit lies in addTransaction with a slight deviation when the input is made negative.
    public static void addTransaction(boolean isPayment, Scanner scanner) {
        while (true) {
            try {
                System.out.print("Please enter date (yyyy-MM-dd):");
                LocalDate inputDate = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);

                System.out.print("Please enter time (HH:mm:ss): ");
                LocalTime inputTime = LocalTime.parse(scanner.nextLine(), TIME_FORMATTER);

                System.out.print("Please enter description: ");
                String inputDescription = scanner.nextLine();
                if (inputDescription.isEmpty())
                    throw new IllegalArgumentException("Description cannot be empty! Please try again.");

                System.out.print("Please enter vendor: ");
                String inputVendor = scanner.nextLine();
                if (inputVendor.isEmpty())
                    throw new IllegalArgumentException("Vendor cannot be empty! Please try again.");

                System.out.print("Please enter amount: $");
                String amountString = scanner.nextLine();
                double amountDouble = Math.abs(Double.parseDouble(amountString));

                if (isPayment) amountDouble = -(amountDouble);
                Transaction transaction = new Transaction(inputDate, inputTime, inputDescription, inputVendor, amountDouble);
                transactions.add(transaction);
                transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                    writer.write(String.format("\n%s|%s|%s|%s|%s", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount()));
                }
                System.out.println("Success!");
                break;

            } catch (DateTimeException e) {
                System.out.println("Invalid date/time! Please try again.");
                System.out.println("-".repeat(40));

            } catch (NumberFormatException e) {
                System.out.println("Invalid amount! Please try again.");
                System.out.println("-".repeat(40));

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println("-".repeat(50));

            } catch (Exception e) {
                System.out.println("Something went wrong!\n" + e);
                System.out.println("-".repeat(50));

                break;
            }
        }
    }

    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("-".repeat(20));
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A":
                    displayLedger();
                    break;
                case "D":
                    displayDeposits();
                    break;
                case "P":
                    displayPayments();
                    break;
                case "R":
                    reportsMenu(scanner);
                    break;
                case "H":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }

    public static void loadTransactions(String fileName) {
        try {
            new File(fileName).createNewFile();
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("[|]");
                LocalDate date = LocalDate.parse(tokens[0]);
                LocalTime time = LocalTime.parse(tokens[1]);
                String description = tokens[2];
                String vendor = tokens[3];
                double amount = Double.parseDouble(tokens[4]);
                transactions.add(new Transaction(date, time, description, vendor, amount));
            }
            transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

            reader.close();
        } catch (IOException e) {
            System.err.println("File issue!");
        } catch (Exception e) {
            System.err.println("Something went wrong!\n" + e);
        }
    }

    private static void displayLedger() {
        // Enhanced for loop iterating and printing each element in the list.
        printColumn(true);
        for (Transaction transaction : transactions) {
            System.out.println(transaction + "\n+" + "-".repeat(94) + "+");
        }

    }
    private static void displayDeposits() {
        // Enhanced for loop iterating and printing each element in the list that is positive, i.e. Deposits.
        boolean found = false;
        for (Transaction transaction : transactions)
            if (transaction.getAmount() > 0) {
                found = true;
                break;
            }
        if (found) {
            printColumn(true);
            for (Transaction transaction : transactions) {
                if (transaction.getAmount() > 0) {
                    System.out.println(transaction + "\n+" + "-".repeat(94) + "+");
                }
            }
        } else printColumn(false);
    }
    private static void displayPayments() {
        // Enhanced for loop iterating and printing each element in the list that is negative, i.e. Payments.
        boolean found = false;
        for (Transaction transaction : transactions)
            if (transaction.getAmount() < 0) {
                found = true;
                break;
            }
        if (found) {
            printColumn(true);
            for (Transaction transaction : transactions) {
                if (transaction.getAmount() < 0) {
                    System.out.println(transaction + "\n+" + "-".repeat(94) + "+");
                }
            }
        } else printColumn(false);
    }

    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("-".repeat(20));
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);
                    filterTransactionsByDate(thisMonth, LocalDate.now());
                    break;
                case "2":
                    LocalDate lastMonth = LocalDate.now().minusMonths(1);
                    LocalDate firstOfLastMonth = lastMonth.withDayOfMonth(1);
                    LocalDate lastOfLastMonth = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());
                    filterTransactionsByDate(firstOfLastMonth, lastOfLastMonth);
                    break;
                case "3":
                    LocalDate thisYear = LocalDate.now().withDayOfYear(1);
                    filterTransactionsByDate(thisYear, LocalDate.now());
                    break;
                case "4":
                    LocalDate firstOfLastYear = LocalDate.now().minusYears(1).withDayOfYear(1);
                    LocalDate lastOfLastYear = LocalDate.now().minusYears(1).withDayOfYear(LocalDate.now().lengthOfYear());

                    filterTransactionsByDate(firstOfLastYear, lastOfLastYear);
                    break;
                case "5":
                    System.out.print("Please enter a vendor name: ");
                    String inputVendor = scanner.nextLine();
                    filterTransactionsByVendor(inputVendor);
                    break;
                case "6":
                    customSearch(scanner);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }
    private static void filterTransactionsByDate(LocalDate startDate, LocalDate endDate) {
        // Checks for dates inclusively (hence the ! operator) and prints elements if found.
        boolean found = false;
        for (Transaction transaction : transactions) {
            if (!transaction.getDate().isBefore(startDate) && !transaction.getDate().isAfter(endDate)) {
                    if (!found) printColumn(true);
                    found = true;
                    System.out.println(transaction + "\n+" + "-".repeat(94) + "+");
                }
            }
        if (!found) printColumn(false);
}
    private static void filterTransactionsByVendor(String vendor) {
        // Check and print similar to Filter Date method!
        boolean found = false;
        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                if (!found) printColumn(true);
                found = true;
                System.out.println(transaction + "\n+" + "-".repeat(94) + "+");
            }
        }
        if (!found) printColumn(false);
    }
    public static void customSearch(Scanner scanner) {
        // Initializing the search variables as null and accepting user input for search!
        LocalDate searchStartDate = null;
        System.out.print("Please enter a Start Date (yyyy-MM-DD): ");
        String inputStartDate = scanner.nextLine();

        LocalDate searchEndDate = null;
        System.out.print("Please enter an End Date (yyyy-MM-DD): ");
        String inputEndDate = scanner.nextLine();

        System.out.print("Please enter a description: ");
        String inputDescription = scanner.nextLine();

        System.out.print("Please enter a vendor: ");
        String inputVendor = scanner.nextLine();

        Double searchAmount = null;
        System.out.print("Please enter an amount: ");
        String inputAmount = scanner.nextLine();

     // Try to convert user inputs to relevant searchable data. If it doesn't work, try to find out why and inform the user!
        try {
            if (!inputStartDate.isEmpty()) {
                searchStartDate = LocalDate.parse(inputStartDate);

            }
            if (!inputEndDate.isEmpty()) {
                searchEndDate = LocalDate.parse(inputEndDate);
            }
            if (!inputAmount.isEmpty()) {
                searchAmount = Double.parseDouble(inputAmount);
            }
        } catch (DateTimeException e) {
            System.out.println("Invalid start/end date! Please try again or leave blank.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount! Please try again or leave blank.");
        } catch (Exception e) {
            System.out.println("Something went wrong!\n"+e);
        }
        // With all the data, start the search! Using opposite logic to exclude irrelevant dates and then print what's left!
        boolean found = false;
        for (Transaction transaction : transactions) {
            boolean matches = true;
            if (searchStartDate != null && transaction.getDate().isBefore(searchStartDate))
                matches = false;
            if (searchEndDate != null && transaction.getDate().isAfter(searchEndDate))
                matches = false;
            if (!inputDescription.isEmpty() && !inputDescription.equals(transaction.getDescription()))
                matches = false;
            if (!inputVendor.isEmpty() && !inputVendor.equals(transaction.getVendor()))
                matches = false;
            if (searchAmount != null && searchAmount != transaction.getAmount())
                matches = false;
            if (matches) {
                if (!found) printColumn(true);
                found = true;
                System.out.println(transaction + "\n+" + "-".repeat(94) + "+");
            }
        }
        if (!found) printColumn(false);
    }
    public static void printColumn(boolean columnHeading) {
        // Repeatable pretty formatting! Accepts parameter for lack of output!
        if (columnHeading) {
            System.out.println("+" + "-".repeat(94) + "+");
            System.out.printf("| %-10s | %-8s | %-30s | %-20s | %-12s |\n",
                    "Date", "Time", "Description", "Vendor", "Amount");
            System.out.println("+" + "-".repeat(94) + "+");
        } else System.out.println("-".repeat(20) + "\nNothing to display!");
    }
}



