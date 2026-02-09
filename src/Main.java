class BankAccount {
    private int balance = 1000;

    public synchronized void withdraw(String name, int amount) {
        System.out.println(name + " acts to withdraw " + amount);

        if (balance >= amount) {
            try {
                System.out.println(name + " sees enough balance. Processing...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            balance = balance - amount;
            System.out.println(name + " completed withdrawal. Remaining: " + balance);
        } else {
            System.out.println(name + " Transaction failed. Insufficient funds: " + balance);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        BankAccount sharedAccount = new BankAccount();

        Runnable transaction = () ->
            sharedAccount.withdraw(Thread.currentThread().getName(), 700);

        Thread alice = new Thread(transaction, "Alice");
        Thread bob = new Thread(transaction, "Bob");

        alice.start();
        bob.start();
    }
}
