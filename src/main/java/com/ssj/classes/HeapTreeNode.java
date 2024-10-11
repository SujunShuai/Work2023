package com.ssj.classes;

public class HeapTreeNode {
    int endId;
    double res;

    public void addRes(double res){
        this.res += res;
    }
    public HeapTreeNode(int endId, double res) {
        this.endId = endId;
        this.res = res;
    }

    public int getEndId() {
        return endId;
    }

    public void setEndId(int endId) {
        this.endId = endId;
    }

    public double getRes() {
        return res;
    }

    public void setRes(double res) {
        this.res = res;
    }
    public void print(){
        System.out.println(this.endId + " " + this.res);
    }
}
