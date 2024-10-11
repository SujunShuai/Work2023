package com.ssj.classes;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

public class Cluster {

    static int k = 0;
    static int dimension;

    static double heapTreeCut;

    double private_heapTreeCut = 0;

    static double heapTreeCutUp = 0.75;
    static double heapTreeCutDown = 0.5;

    public static double stride = 0;
    Semaphore mutex = new Semaphore( 1);
    private int id = 0;


    public void heapTreeCutPlus(){
        if(this.private_heapTreeCut < heapTreeCutUp){
            this.private_heapTreeCut += stride;
        }
    }

    public void heapTreeCutMinus(){
        if(this.private_heapTreeCut > heapTreeCutDown){
            this.private_heapTreeCut -= stride;
        }
    }

    CopyOnWriteArrayList<HeapTreeNode> heapTree = new CopyOnWriteArrayList<>();

    Map<Integer, TreeNodeInfor> hashMap = new HashMap<>();
    ArrayList<KeyWord> centroidKeywords;
    public CopyOnWriteArrayList<Point> pointList = new CopyOnWriteArrayList<>();

    public void calculateCentroid() {
        for (int i = 0; i < dimension; i++) {
            double sum = 0;
            for (Point p : this.pointList) {
                sum += p.getKeywords().get(i).getValue();
            }
            this.centroidKeywords.set(i, new KeyWord(sum / this.pointList.size()));
        }
        this.print();
    }
    public double calculateEuclideanDistance(Point point){
        //this.print();
        double sum = 0.0;
        for (int i = 0; i < dimension; i++) {
            sum += Math.pow(this.centroidKeywords.get(i).getTrueValue(i) - point.getKeywords().get(i).getTrueValue(i), 2);
        }
        return Math.sqrt(sum);
    }

    public Cluster(int id , double startHeapTreeCut) {
        this.id = id;
        this.private_heapTreeCut = startHeapTreeCut;
    }

    public void calculateErrorAverage(){
        double errorAverage = 0;
        if(this.pointList.size() != 0){
            for (Point p :this.pointList){
                errorAverage += this.calculateEuclideanDistance(p);
            }
            errorAverage = errorAverage/this.pointList.size();
        }
        System.out.println("cluster " + this.id + " errorAverage = " + errorAverage);
    }
    public void putPointIn(Point p){
        System.out.println("point " + p.getId() + " get in cluster " +this.id);
        this.pointList.add(p);
        this.calculateCentroid();
    }

    public void cleanPointList(){
        this.pointList = new CopyOnWriteArrayList<>();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void getNewVisitor(Collection<ResidueElement> newElements, int visitId, int step) throws InterruptedException {
        //System.out.println("new visitor : " + visitId);
        this.mutex.acquire();
        for (ResidueElement item : newElements){
            double res = item.getResidue();
            int destination = item.getDestination();
            ResidueElement add = new ResidueElement(visitId,destination,res);
            for (TreeNodeInfor infor : this.hashMap.values()){
                int loc = infor.getLoc();
                if (infor.group.containsKey(visitId)){
                    double old_res = infor.group.get(visitId).getResidue();
                    infor.group.remove(visitId);
                    infor.addRes((-1) * old_res);
                    this.heapTree.get(loc).addRes((-1) * old_res);
                }
            }
        }

        for (ResidueElement item : newElements) {
            double res = item.getResidue();
            int destination = item.getDestination();
            ResidueElement add = new ResidueElement(visitId,destination,res);
            if (this.hashMap.containsKey(destination)) {
                TreeNodeInfor infor = this.hashMap.get(destination);
                int loc = infor.getLoc();
                infor.addGroup(visitId,add);
                //System.out.println(visitId + "," + destination  +  " add " +res);
                infor.addRes(res);
                this.heapTree.get(loc).addRes(res);
                int newLoc = adjustHeapTree(loc);
            } else {
                TreeNodeInfor newTreeNodeInfor = new TreeNodeInfor(this.heapTree.size(), res, item);
                int loc = newTreeNodeInfor.getLoc();
                this.hashMap.put(destination, newTreeNodeInfor);
                HeapTreeNode newTreeNode = new HeapTreeNode(destination, res);
                this.heapTree.add(newTreeNode);
                int newLoc = adjustHeapTree(loc);
            }
        }

        this.halfExecute();
        //this.checkZeroTreeNode();
        this.mutex.release();
    }

    public void checkZeroTreeNode(){
        if(this.heapTree.size()>0){
            double res = this.heapTree.get(this.heapTree.size()-1).getRes();
            while (res <= 0 || Double.isNaN(res)){
                int des = this.heapTree.get(this.heapTree.size()-1).getEndId();
                this.hashMap.remove(des);
                this.heapTree.remove(this.heapTree.size()-1);
                res = this.heapTree.get(this.heapTree.size()-1).getRes();
            }
        }
    }

    public void halfExecute(){
        if(this.heapTree.size()>0){
            int totalSize = this.heapTree.size();
            for (int i = 0; i < (totalSize*this.private_heapTreeCut); i++) {
                int des = this.heapTree.get(this.heapTree.size()-1).getEndId();
                this.hashMap.remove(des);
                this.heapTree.remove(this.heapTree.size()-1);
            }
        }
    }
    public void updateCluster(Node visitor) {
        Collection<ResidueElement> elements = visitor.collectResidue();
        int visitId = visitor.getId();
        for (TreeNodeInfor infor : this.hashMap.values()) {
            if (infor.getGroup().containsKey(visitId)) {
                int loc = infor.getLoc();
                double old_res = infor.getGroup().get(visitId).getResidue();
                infor.getGroup().remove(visitId);
                infor.addRes((-1) * old_res);
                this.heapTree.get(loc).addRes((-1) * old_res);
            }
        }
    }
    public int adjustHeapTree(int loc) {
        //System.out.println("adjustHeapTree from " + loc);
        //不断与上层比
        double currentRes = this.heapTree.get(loc).getRes();
        if (loc != 0) {
            int upLoc = (int) Math.ceil((double) loc / 2) - 1;

            double upRes = this.heapTree.get(upLoc).getRes();
            if (upRes < currentRes) {
                //向上浮动
                //System.out.println("uploc : " + upLoc + " loc : " + loc);
                heapTreeSwap(upLoc, loc);

                return adjustHeapTree(upLoc);
            } else {
                //System.out.println("stop adjust at " + loc);
                return loc;
            }
        } else {
            return 0;
        }

    }

    public void heapTreeSwap(int loc1, int loc2) {
        int key1 = this.heapTree.get(loc1).getEndId();
        this.hashMap.get(key1).setLoc(loc2);
        int key2 = this.heapTree.get(loc2).getEndId();
        this.hashMap.get(key2).setLoc(loc1);

        Collections.swap(this.heapTree,loc1,loc2);
    }


    public void print() {
        System.out.println("cluster: " + this.id);
        System.out.println("points: ");
        for (Point p : this.pointList){
            System.out.print(p.getId()+ ", ");
        }
        System.out.println(" ");
//        System.out.println("keywords: ");
//        for (int i = 0; i < Cluster.dimension; i++) {
//            System.out.print(this.centroidKeywords.get(i).getValue() + " ; ");
//        }
//        System.out.println(" ");
        System.out.println("heap tree :");
        for (HeapTreeNode treeNode : this.heapTree){
            treeNode.print();
        }
//        System.out.println("");
//        this.calculateErrorAverage();
    }


}
