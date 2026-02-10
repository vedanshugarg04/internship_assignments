package com.example.etldemo.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class CsvGenerator {
    public static void main(String[] args) {
        String filename = "sales_data.csv";
        int recordCount = 500000;
        String[] regions = {"Asia", "Europe", "Africa", "North America"};
        String[] items = {"Cereal", "Fruits", "Clothes", "Meat", "Beverages"};
        Random random = new Random();

        System.out.println("Generating 5 Lakh records...");

        try (FileWriter writer = new FileWriter(filename)) {

            writer.append("orderId,region,itemType,unitsSold,totalRevenue\n");

            for (int i = 1; i <= recordCount; i++) {
                writer.append(i + ",")
                        .append(regions[random.nextInt(regions.length)] + ",")
                        .append(items[random.nextInt(items.length)] + ",")
                        .append(random.nextInt(100) + ",") // Units Sold
                        .append(random.nextDouble() * 1000 + "\n"); // Revenue
            }
            System.out.println("Success! File created: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
