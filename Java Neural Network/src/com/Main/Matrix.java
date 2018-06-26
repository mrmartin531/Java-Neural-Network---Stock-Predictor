package com.Main;

import java.util.Random;

public class Matrix {
    public int rows;
    public int cols;
    public float[][] data;

    // Constructor functions
    public Matrix() {
        this(1,1);
    }

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        // fill matrix with zeros
        this.data = new float[rows][cols];
    }

    // sends matrix to an array
    public float[] toArray(){
        int index = 0;
        float[] arr = new float[this.rows*this.cols];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                arr[index] = this.data[i][j];
                index++;
            }
        }
        return arr;
    }

    // sends array to a matrix
    public static Matrix fromArray(float[] arr){
        Matrix m = new Matrix(arr.length,1);
        for(int i = 0; i < arr.length; i++){
            m.data[i][0] = arr[i];
        }
        return m;
    }

    // makes a copy of a matrix
    public Matrix copy(){
        Matrix m = new Matrix(this.rows,this.cols);
        for(int i = 0; i < this.rows; i++){
            for(int j = 0; j < this.cols; j++){
                m.data[i][j] = this.data[i][j];
            }
        }
        return m;
    }

    // transposes a given matrix
    public static Matrix transpose(Matrix matrix){
        Matrix n = new Matrix(matrix.cols,matrix.rows);
        for(int i = 0; i < matrix.rows; i++){
            for(int j = 0; j < matrix.cols; j++){
                n.data[j][i] = matrix.data[i][j];
            }
        }
        return n;
    }

    // randomize matrix values between -1 and 1
    public void randomize() {
        Random rand = new Random();
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                //this.data[i][j] = rand.nextFloat() * 2 - 1;
                this.data[i][j] = (float) 0.50;
            }
        }
    }

    // scalar add function for a single value
    public void addSingle(float v){
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.data[i][j] += v;
            }
        }
    }

    // scalar add function, element-wise for matrix values
    public void addMatrixElements(Matrix v){
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.data[i][j] += v.data[i][j];
            }
        }
    }

    // scalar multiply function for a single value
    public void multiplySingle(float v) {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.data[i][j] *= v;
            }
        }
    }

    // matrix multiplication
    public static Matrix multiplyMatrix(Matrix a, Matrix b) {
        if(a.cols != b.rows){
            System.out.println("Columns of A must Match rows of B");
            return null;
        }
        Matrix m = new Matrix(a.rows,b.cols);
        for(int i = 0; i < m.rows; i++){
            for(int j = 0; j < m.cols; j++){
                float sum = 0;
                for(int k = 0; k < a.cols; k++){
                    sum += a.data[i][k] * b.data[k][j];
                }
                m.data[i][j] = sum;
            }
        }
        return m;
    }

    // scalar add function, element-wise for matrix values
    public void multiplyMatrixElements(Matrix v){
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.data[i][j] *= v.data[i][j];
            }
        }
    }

    // returns a new matrix a-b
    public static Matrix subtract(Matrix a, Matrix b){
        Matrix m = new Matrix(a.rows,a.cols);
        for (int i = 0; i < m.rows; i++) {
            for (int j = 0; j < m.cols; j++) {
                m.data[i][j] = a.data[i][j] - b.data[i][j];
            }
        }
        return m;
    }

    // print matrix to output
    public void show() {
        System.out.println();
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                System.out.print(" " + data[i][j]);
            }
            System.out.println();
        }
    }
}
