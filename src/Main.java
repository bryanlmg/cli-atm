import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;
import java.util.stream.*;

public class Main {
    // ANSI escape codes for a colorful ATM menu
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public static void main(String[] args) throws InterruptedException {
        final int MAX_ACCOUNTS = 15;
        int totalNumOfAccounts;
        char choice;
        Scanner scanner = new Scanner(System.in);

        // instantiate parallel arrays with data from file
        int[] accountNumbers = new int[MAX_ACCOUNTS];
        double[] accountBalances = new double[MAX_ACCOUNTS];
        totalNumOfAccounts = readBankAccounts(accountNumbers, accountBalances);
        System.out.println(ANSI_GREEN + "Successfully read in " + totalNumOfAccounts + " accounts.");
        System.out.println("Now printing the initial customer database..." + ANSI_RESET);

        // prints the initial customer database
        printCustomerDatabaseTable(accountNumbers, accountBalances, totalNumOfAccounts);

        // printing main menu
        System.out.println();
        System.out.println(ANSI_YELLOW + "Please select an option from the main menu below.\n"
                        + ANSI_RESET);
        printMainMenu();
        System.out.println();

        // getting user input choice
        do {
            choice = scanner.next().charAt(0);
            switch (choice) {
                case 'W':
                case 'w':
                    withdraw(accountNumbers, accountBalances, totalNumOfAccounts, scanner);
                    break;

                case 'D':
                case 'd':
                    deposit(accountNumbers, accountBalances, totalNumOfAccounts, scanner);
                    break;

                case 'N':
                case 'n':
                    createNewAccount(accountNumbers, accountBalances, totalNumOfAccounts, scanner);
                    break;

                case 'B':
                case 'b':
                    showBalance(accountNumbers, accountBalances, totalNumOfAccounts, scanner);
                    break;

                case 'X':
                case 'x':
                    deleteAccount(accountNumbers, accountBalances, totalNumOfAccounts, scanner);
                    break;

                case 'Q':
                case 'q':
                    break;

                default:
                    System.out.println(ANSI_RED + "Incorrect option." + ANSI_RESET);
            }

            if (choice != 'q' && choice != 'Q') {
                System.out.println(ANSI_YELLOW + "Please select an option from the main menu below.\n"
                        + ANSI_RESET);
                printMainMenu();
            }
        } while(choice != 'q' && choice != 'Q');


        System.out.println(ANSI_YELLOW + "Printing updated customer database..." + ANSI_RESET);
        printCustomerDatabaseTable(accountNumbers, accountBalances, totalNumOfAccounts);
    }

    /**
     * Existing customer account information is read into a pair of parallel arrays
     * from a .txt file.
     *
     * @param accountNumbers  An integer array containing customer account numbers.
     * @param accountBalances A double array containing customer account balances.
     * @return Total successful number of accounts read in.
     */
    private static int readBankAccounts(int[] accountNumbers, double[] accountBalances) {
        int totalAccountsRead = 0;
        int counter = 0;

        try {
            Scanner scanner = new Scanner(new File("/Users/bryan/IdeaProjects/ATM/src/database.txt"));
            while (scanner.hasNextInt() && scanner.hasNextDouble()) {
                accountNumbers[counter] = scanner.nextInt();
                accountBalances[counter] = scanner.nextDouble();
                counter++;
                totalAccountsRead++;
            }

        } catch(FileNotFoundException e) {
            System.out.println(ANSI_RED + "FileNotFoundException thrown." + ANSI_RESET);
            e.printStackTrace();
        }
        return totalAccountsRead;
    }

    /**
     * Searches linearly for an account number in the accountNumbers array.
     * @param accountNumbers An integer array containing customer account numbers.
     * @param numOfAccounts Total number of successful accounts read in.
     * @param accountNumber The account number being sought.
     * @return The index of the account in the accountNumbers array if it exists, else -1 if it does not.
     */
    private static int findAccount(int[] accountNumbers, int numOfAccounts, int accountNumber) {
        for (int i = 0; i < numOfAccounts; i++) {
            if (accountNumbers[i] == accountNumber) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Prompts the user to enter an amount to withdraw from an existing account.
     * @param accountNumbers An integer array containing customer account numbers.
     * @param accountBalances A double array containing customer account balances.
     * @param numOfAccounts Total number of successful accounts read in.
     * @param scanner Scanner object passed as an argument to get user input.
     */
    private static void withdraw(int[] accountNumbers, double[] accountBalances, int numOfAccounts,
                                   Scanner scanner) {
        int accountNumber, accountIndex;
        double amountToWithdraw;

        System.out.println(ANSI_YELLOW + "\nPlease enter the account number: " + ANSI_RESET);
        accountNumber = scanner.nextInt();
        accountIndex = findAccount(accountNumbers, numOfAccounts, accountNumber);

        while (accountIndex == -1) {
            System.out.println(ANSI_RED + "Account does not exist or the input is invalid. " +
                                            "Please try again: " + ANSI_RESET);
            accountNumber = scanner.nextInt();
            accountIndex = findAccount(accountNumbers, numOfAccounts, accountNumber);
        }

        System.out.println(ANSI_YELLOW + "\nEnter withdrawal amount: " + ANSI_RESET);
        amountToWithdraw = scanner.nextDouble();
        printWithdrawalTransactionMsg(accountBalances, amountToWithdraw, accountNumber, accountIndex,
                !(amountToWithdraw > accountBalances[accountIndex]));
    }

    private static void printWithdrawalTransactionMsg(double[] accountBalances, double amountToWithdraw,
                                            int accountNumber, int accountIndex, boolean isSuccessful) {
        if (!isSuccessful) {
            System.out.println(ANSI_BLUE + "\n==============================" + ANSI_RESET);
            System.out.println(ANSI_YELLOW + "Transaction Type: WITHDRAW");
            System.out.println("Account Number: " + accountNumber);
            System.out.printf("Current Balance: $%.2f", accountBalances[accountIndex]);
            System.out.printf("\nAmount to Withdraw: $%.2f", amountToWithdraw);
            System.out.println(ANSI_RESET +
                    ANSI_RED + "\nERROR: Insufficient Funds - Transaction Voided"
                    + ANSI_RESET);
            System.out.println(ANSI_BLUE + "==============================" + ANSI_RESET);
            System.out.println("\nReturning to main menu...\n");
        } else {
            System.out.println(ANSI_BLUE + "\n==============================" + ANSI_RESET);
            System.out.println(ANSI_YELLOW + "Transaction Type: WITHDRAW");
            System.out.println("Account Number: " + accountNumber);
            System.out.printf("Current Balance: $%.2f", accountBalances[accountIndex]);
            System.out.printf("\nAmount to Withdraw: $%.2f", amountToWithdraw);
            System.out.print(ANSI_RESET);
            accountBalances[accountIndex] -= amountToWithdraw;
            System.out.printf(ANSI_GREEN + "\nNew Balance: $%.2f", accountBalances[accountIndex]);
            System.out.println(ANSI_RESET + ANSI_BLUE + "\n==============================" + ANSI_RESET);
            System.out.println("\nReturning to main menu...\n");
        }

    }
    private static void deposit(int[] accountNumbers, double[] accountBalances, int numOfAccounts,
                                Scanner scanner) {
        int accountNumber, accountIndex;
        double amountToDeposit;

        System.out.println(ANSI_YELLOW + "Please enter the account number: " + ANSI_RESET);
        accountNumber = scanner.nextInt();
        accountIndex = findAccount(accountNumbers, numOfAccounts, accountNumber);

        if (accountIndex == -1) {
            System.out.println(ANSI_RED + "Account does not exist!" + ANSI_RESET);
        } else {
            System.out.println(ANSI_YELLOW + "Enter deposit amount: " + ANSI_RESET);
            amountToDeposit = scanner.nextDouble();
            accountBalances[accountIndex] += amountToDeposit;
        }
    }

    private static void showBalance(int[] accountNumbers, double[] accountBalances, int numOfAccounts,
                                    Scanner scanner) throws InterruptedException {
        int accountNumber, accountIndex;

        System.out.println(ANSI_YELLOW + "Please enter the account number: " + ANSI_RESET);
        accountNumber = scanner.nextInt();
        accountIndex = findAccount(accountNumbers, numOfAccounts, accountNumber);

        while (accountIndex == -1) {
            System.out.println(ANSI_RED + "Account does not exist or the input is invalid. " +
                               "Please try again: " + ANSI_RESET);
            accountNumber = scanner.nextInt();
            accountIndex = findAccount(accountNumbers, numOfAccounts, accountNumber);
        }
        System.out.println(ANSI_GREEN + "\nAccount found! Printing details now..." + ANSI_RESET);
        System.out.println(ANSI_BLUE + "\n==============================" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "Transaction Type: View Balance");
        System.out.println("Account Number: " + accountNumber);
        System.out.printf("Current Total Balance: " + ANSI_RESET + ANSI_GREEN +
                          "$%.2f", accountBalances[accountIndex]);
        System.out.println(ANSI_RESET + ANSI_BLUE + "\n==============================" + ANSI_RESET + "\n");
        System.out.println(ANSI_YELLOW + "Returning to main menu...\n" + ANSI_RESET);
        Thread.sleep(1000);
    }

    private static int createNewAccount(int[] accountNumbers, double[] accountBalances, int numOfAccounts,
                                        Scanner scanner) {
        int newAccountNumber, accountIndex;
        int newNumOfAccounts = numOfAccounts;
        String answer;

        do {
            System.out.println(ANSI_RED + "Quit? Enter 'Y/y':" + ANSI_RESET);
            answer = scanner.next();

            if (answer.equalsIgnoreCase("y")) {
                break;
            } else {
                System.out.println(ANSI_YELLOW + "Please enter a 6-digit non-existing account number " +
                        "ranging from 100,000 to 999,999: " + ANSI_RESET);
                newAccountNumber = scanner.nextInt();

                if(newAccountNumber < 100000 || newAccountNumber > 999999) {
                    System.out.println(ANSI_RED + "Invalid input. Please try again: " + ANSI_RESET);
                    newAccountNumber = scanner.nextInt();
                } else {
                    accountIndex = findAccount(accountNumbers,numOfAccounts, newAccountNumber);

                    if (accountIndex == -1) { // new acct number does not exist
                        accountNumbers = Arrays.copyOf(accountNumbers, accountNumbers.length + 1);
                        accountBalances = Arrays.copyOf(accountBalances, accountBalances.length + 1);
                        accountBalances[accountBalances.length - 1] = 0;
                        newNumOfAccounts++;
                    } else {
                        System.out.println(ANSI_RED + "ERROR: Account already exists! Try again:" +
                                ANSI_RESET);
                    }
                }
            }
        } while(newAccountNumber > 100000 && newAccountNumber < 999999);

        return newNumOfAccounts;
    }

    /**
     * Prompts the user for an account number. If the account number exists, and if its balance is
     * $0.00, then it will get deleted.
     * @param accountNumbers An integer array that contains customer account numbers.
     * @param accountBalances A double array that contains customer account balances.
     * @param numOfAccounts The total number of successful accounts read in.
     * @param scanner Scanner object passed as a parameter to get user input.
     * @return The new total number of accounts in the accountNumbers array.
     */
    private static int deleteAccount(int[] accountNumbers, double[] accountBalances, int numOfAccounts,
                                     Scanner scanner) {
        int accountNumberToDelete, accountIndex;
        int totalNumberOfAccounts = numOfAccounts;
        String answer;

        do {
            System.out.println(ANSI_RED + "Quit? Enter 'Y/y': " + ANSI_RESET);
            answer = scanner.next();

            if (answer.equalsIgnoreCase("y")) {
                break;
            } else {
                System.out.println(ANSI_YELLOW + "Please enter the account number: " + ANSI_RESET);
                accountNumberToDelete = scanner.nextInt();

                accountIndex = findAccount(accountNumbers, numOfAccounts, accountNumberToDelete);
                if (accountIndex == -1) {
                    System.out.println(ANSI_RED + "ERROR: That account does not exist. Try again:" + ANSI_RESET);
                    scanner.nextInt();
                } else if (accountBalances[accountIndex] != 0.00) {
                    System.out.println(ANSI_RED + "ERROR: The account has a balance that is not 0." + ANSI_RESET);
                } else {
                    accountNumbers = removeIntElement(accountNumbers, accountIndex);
                    accountBalances = removeDoubleElement(accountBalances, accountIndex);
                    totalNumberOfAccounts--;
                    System.out.println(ANSI_GREEN + "Account successfully deleted." + ANSI_RESET);
                }
            }
        } while(accountIndex != -1);

        return totalNumberOfAccounts;
    }

    private static int[] removeIntElement(int[] accountNumbers, int accountIndex) {
        // convert array to ArrayList
        List<Integer> list = IntStream.of(accountNumbers).boxed().collect(Collectors.toList());
        // remove element
        list.remove(accountIndex);

        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    private static double[] removeDoubleElement(double[] accountBalances, int accountIndex) {
        List<Double> list = DoubleStream.of(accountBalances).boxed().collect(Collectors.toList());
        list.remove(accountIndex);

        return list.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private static void printCustomerDatabaseTable(int[] accountNumbers, double[] accountBalances,
                                                    int numOfAccounts) {
        System.out.print(ANSI_YELLOW);
        System.out.printf("-----------------------%n");
        System.out.printf("|  CUSTOMER DATABASE  |%n");
        System.out.printf("-----------------------%n");
        System.out.printf("| %-7s  | %-7s  |%n", "ACCOUNT", "BALANCE");
        System.out.printf("-----------------------%n");
        for (int i = 0; i < numOfAccounts; i++) {
            System.out.printf("| %7d  | $%7.2f |", accountNumbers[i], accountBalances[i]);
            System.out.println();
        }
        System.out.printf("-----------------------%n");
        System.out.print(ANSI_RESET);
    }

    /**
     * Prints the ATM's main menu to the console.
     */
    private static void printMainMenu() {
        System.out.println(ANSI_YELLOW + "\tATM MENU");
        System.out.println("\t========" + ANSI_RESET);
        System.out.println(ANSI_GREEN +
                           "[W] - WITHDRAW");
        System.out.println("[D] - DEPOSIT");
        System.out.println("[N] - CREATE NEW ACCOUNT");
        System.out.println("[B] - VIEW BALANCE" + ANSI_RESET);
        System.out.println(ANSI_RED +
                           "[X] - DELETE ACCOUNT");
        System.out.println("[Q] - QUIT SESSION" + ANSI_RESET);
    }
}
