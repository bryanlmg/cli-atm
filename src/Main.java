import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    // ANSI color escape codes for colorful output to console
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        // maximum number of accounts allowed
        final int MAX_ACCOUNTS = 15;

        // parallel arrays that will hold account data
        int[] accountNumbers = new int[MAX_ACCOUNTS];
        double[] accountBalances = new double[MAX_ACCOUNTS];

        int totalNumOfAccounts = readBankAccounts(accountNumbers, accountBalances);
        System.out.println(ANSI_GREEN + "Successfully read in " + totalNumOfAccounts + " accounts!");
        System.out.println("Now printing the initial customer database..." + ANSI_RESET);
        printCustomerDatabaseTable(accountNumbers, accountBalances, totalNumOfAccounts);

        System.out.println(ANSI_YELLOW + "\nPlease select an option from the main menu below.\n" + ANSI_RESET);
        printMainMenu();

        // get user input and evaluate it
        Scanner scanner = new Scanner(System.in);
        char choice;
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
                    totalNumOfAccounts =
                            createNewAccount(accountNumbers, accountBalances, totalNumOfAccounts, scanner);
                    break;

                case 'B':
                case 'b':
                    getAccountBalance(accountNumbers, accountBalances, totalNumOfAccounts, scanner);
                    break;

                case 'X':
                case 'x':
                    totalNumOfAccounts =
                            deleteActiveAccount(accountNumbers, accountBalances, totalNumOfAccounts, scanner);
                    break;

                case 'Q':
                case 'q':
                    break;

                default:
                    System.out.println(ANSI_RED + "Not a valid option." + ANSI_RESET);
            }

            if (choice != 'q' && choice != 'Q') {
                System.out.println(ANSI_YELLOW + "Please select an option from the main menu below:\n"
                                 + ANSI_RESET);
                printMainMenu();
            }
        } while(choice != 'q' && choice != 'Q');

    }

    /**
     * Prints a colorful main menu to the console.
     */
    private static void printMainMenu() {
        System.out.println(ANSI_YELLOW +
                           "\tMAIN MENU");
        System.out.println("\t========" +
                           ANSI_RESET);
        System.out.println(ANSI_GREEN +
                           "[W] - WITHDRAW");
        System.out.println("[D] - DEPOSIT");
        System.out.println("[N] - CREATE NEW ACCOUNT");
        System.out.println("[B] - VIEW BALANCE"
                         + ANSI_RESET);
        System.out.println(ANSI_RED +
                           "[X] - DELETE ACCOUNT");
        System.out.println("[Q] - QUIT SESSION"
                         + ANSI_RESET);
    }

    /**
     * Existing customer account information is read into a pair of parallel arrays
     * from a .txt file.
     * @param accountNumbers  An integer array containing customer account numbers.
     * @param accountBalances A double array containing customer account balances.
     * @return Total successful number of accounts read in.
     */
    private static int readBankAccounts(int[] accountNumbers, double[] accountBalances) {
        int totalAccountsRead = 0;
        int counter = 0;

        try {
            Scanner scanner = new Scanner(new File("database.txt"));
            while (scanner.hasNextInt() && scanner.hasNextDouble()) {
                accountNumbers[counter] = scanner.nextInt();
                accountBalances[counter] = scanner.nextDouble();
                counter++;
                totalAccountsRead++;
            }
        } catch(FileNotFoundException e) {
            System.out.println("FileNotFoundException thrown.");
            e.printStackTrace();
        }
        return totalAccountsRead;
    }

    /**
     * Searches linearly for an account number in the accountNumbers array.
     * @param accountNumbers An integer array containing customer account numbers.
     * @param numOfAccounts  Total number of successful accounts read in.
     * @return The index of the account in the accountNumbers array if it exists, else -1 if it does not.
     */
    private static int findExistingAccount(int[] accountNumbers, int numOfAccounts, int accountNumber) {
        for (int i = 0; i < numOfAccounts; i++) {
            if (accountNumbers[i] == accountNumber) {
                return i;
            }
        }
        return -1;
    }

    private static int findEmptyIndex(int[] accountNumbers, int numOfAccounts) {
        for (int i = 0; i < numOfAccounts; i++) {
            if (accountNumbers[i] == 0) {
                return -1;
            }
        }
        return 0;
    }

    private static int getValidAccountNumber(Scanner scanner) {
        System.out.println(ANSI_YELLOW + "Enter the 6-digit account number [100000 to 999999]: " + ANSI_RESET);
        int accountNumber = scanner.nextInt();

        while(accountNumber < 100000 || accountNumber > 999999) {
            System.out.println(ANSI_RED + "ERROR: Invalid input. Try again: " + ANSI_RESET);
            accountNumber = scanner.nextInt();
        }
        return accountNumber;
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
        double amountToWithdraw;
        int accountNumber = getValidAccountNumber(scanner);
        int accountIndex = findExistingAccount(accountNumbers, numOfAccounts, accountNumber);

        System.out.println(ANSI_YELLOW + "\nEnter withdrawal amount: " + ANSI_RESET);
        amountToWithdraw = scanner.nextDouble();

        while (amountToWithdraw <= 0.00) {
            System.out.println(ANSI_RED + "ERROR: Withdrawal amount must be $0.01 or greater!" + ANSI_RESET);
            System.out.println(ANSI_YELLOW + "Try again. Enter withdrawal amount: " + ANSI_RESET);
            amountToWithdraw = scanner.nextDouble();
        }

        System.out.println("==============================");
        System.out.println("TRANSACTION TYPE: WITHDRAW");
        System.out.println("ACCOUNT NUMBER: " + accountNumber);
        System.out.printf("CURRENT BALANCE: $%.2f", accountBalances[accountIndex]);
        accountBalances[accountIndex] -= amountToWithdraw;
        System.out.printf("\nAMOUNT TO WITHDRAW: $%.2f", amountToWithdraw);
        System.out.printf("\nNEW BALANCE: $%.2f\n", accountBalances[accountIndex]);
        System.out.println("==============================");
        returnMsg();
    }

    private static void deposit(int[] accountNumbers, double[] accountBalances, int numOfAccounts,
                                Scanner scanner) {
        System.out.println(ANSI_YELLOW + "Enter the account number: " + ANSI_RESET);
        int accountNumber = scanner.nextInt();
        int accountIndex = findExistingAccount(accountNumbers, numOfAccounts, accountNumber);

        System.out.println(ANSI_YELLOW + "Enter deposit amount: " + ANSI_RESET);
        double amountToDeposit = scanner.nextDouble();

        while (amountToDeposit <= 0.00) {
            System.out.println(ANSI_RED + "ERROR: Deposit amount must be $0.01 or greater!" + ANSI_RESET);
            System.out.println(ANSI_YELLOW + "Try again. Enter deposit amount: " + ANSI_RESET);
            amountToDeposit = scanner.nextDouble();
        }

        accountBalances[accountIndex] += amountToDeposit;

        System.out.println("==============================");
        System.out.println("TRANSACTION TYPE: DEPOSIT");
        System.out.println("ACCOUNT NUMBER: " + accountNumber);
        System.out.printf("\nCURRENT BALANCE: $%.2f", accountBalances[accountIndex]);
        System.out.printf("\nAMOUNT TO DEPOSIT: $%.2f", amountToDeposit);
        System.out.printf("\nNEW BALANCE: $%.2f", accountBalances[accountIndex]);
        System.out.println("==============================");
        returnMsg();
    }

    /**
     * Gets the account balance for any given account.
     * @param accountNumbers An integer array containing customer account numbers.
     * @param accountBalances A double array containing customer account balances.
     * @param numOfAccounts Total number of successful accounts read in.
     * @param scanner Scanner object passed as an argument to get user input.
     */
    private static void getAccountBalance(int[] accountNumbers, double[] accountBalances, int numOfAccounts,
                                          Scanner scanner) {

        int accountNumber = getValidAccountNumber(scanner);
        int accountIndex = findExistingAccount(accountNumbers, numOfAccounts, accountNumber);

        System.out.println("\n==============================");
        System.out.println("Transaction Type: VIEW BALANCE");
        System.out.println("Account Number: " + accountNumber);
        System.out.printf("Current Total Balance: $%.2f", accountBalances[accountIndex]);
        System.out.println("\n==============================");
        returnMsg();
    }

    private static int createNewAccount(int[] accountNumbers, double[] accountBalances, int numOfAccounts,
                                        Scanner scanner) {
        int newAccountNumber, accountIndex;
        int newNumOfAccounts = numOfAccounts;
        char answer;

        do {
            newAccountNumber = getValidAccountNumber(scanner);
            accountIndex = findEmptyIndex(accountNumbers,numOfAccounts);

            // account can be created
            if (accountIndex == -1) {
                for (int i = 0; i < accountNumbers.length; i++) {
                    if (accountNumbers[i] == 0) {
                        accountNumbers[i] = newAccountNumber;
                        accountBalances[i] = 0.00;
                        break;
                    }
                }
                newNumOfAccounts++;
                System.out.println(ANSI_GREEN + "Account " + newAccountNumber + " successfully created!" +
                                   ANSI_RESET);
            } else {
                System.out.println(ANSI_RED + "ERROR: Account already exists! Try again:" +
                        ANSI_RESET);
            }

            System.out.println(ANSI_YELLOW + "Create another account? [Y/n]: " + ANSI_RESET);
            answer = scanner.next().charAt(0);

            // checking that the number of accounts does not go over the accountNumbers array length
            if (newNumOfAccounts == accountNumbers.length) {
                System.out.println(ANSI_RED + "ERROR: Account limit reached!");
                System.out.println("To create a new account, delete an existing one.");
                System.out.println(ANSI_RESET);
                break;
            }
        } while(answer != 'n' && answer != 'N');
        returnMsg();

        return newNumOfAccounts;
    }
    
    /**
     * Prompts the user for an account number. The account can be deleted if the account number exists and if
     * its balance is $0.00.
     * @param accountNumbers An integer array that contains customer account numbers.
     * @param accountBalances A double array that contains customer account balances.
     * @param numOfAccounts The total number of successful accounts read in.
     * @param scanner Scanner object passed as a parameter to get user input.
     * @return The new total number of accounts in the accountNumbers array.
     */
    private static int deleteActiveAccount(int[] accountNumbers, double[] accountBalances, int numOfAccounts,
                                     Scanner scanner) {
        int accountNumberToDelete, accountIndex;
        int totalNumberOfAccounts = numOfAccounts;
        char userChoice;

        do {
            accountNumberToDelete = getValidAccountNumber(scanner);
            accountIndex = findExistingAccount(accountNumbers, numOfAccounts, accountNumberToDelete);

            if (accountBalances[accountIndex] != 0.00) {
                System.out.println(ANSI_RED + "ERROR: The account has a non-zero balance." +
                                              " Transaction voided."
                                 + ANSI_RESET);
            } else {
                accountNumbers[accountIndex] = -1;
                accountBalances[accountIndex] = -1.00;
                totalNumberOfAccounts--;
                System.out.println(ANSI_GREEN + "Account " + accountNumberToDelete + " successfully deleted." +
                                 " Transaction details sent to output file."
                                 + ANSI_RESET);
            }

            System.out.println(ANSI_YELLOW + "Delete another account? [Y/N]: " + ANSI_RESET);
            userChoice = scanner.next().charAt(0);
        } while(userChoice != 'N' && userChoice != 'n');

        returnMsg();

        return totalNumberOfAccounts;
    }

    private static void printCustomerDatabaseTable(int[] accountNumbers, double[] accountBalances, int numOfAccounts) {
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

    private static void returnMsg() {
        System.out.println(ANSI_YELLOW + "Returning to main menu..." + ANSI_RESET);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
