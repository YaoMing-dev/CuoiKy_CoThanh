/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.nhom08;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author miyam
 */
public class Login {
    
    public static void main(String[] args) {
        System.out.println("Hello World!");
        System.out.println("De chay test Selenium, hay chay file testng.xml");
        System.out.println("Ket qua se duoc xuat ra file: test-results.csv");
    }
    

    public static class CSVReporter {
        private static final String CSV_FILE = "test-results.csv";
        private static List<String[]> results = new ArrayList<>();
        
        public static void addResult(String testName, String status, long timeMs) {
            String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
            results.add(new String[]{testName, status, timestamp, String.valueOf(timeMs)});
            System.out.println("  -> Da luu ket qua: " + testName);
        }
        
        public static void writeToCSV() {
            try {
                FileWriter out = new FileWriter(CSV_FILE, false); // false = ghi de file cu
                CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                        .withHeader("Test Case", "Status", "Time", "Duration (ms)"));
                
                for (String[] result : results) {
                    printer.printRecord((Object[]) result);
                }
                
                printer.flush();
                printer.close();
                out.close();
                
                System.out.println("\n>>> DA XUAT KET QUA RA FILE: " + CSV_FILE + " <<<");
                System.out.println("[OK] Vi tri file: " + new java.io.File(CSV_FILE).getAbsolutePath());
                
                long pass = results.stream().filter(r -> "PASS".equals(r[1])).count();
                long fail = results.size() - pass;
                System.out.println("[OK] Tong so test: " + results.size());
                System.out.println("[OK] PASS: " + pass + " | FAIL: " + fail);
                
            } catch (IOException e) {
                System.err.println("[ERROR] LOI KHI GHI FILE CSV: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        public static void clear() {
            results.clear();
            System.out.println("Da xoa ket qua cu, san sang cho test moi");
        }
    }
}