package com.ssj.classes;

import java.util.ArrayList;

public class KeyWord {
    static ArrayList<Double> maxValue = new ArrayList<>();
    static ArrayList<Double> minValue = new ArrayList<>();
    static ArrayList<Double> power = new ArrayList<>();
    static ArrayList<String> lables = new ArrayList<>();
    double value ;


    public void setValue(double value){
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public double getTrueValue(int dimension){
        double gap = maxValue.get(dimension) - minValue.get(dimension);
        return this.value/gap * power.get(dimension);
    }

    public static void printInfor(){
        System.out.println("KeyWord Information : ");
        System.out.println(lables);
        System.out.println(maxValue);
        System.out.println(minValue);
        System.out.println(power);
    }

    public KeyWord(double value) {
        this.value = value;
    }


    public void print(){
        System.out.println(this.value);
    }
}
