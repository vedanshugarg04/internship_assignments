public class JoinDemo {
    public static void main(String[] args) throws InterruptedException {

        Thread monitor = new Thread(() -> {
            while(true) {
                System.out.println("[Daemon] Monitoring system health...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        monitor.setDaemon(true);
        monitor.start();

        Thread fileDownloader = new Thread(() -> {
            System.out.println("Worker: Started downloading file...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("Worker: Download complete!");
        });

        fileDownloader.start();

        System.out.println("Main: Waiting for Worker to finish...");

        fileDownloader.join();

        System.out.println("Main: Worker is done. I can now process the file.");
        System.out.println("Main: Exiting. Daemon will be killed now.");
    }
}

// List<String> countryNames = List.of("India", "Japan", "China", "Australia");
// List<String> filteredCountryNames = countryNames.stream().filter(country -> country.toLowerCase().matches(".*[aeiou].*")).toList();
// System.out.println(filteredCountryNames);
