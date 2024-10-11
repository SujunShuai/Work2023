package com.ssj.classes;

public class Job {
    String type;
    int startId;
    int endId;

    Job nextJob = null;

    public Job(String type, int startId, int endId) {
        this.type = type;
        this.startId = startId;
        this.endId = endId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStartId() {
        return startId;
    }

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public int getEndId() {
        return endId;
    }

    public void setEndId(int endId) {
        this.endId = endId;
    }

    public Job getNextJob() {
        return nextJob;
    }

    public void setNextJob(Job nextJob) {
        this.nextJob = nextJob;
    }


    @Override
    public String toString() {
        return "Job{" +
                "type='" + type + '\'' +
                ", startId=" + startId +
                ", endId=" + endId +
                '}';
    }
}
