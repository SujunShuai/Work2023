package com.ssj.classes;

public class SPandIN {
    int startPoint;
    int itemNumber;

    public int getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(int startPoint) {
        this.startPoint = startPoint;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public SPandIN(int startPoint, int itemNumber) {
        this.startPoint = startPoint;
        this.itemNumber = itemNumber;
    }
}
