public class DaemonDemo {
    public static void main(String[] args) {
        Thread bgWorker = new Thread(() -> {
            while (true) {
                System.out.println("Daemon: Auto-saving work...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        bgWorker.setDaemon(true);
        bgWorker.start();

        System.out.println("Main: I am working for 3 seconds...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Main: I am done. Exiting!");
    }
}
