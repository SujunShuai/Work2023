package com.ssj.classes;

import javax.swing.tree.TreeNode;
import java.util.*;

public class Point {
    int id;

    ArrayList<KeyWord> keywords = new ArrayList<>();

    Cluster belongTo ;
    List<Node> visitNodeList = new ArrayList<>();

    ArrayList<HeapTreeNode> heapTree = new ArrayList<>();
    Map<Integer, TreeNodeInfor> hashMap = new HashMap<>();



    public void getNewVisitor(Collection<ResidueElement> newElements,int visitId) {
        System.out.println("new visitor : " + visitId);
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

//            if(this.hashMap.containsKey(destination)){
//                TreeNodeInfor infor = this.hashMap.get(destination);
//                int loc = infor.getLoc();
//                if (infor.group.containsKey(visitId)){
//                    double old_res = infor.group.get(visitId).getResidue();
//                    infor.group.remove(visitId);
//                    infor.addRes((-1) * old_res);
//                    this.heapTree.get(loc).addRes((-1) * old_res);
//                }
//            }
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
    }

    public void updateP(Node visitor) {
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

    public Cluster getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(Cluster belongTo) {
        this.belongTo = belongTo;
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
        HeapTreeNode loc1Node = this.heapTree.get(loc1);
        int key1 = loc1Node.getEndId();
        this.hashMap.get(key1).setLoc(loc2);
        HeapTreeNode loc2Node = this.heapTree.get(loc2);
        int key2 = loc2Node.getEndId();
        this.hashMap.get(key2).setLoc(loc1);

        this.heapTree.set(loc1, loc2Node);
        this.heapTree.set(loc2, loc1Node);
    }

    public void resultCheck(){

    }

    public Point(int id, ArrayList<KeyWord> keywords) {
        this.id = id;
        this.keywords = keywords;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Node> getVisitNodeList() {
        return visitNodeList;
    }

    public void setVisitNodeList(List<Node> visitNodeList) {
        this.visitNodeList = visitNodeList;
    }

    public ArrayList<HeapTreeNode> getHeapTree() {
        return heapTree;
    }

    public void setHeapTree(ArrayList<HeapTreeNode> heapTree) {
        this.heapTree = heapTree;
    }

    public Map<Integer, TreeNodeInfor> getHashMap() {
        return hashMap;
    }

    public void setHashMap(Map<Integer, TreeNodeInfor> hashMap) {
        this.hashMap = hashMap;
    }

    public ArrayList<KeyWord> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<KeyWord> keywords) {
        this.keywords = keywords;
    }

    public void print() {
        System.out.println("point: " + this.id);
//        System.out.println("keywords: ");
//        for (int i = 0; i < Cluster.dimension; i++) {
//            System.out.print(this.keywords.get(i).getValue() + " ; ");
//
//        }
        System.out.println("belong To : " + this.belongTo.getId());
        System.out.println("");
    }
}
