package com.ssj.classes;

public class StartPoint {
    int id;
    int startPoint;

    int itemNumber;

    public StartPoint() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public StartPoint(int id, int startPoint, int itemNumber) {
        this.id = id;
        this.startPoint = startPoint;
        this.itemNumber = itemNumber;
    }

    @Override
    public String toString() {
        return "StartPoint{" +
                "id=" + id +
                ", startPoint=" + startPoint +
                ", itemNumber=" + itemNumber +
                '}';
    }
}
