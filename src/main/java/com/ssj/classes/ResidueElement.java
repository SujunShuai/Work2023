package com.ssj.classes;

public class ResidueElement {
    int nextStep;
    int destination;
    double residue;


    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public double getResidue() {
        return residue;
    }

    public void setResidue(double residue) {
        this.residue = residue;
    }

    public ResidueElement(int nextStep, int destination, double residue) {
        this.nextStep = nextStep;
        this.destination = destination;
        this.residue = residue;
    }
    public void print(){
        System.out.println(this.nextStep + "," + this.destination+ "," + this.residue);
    }

    public int getNextStep() {
        return nextStep;
    }

    public void setNextStep(int nextStep) {
        this.nextStep = nextStep;
    }

    @Override
    public String toString() {
        return "ResidueElement{" +
                "nextStep=" + nextStep +
                ", destination=" + destination +
                ", residue=" + residue +
                '}';
    }
}
