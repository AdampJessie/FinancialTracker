package com.pluralsight;

import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

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

    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
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
    /*  This method should load transactions from a file with the given file name
        If the file does not exist, it should be created
        The transactions should be stored in the `transactions` ArrayList
        Each line of the file represents a single transaction in the following format:
        <date>|<time>|<description>|<vendor>|<amount>
        For example: 2023-04-15|10:13:25|ergonomic keyboard|Amazon|-89.50
        After reading all the transactions, the file should be closed
        If any errors occur, an appropriate error message should be displayed
    */

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
         }           transactions.sort(Comparator.comparing(Transaction::getDate));

            reader.close();
        } catch (IOException e) {
            System.err.println("File issue!");
        } catch (Exception e) {
            System.err.println("Something went wrong!\n"+e);
        }
    }

    private static void displayLedger() {
        // This method should display a table of all transactions in the `transactions` ArrayList.
        // The table should have columns for date, time, description, vendor, and amount.
        printColumn(true);
        for (Transaction i : transactions) {
            System.out.println(i);
            System.out.println("+" + "-".repeat(90) + "+");
        }

    }

    private static void displayDeposits() {

        boolean found = false;
        for (Transaction i : transactions)
            if (i.getAmount() < 0) {
                found = true;
                break;
            }
        if (found) {
            printColumn(true);
            for (Transaction i : transactions) {
                if (i.getAmount() < 0) {
                    System.out.println(i);
                    System.out.println("+" + "-".repeat(90) + "+");
                }
            }
        }else printColumn(false);
    }

    private static void displayPayments() {
        // This method should display a table of all payments in the `transactions` ArrayList.
        // The table should have columns for date, time, description, vendor, and amount.
        boolean found = false;
        for (Transaction i : transactions)
            if (i.getAmount() < 0) {
                found = true;
                break;
            }
        if (found) {
            printColumn(true);
            for (Transaction i : transactions) {
                if (i.getAmount() < 0) {
                    System.out.println(i);
                    System.out.println("+" + "-".repeat(90) + "+");
                }
            }
        }else printColumn(false);
    }

    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    // Generate a report for all transactions within the current month,
                    // including the date, time, description, vendor, and amount for each transaction.
                    LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);
                    filterTransactionsByDate(thisMonth, LocalDate.now());
                    break;
                case "2":
                    // Generate a report for all transactions within the previous month,
                    // including the date, time, description, vendor, and amount for each transaction.
                    LocalDate lastMonth = LocalDate.now().minusMonths(1);
                    filterTransactionsByDate(lastMonth, LocalDate.now());
                    break;
                    case "3":
                    // Generate a report for all transactions within the current year,
                    // including the date, time, description, vendor, and amount for each transaction.
                    LocalDate thisYear = LocalDate.now().withDayOfYear(1);
                    filterTransactionsByDate(thisYear, LocalDate.now());
                    break;
                case "4":
                    // Generate a report for all transactions within the previous year,
                    // including the date, time, description, vendor, and amount for each transaction.
                    LocalDate lastYear = LocalDate.now().minusYears(1);
                    filterTransactionsByDate(lastYear, LocalDate.now());
                    break;




                case "5":
                    // Prompt the user to enter a vendor name, then generate a report for all transactions
                    // with that vendor, including the date, time, description, vendor, and amount for each transaction.
                    System.out.print("Please enter a vendor name: ");
                    String inputVendor = scanner.nextLine();
                    filterTransactionsByVendor(inputVendor);

                    break;



                case "0":
                    running = false;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    private static void filterTransactionsByDate(LocalDate startDate, LocalDate endDate) {
        /*
         This method filters the transactions by date and prints a report to the console.
         It takes two parameters: startDate and endDate, which represent the range of dates to filter by.
         The method loops through the transactions list and checks each transaction's date against the date range.
         Transactions that fall within the date range are printed to the console.
         If no transactions fall within the date range, the method prints a message indicating that there are no results.
        */

        boolean found = false;
        for (Transaction i : transactions)
            if (!i.getDate().isBefore(startDate) && !i.getDate().isAfter(endDate)) {
                found = true;
                break;
            }

        if (found) {
            printColumn(true);
            for (Transaction i : transactions) {
                if (!i.getDate().isBefore(startDate) && !i.getDate().isAfter(endDate)) {
                    System.out.println(i);
                    System.out.println("+" + "-".repeat(90) + "+");

                }
            }

        }else printColumn(false);
    }

    private static void filterTransactionsByVendor(String vendor) {
        // This method filters the transactions by vendor and prints a report to the console.
        // It takes one parameter: vendor, which represents the name of the vendor to filter by.
        // The method loops through the transactions list and checks each transaction's vendor name against the specified vendor name.
        // Transactions with a matching vendor name are printed to the console.
        // If no transactions match the specified vendor name, the method prints a message indicating that there are no results.



        boolean found = false;
        for (Transaction i : transactions)
            if (i.getVendor().equalsIgnoreCase(vendor)) {
                found = true;
                break;
            }

        if (found) {
            printColumn(true);
            for (Transaction i : transactions) {
                if (i.getVendor().equalsIgnoreCase(vendor)) {
                    System.out.println(i);
                    System.out.println("+" + "-".repeat(90) + "+");

                }
            }

        }else printColumn(false);
    }

    public static void printColumn(boolean columnHeading) {

        if (columnHeading) {

            System.out.println("+" + "-".repeat(90) + "+");
            System.out.printf("| %-10s | %-8s | %-30s | %-20s | %-8s |\n",
                    "Date", "Time", "Description", "Vendor", "Amount");
            System.out.println("+" + "-".repeat(90) + "+");
        }else System.out.println("Nothing to display!\n"+"-".repeat(20));;

    }

    public static void addTransaction(boolean isPayment, Scanner scanner) {
        while (true) {
            try {

                System.out.print("Please enter date (yyyy-MM-dd):");
                LocalDate inputDate = LocalDate.parse(scanner.nextLine());
                System.out.print("Please enter time (HH:mm:ss): ");
                LocalTime inputTime = LocalTime.parse(scanner.nextLine());
                System.out.print("Please enter description: ");
                String inputDescription = scanner.nextLine();
                System.out.print("Please enter vendor: ");
                String inputVendor = scanner.nextLine();
                System.out.print("Please enter amount: $");
                String amountString = scanner.nextLine();
                double amountDouble = Math.abs(Double.parseDouble(amountString));

                if (isPayment) amountDouble = -(amountDouble);
                Transaction i = new Transaction(inputDate, inputTime, inputDescription, inputVendor, amountDouble);
                transactions.add(i);


                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                     writer.write(String.format("\n%s|%s|%s|%s|%s",i.getDate(),i.getTime(),i.getDescription(),i.getVendor(),i.getAmount()));
                }
                    System.out.println("Success!");
                    break;
            }catch (DateTimeException e) {
                    System.out.println("Invalid date/time! Please try again.");
                } catch (NumberFormatException e) {
                    System.err.println("Invalid amount! Please try again.");
                    System.out.println();
            } catch (Exception e) {
                    System.err.println("Something went wrong!\n" + e);
                    break;
            }
        }
    }
}
