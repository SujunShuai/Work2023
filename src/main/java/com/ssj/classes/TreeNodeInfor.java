package com.ssj.classes;

import java.util.HashMap;
import java.util.Map;

public class TreeNodeInfor {
    int loc;
    double res;
    Map<Integer,ResidueElement> group;


    public void addRes(double addRes){
        this.res += addRes;
    }
    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public double getRes() {
        return res;
    }

    public void setRes(double res) {
        this.res = res;
    }

    public Map<Integer, ResidueElement> getGroup() {
        return group;
    }

    public void setGroup(Map<Integer, ResidueElement> group) {
        this.group = group;
    }

    public TreeNodeInfor(int loc, double res, ResidueElement newElement) {
        this.loc = loc;
        this.res = res;
        this.group = new HashMap<>();
        int nextStep = newElement.nextStep;
        this.group.put(nextStep,newElement);
    }
    public void addGroup(int key,ResidueElement ele){
        this.group.put(key,ele);
    }
}
