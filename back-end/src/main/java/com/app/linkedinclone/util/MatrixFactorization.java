package com.app.linkedinclone.util;
import java.util.Random;

public class MatrixFactorization {
    private int numUsers, numPosts, numFactors;
    private double[][] userMatrix;
    private double[][] postMatrix;
    private double[][] ratings; // User-post interaction matrix
    private double learningRate;
    private double regularization;
    private int epochs;

    public MatrixFactorization(double[][] ratings, int numFactors, double learningRate, double regularization, int epochs) {
        this.ratings = ratings;
        this.numUsers = ratings.length;
        this.numPosts = ratings[0].length;
        this.numFactors = numFactors;
        this.learningRate = learningRate;
        this.regularization = regularization;
        this.epochs = epochs;

        // Initialize user and post matrices with random values
        userMatrix = new double[numUsers][numFactors];
        postMatrix = new double[numPosts][numFactors];

        Random random = new Random();
        for (int i = 0; i < numUsers; i++) {
            for (int j = 0; j < numFactors; j++) {
                userMatrix[i][j] = random.nextDouble();
            }
        }
        for (int i = 0; i < numPosts; i++) {
            for (int j = 0; j < numFactors; j++) {
                postMatrix[i][j] = random.nextDouble();
            }
        }
    }

    // Train the model using Stochastic Gradient Descent (SGD)
    public void train() {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < numUsers; i++) {
                for (int j = 0; j < numPosts; j++) {
                    if (ratings[i][j] > 0) { // Only consider non-zero interactions
                        double prediction = predict(i, j);
                        double error = ratings[i][j] - prediction;

                        // Update user and post matrices using SGD
                        for (int k = 0; k < numFactors; k++) {
                            double userFactor = userMatrix[i][k];
                            double postFactor = postMatrix[j][k];

                            userMatrix[i][k] += learningRate * (error * postFactor - regularization * userFactor);
                            postMatrix[j][k] += learningRate * (error * userFactor - regularization * postFactor);
                        }
                    }
                }
            }
            if (epoch % 10 == 0) {
                System.out.println("Epoch " + epoch + ", RMSE: " + calculateRMSE());
            }
        }
    }

    // Predict the interaction between a user and a post
    public double predict(int user, int post) {
        if (user >= numUsers || post >= numPosts) {
            throw new IllegalArgumentException("Invalid user or post index");
        }
        double prediction = 0;
        for (int k = 0; k < numFactors; k++) {
            prediction += userMatrix[user][k] * postMatrix[post][k];
        }
        return prediction;
    }

    // Calculate Root Mean Square Error (RMSE) for evaluation
    public double calculateRMSE() {
        double sumSquaredError = 0;
        int count = 0;
        for (int i = 0; i < numUsers; i++) {
            for (int j = 0; j < numPosts; j++) {
                if (ratings[i][j] > 0) { // Only consider non-zero interactions
                    double prediction = predict(i, j);
                    double error = ratings[i][j] - prediction;
                    sumSquaredError += error * error;
                    count++;
                }
            }
        }
        return Math.sqrt(sumSquaredError / count);
    }


}
