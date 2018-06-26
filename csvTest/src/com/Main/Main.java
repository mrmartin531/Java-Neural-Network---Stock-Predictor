package com.Main;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        String tickerName = "BTC-USD";
        Scanner scanner = new Scanner(new File("/Users/Michael/Desktop/Python/Trading/"+tickerName+".csv"));
//        Scanner scanner = new Scanner(new File("/Users/Michael/Desktop/csvTest/testfile.csv"));
        scanner.useDelimiter(",");
        String string;
        int index;
        BufferedWriter writer = new BufferedWriter(new FileWriter(tickerName+"_TEST_VALUES.csv"));

        while(scanner.hasNext()){
            index = 0;
            string = scanner.next();
            for(int i = 1; i < string.length(); i++){
                if(string.substring(i-1,i).equals("\n")){
                    index = i;
                    break;
                }
            }
            if(index > 0){
                string = string.substring(0,index-1) + "," + string.substring(index);
            }

            writer.write(string + ",");

        }
        scanner.close();
        writer.close();

//        String s = "datevolume";
//        s = s.substring(0,4) + "," + s.substring(4);
//        System.out.println(s);
    }
}
