package com.Main;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(new File("/Users/Michael/Desktop/Stock Data/Processed files/AMZN_TEST_VALUES.csv"));
        scanner.useDelimiter(",");
        String[][] values = new String[5267][7];
        for(int i = 0; i < 5267; i++){
            for(int j = 0; j < 7; j++){
                values[i][j] = scanner.next();
            }
        }

        for(int i = 0; i < 5267; i++){
            for(int j = 1; j < 7; j++){
                System.out.print(values[i][j] + " ");
            }
            System.out.println();
        }
        scanner.close();
    }
}
