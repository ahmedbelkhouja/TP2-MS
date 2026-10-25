package org.example.restwebapp;

public class Calculatrice {
    public int somme(int a, int b) {
        return a + b;
    }
    public int soustraction(int a, int b) {
        return a - b;
    }
    public int multiply(int a, int b) {
        return a * b;
    }
    public double divide(double a, double b) {
        if(b==0){
            throw new ArithmeticException("Division by zero");
        }
        return (double) a / b;
    }
}
