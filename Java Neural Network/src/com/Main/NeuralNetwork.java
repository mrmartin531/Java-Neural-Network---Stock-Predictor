package com.Main;

public class NeuralNetwork {
    private int input_nodes;
    private int hidden_nodes;
    private int output_nodes;

    private Matrix weights_ih;
    private Matrix weights_ho;

    private Matrix bias_h;
    private Matrix bias_o;

    private float learning_rate;

    public NeuralNetwork(int input_nodes, int hidden_nodes, int output_nodes) {
        this.input_nodes = input_nodes;
        this.hidden_nodes = hidden_nodes;
        this.output_nodes = output_nodes;

        this.weights_ih = new Matrix(this.hidden_nodes,this.input_nodes);
        this.weights_ho = new Matrix(this.output_nodes,this.hidden_nodes);
        this.weights_ih.randomize();
        this.weights_ho.randomize();

        this.bias_h = new Matrix(this.hidden_nodes,1);
        this.bias_o = new Matrix(this.output_nodes, 1);
        this.bias_h.randomize();
        this.bias_o.randomize();
        this.learning_rate = (float) 3;

    }

    public float sigmoid(float x){
        return (float) (1/(1 + Math.exp(-x)));
    }

    public float dsigmoid(float y){
        return (y * (1 - y));
    }

    public float[] feedforward(float[] input_array){
        // generating hidden outputs
        Matrix inputs = Matrix.fromArray(input_array);
        Matrix hidden = Matrix.multiplyMatrix(this.weights_ih,inputs);
        hidden.addMatrixElements(this.bias_h);

        // activation function
        for(int i = 0; i < hidden.rows; i++){
            for(int j = 0; j < hidden.cols; j++){
                hidden.data[i][j] = sigmoid(hidden.data[i][j]);
            }
        }

        // generating output's output
        Matrix output = Matrix.multiplyMatrix(this.weights_ho,hidden);
        output.addMatrixElements(this.bias_o);

        for(int i = 0; i < output.rows; i++){
            for(int j = 0; j < output.cols; j++){
                output.data[i][j] = sigmoid(output.data[i][j]);
            }
        }

        // MATRIX MATH
        return output.toArray();
    }

    public float[] train(float[] input_array, float[] target_array){
        // generating hidden outputs
        Matrix inputs = Matrix.fromArray(input_array);
        Matrix hidden = Matrix.multiplyMatrix(this.weights_ih,inputs);
        hidden.addMatrixElements(this.bias_h);

        // activation function
        for(int i = 0; i < hidden.rows; i++){
            for(int j = 0; j < hidden.cols; j++){
                hidden.data[i][j] = sigmoid(hidden.data[i][j]);
            }
        }

        // generating output's output
        Matrix outputs = Matrix.multiplyMatrix(this.weights_ho,hidden);
        outputs.addMatrixElements(this.bias_o);

        for(int i = 0; i < outputs.rows; i++){
            for(int j = 0; j < outputs.cols; j++){
                outputs.data[i][j] = sigmoid(outputs.data[i][j]);
            }
        }

        //Convert Array to Matrix objects
        Matrix targets = Matrix.fromArray(target_array);

        //Calculate the error
        // Error = Targets - outputs
        Matrix output_errors = Matrix.subtract(targets, outputs);

        if(Math.abs(output_errors.data[0][0]) > 0.10){
            this.learning_rate += 0.005;
        } else {
            this.learning_rate -= 0.00025;
        }

        //let gradient = outputs * (1 - outputs)
        //Calculate gradient
        Matrix gradients = outputs.copy();
        for(int i = 0; i < outputs.rows; i++){
            for(int j = 0; j < outputs.cols; j++){
                gradients.data[i][j] = dsigmoid(gradients.data[i][j]);
            }
        }
        gradients.multiplyMatrixElements(output_errors);
        gradients.multiplySingle(this.learning_rate);

        //Calculate deltas
        Matrix hidden_T = Matrix.transpose(hidden);
        Matrix weights_ho_deltas = Matrix.multiplyMatrix(gradients,hidden_T);

        //Adjust the weights by deltas
        this.weights_ho.addMatrixElements(weights_ho_deltas);
        //Adjust the bias by its deltas (which is just gradients)
        this.bias_o.addMatrixElements(gradients);

        //Calculate the hidden layer errors
        Matrix who_t = Matrix.transpose(this.weights_ho);
        Matrix hidden_errors = Matrix.multiplyMatrix(who_t,output_errors);
//        hidden_errors.show();
        if(Math.abs(hidden_errors.data[0][0]) > 0.10){
            this.learning_rate += 0.005;
        } else {
            this.learning_rate -= 0.00025;
        }

        //Calculate hidden gradient
        Matrix hidden_gradient = hidden.copy();
        for(int i = 0; i < outputs.rows; i++){
            for(int j = 0; j < outputs.cols; j++){
                hidden_gradient.data[i][j] = dsigmoid(hidden_gradient.data[i][j]);
            }
        }
        hidden_gradient.multiplyMatrixElements(hidden_errors);
        hidden_gradient.multiplySingle(this.learning_rate);

        //Calculate input -> hidden deltas
        Matrix inputs_T = Matrix.transpose(inputs);
        Matrix weights_ih_deltas = Matrix.multiplyMatrix(hidden_gradient,inputs_T);

        //Adjust the weights by deltas
        this.weights_ih.addMatrixElements(weights_ih_deltas);
        //Adjust the bias by its deltas (which is just gradients)
        this.bias_h.addMatrixElements(hidden_gradient);

        //returns guesses while training
        return outputs.toArray();
    }

}
