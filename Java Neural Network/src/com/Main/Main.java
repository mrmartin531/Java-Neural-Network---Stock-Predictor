package com.Main;

import org.jfree.data.xy.XYSeries;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

// To-do
// Possible data points to add:
// - RSI
// - Last max, highest so far
// - Last min, lowest so far
// - volatility over last month



public class Main{

    public static void main (String[] args) throws IOException {
        int runs = 10;
        int a_days = 1;
        int data_points = 6;
        int neurons = a_days * data_points * runs * 3;

        String file_path = "/Users/Michael/Desktop/Stock Data/Processed files/";

        // Training files
        String AMZN_file = file_path + "AMZN_TEST_VALUES.csv";
        String FB_file = file_path + "FB_TEST_VALUES.csv";
        String NFLX_file = file_path + "NFLX_TEST_VALUES.csv";
        String TSLA_file = file_path + "TSLA_TEST_VALUES.csv";
        String TWTR_file = file_path + "TWTR_TEST_VALUES.csv";
        String MCK_file = file_path + "MCK_TEST_VALUES.csv";
        String NASDAQ_file = file_path + "^IXIC_TEST_VALUES.csv";
        String SP500_file = file_path + "^GSPC_TEST_VALUES.csv";
        String DOW_file = file_path + "^DJI_TEST_VALUES.csv";
        String NVDA_file = file_path + "NVDA_TEST_VALUES.csv";
        String BTC_file = file_path + "BTC-USD_TEST_VALUES.csv";

        String MODERATEMIX_file = file_path + "AOM_TEST_VALUES.csv";
        String GAMERS_file = file_path + "GAMR_TEST_VALUES.csv";
        String MATCHMARKET_file = file_path + "IVV_TEST_VALUES.csv";
        String BLUECHIPS_file = file_path + "MGC_TEST_VALUES.csv";
        String YOUNGMONEY_file = file_path + "MILN_TEST_VALUES.csv";
        String DELICIOUSDIV_file = file_path + "SCHD_TEST_VALUES.csv";
        String SLOWSTEADY_file = file_path + "SPLV_TEST_VALUES.csv";
        String DOTHERIGHTTHING_file = file_path + "SUSA_TEST_VALUES.csv";

        String AMD_file = file_path + "AMD_TEST_VALUES.csv";

        String test_file = file_path + "AMD_TEST_VALUES.csv";

        String[] file_locations = new String[] {AMZN_file,FB_file,NFLX_file,TSLA_file,TWTR_file,MCK_file,
                NASDAQ_file,DOW_file,BTC_file,MODERATEMIX_file,GAMERS_file,MATCHMARKET_file,NVDA_file,
                BLUECHIPS_file,DELICIOUSDIV_file,SLOWSTEADY_file,DOTHERIGHTTHING_file, YOUNGMONEY_file};

        // Predict High for next day
        NeuralNetwork nnh = new NeuralNetwork(data_points * a_days,neurons,1);

        // Predict Low for next day
        NeuralNetwork nnl = new NeuralNetwork(data_points * a_days,neurons,1);

        System.out.println("Processing Files...");
        for(int run = 0; run < runs; run++) {
            for (int ind = 0; ind < file_locations.length; ind++) {
                int index = findIndex(file_locations[ind]);
                float[][] inputs = setUpInputs(file_locations[ind], index);
                float MAX = findMax(inputs, index);
                float[] TARGETS_H = normalizeTargets(inputs, MAX,1);
                float[] TARGETS_L = normalizeTargets(inputs, MAX,2);

                //normalize inputs eventually
                inputs = normalizeInputs(inputs, MAX);

                //Neural Network Training
                float[] target_array_h = new float[1];
                float[] target_array_l = new float[1];
                float[] input_array = new float[data_points * a_days];
                float[] guesses_h = new float[TARGETS_H.length - 1];
                float[] guesses_l = new float[TARGETS_H.length - 1];
                int day;

                for (int start_day = 0; start_day < index - (1 + a_days); start_day++) {
                    target_array_h[0] = TARGETS_H[start_day + a_days];
                    target_array_l[0] = TARGETS_L[start_day + a_days];
                    day = 0;
                    for (int j = 0; j < data_points * a_days; j++) {
                        input_array[j] = inputs[start_day + day][j % data_points];
                        if (j % data_points == 0 && j > 0) {
                            day++;
                        }
                    }
                    guesses_h = nnh.train(input_array, target_array_h);
                    guesses_l = nnl.train(input_array, target_array_l);
                }
            }
        }
        System.out.println("Testing: " + test_file);
        // Test file
        int TEST_INDEX = findIndex(test_file);
        float[][] TEST_INPUTS = setUpInputs(test_file,TEST_INDEX);
        float TEST_MAX = findMax(TEST_INPUTS,TEST_INDEX);
        float[] TEST_TARGETS_H = normalizeTargets(TEST_INPUTS,TEST_MAX,1);
        float[] TEST_TARGETS_L = normalizeTargets(TEST_INPUTS,TEST_MAX,2);
        TEST_INPUTS = normalizeInputs(TEST_INPUTS,TEST_MAX);
        float[] TEST_INPUT_ARRAY = new float[data_points * a_days];
        float[] TEST_GUESSES_H = new float[TEST_TARGETS_H.length-1];
        float[] TEST_GUESSES_L = new float[TEST_TARGETS_L.length-1];
        float[] target_array_h = new float[1];
        float[] target_array_l = new float[1];
        int day;

        for (int start_day = 0; start_day < TEST_INDEX - (1 + a_days); start_day++) {
            target_array_h[0] = TEST_TARGETS_H[start_day + a_days];
            target_array_l[0] = TEST_TARGETS_L[start_day + a_days];
            day = 0;
            for (int j = 0; j < data_points * a_days; j++) {
                TEST_INPUT_ARRAY[j] = TEST_INPUTS[start_day + day][j % data_points];
                if (j % data_points == 0 && j > 0) {
                    day++;
                }
            }
            TEST_GUESSES_H[start_day] += nnh.feedforward(TEST_INPUT_ARRAY)[0];
            TEST_GUESSES_L[start_day] += nnl.feedforward(TEST_INPUT_ARRAY)[0];
        }

        float error_h = 0;
        float error_l = 0;

        //Calculate average error for high
        for (int i = 0; i < TEST_GUESSES_H.length; i++) {
            error_h += (Math.abs(TEST_TARGETS_H[i] - TEST_GUESSES_H[i])) / TEST_GUESSES_H.length;
        }
        error_h *= TEST_MAX;

        //Calculate average error for low
        for (int i = 0; i < TEST_GUESSES_L.length; i++) {
            error_l += (Math.abs(TEST_TARGETS_L[i] - TEST_GUESSES_L[i])) / TEST_GUESSES_L.length;
        }
        error_l *= TEST_MAX;

        error_h *= 1;
        error_l *= 1;

        System.out.println("Error: High = +/- " + error_h);
        System.out.println("Error: Low = +/- " + error_l);

        // Guess the next day
        float[] input_array = new float[data_points * a_days];
        for(int j = 0; j < data_points * a_days; j++){
            input_array[j] = TEST_INPUTS[TEST_INDEX - (2 + a_days)][j % data_points];
        }

        float guess_h = nnh.feedforward(input_array)[0] * TEST_MAX;
        float guess_l = nnl.feedforward(input_array)[0] * TEST_MAX;
        float prevOpen = TEST_INPUTS[TEST_INDEX - (1 + a_days)][0] * TEST_MAX;

        System.out.println();
        System.out.println("Guess High:       Previous Open:");
        System.out.println(guess_h + "        " + prevOpen );
        System.out.println();
        System.out.println("Guess High: " + (guess_h - error_h) + " - " + (guess_h + error_h) );

        System.out.println();
        System.out.println("Guess Low:       Previous Open:");
        System.out.println(guess_l + "        " + prevOpen );
        System.out.println();
        System.out.println("Guess Low: " + (guess_l - error_l) + " - " + (guess_l + error_l) );

        XYSeries guess_series_h = toSeries(TEST_GUESSES_H,"Guesses: High");
        XYSeries target_series_h = toSeries(TEST_TARGETS_H,"Targets: High");

        XYSeries guess_series_l = toSeries(TEST_GUESSES_L,"Guesses: Low");
        XYSeries target_series_l = toSeries(TEST_TARGETS_L,"Targets: Low");

//        SwingUtilities.invokeLater(() -> {
        LineChartEx high = new LineChartEx("days","price", "Neural Network Test",target_series_h,guess_series_h);
        high.setVisible(true);
        LineChartEx low = new LineChartEx("days","price", "Neural Network Test",target_series_l,guess_series_l);
        low.setVisible(true);
//        });

    }

    public static int findIndex(String filename) throws IOException{
        int counter = 0;
        Scanner scanner = new Scanner(new File(filename));
        scanner.useDelimiter(",");
        while(scanner.hasNext()){
            counter++;
            scanner.next();
        }
        return counter/7;
    }

    public static float[][] setUpInputs(String filename, int index) throws IOException{
        Scanner scanner = new Scanner(new File(filename));
        scanner.useDelimiter(",");
        String[][] stringValues = new String[index][7];
        for(int i = 0; i < index; i++){
            for(int j = 0; j < 7; j++){
                stringValues[i][j] = scanner.next();
            }
        }
        String[][] stringValuesProcessed = new String[index - 1][6];
        for(int i = 1; i < index; i++){
            for(int j = 1; j < 7; j++){
                stringValuesProcessed[i - 1][j - 1] = stringValues[i][j];
            }
        }
        float[][] finalValues = new float[index - 1][6];
        for(int i = 1; i < index; i++){
            for(int j = 1; j < 7; j++){

                finalValues[i - 1][j - 1] = Float.parseFloat(stringValues[i][j]);

            }
        }

        return finalValues;
    }

    public static float findMax(float[][] inputValues, int index){
        float max = 0;
        float value;
        for(int i = 0; i < index - 1; i++){
            value = inputValues[i][1];
            if(value > max){
                max = value;
            }
        }
//        max = max * (float) 1.05;
        return max;
    }

    public static XYSeries toSeries(float[] arr,String key){
        XYSeries series = new XYSeries(key);
        for(int i = 0; i < arr.length; i++){
            series.add(i,arr[i]);
        }
        return series;
    }

    public static float[] normalizeTargets(float[][] inputValues, float max, int index){
        float[] normalizedValues = new float[inputValues.length];
        for(int i = 0; i < inputValues.length; i++){
            normalizedValues[i] = inputValues[i][index] / max;
        }
        return normalizedValues;
    }

    public static float[][] normalizeInputs(float[][] inputValues, float max){
        float[][] normalizedValues = new float[inputValues.length][inputValues[0].length];
        float[][] normalizedVolumes = new float[inputValues.length][2];
        for(int i = 0; i < inputValues.length; i++){
            normalizedVolumes[i][1] = inputValues[i][inputValues[0].length - 1];
        }
        float MAX_VOLUME = findMax(normalizedVolumes,inputValues.length - 1);
        for (int i = 0; i < inputValues.length; i++){
            normalizedValues[i][inputValues[0].length-1] = normalizedVolumes[i][1]/MAX_VOLUME;
        }
        for(int i = 0; i < inputValues.length; i++){
            for(int j = 0; j < inputValues[0].length - 1; j++) {
                normalizedValues[i][j] = inputValues[i][j] / max;
            }
        }
        return normalizedValues;
    }

    public static void show(float[][] inputValues){
        System.out.println();
        for(int i = 0; i < inputValues.length; i++){
            for(int j = 0; j < inputValues[i].length; j++){
                System.out.print(inputValues[i][j] + " ");
            }
            System.out.println();
        }
    }
}
