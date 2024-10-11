package com.ssj.classes;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class Node {
    int id;
    CopyOnWriteArrayList<Node> outList;
    public Object outListLock = new Object();
    CopyOnWriteArrayList<Node> inList;
    public Object inListLock = new Object();
    CopyOnWriteArrayList<Point> pointList;
    public Object pointListLock = new Object();
    CopyOnWriteArrayList<Cluster> clusterList;
    CopyOnWriteArrayList<ResidueElement> residueElementCollection = new CopyOnWriteArrayList<>();
    public Object residueElementCollectionLock = new Object();
    private HashMap<Integer, Integer> inListMap = new HashMap<Integer, Integer>();

    private HashMap<Integer, Integer> outListMap = new HashMap<Integer, Integer>();
    Map<Integer, ResidueElement> residueMap;
    Map<Integer, Double> piMap;

    static int totalInsert = 0;
    static int totalExecute = 0;
    public double maxStep1 = 0;


    public final Object stepLock = new Object();
    public double privateR_max = 0 ;// init privateR_max = 0.01;
    public static double r_maxUp = 0.02;
    public static double r_maxDown = 0.001;
    public static double stride = 0.0001;

    public void overTimeLimit(){
        if (this.privateR_max < r_maxUp){
            this.privateR_max += stride;
        }
    }



    public void lowerTimeLimit(){
        if(this.privateR_max > r_maxDown){
            this.privateR_max -= stride;
        }
    }

//    public double socialWorkLoad = 0;
//    public double pointWorkLoad = 0;

    static final Object lock = new Object();
    double p_min;
    double p_invert;
    double step1;
    double step2;
    double old_step2;
    static Object minusLock = new Object();
    CopyOnWriteArrayList<StartPoint> startPoints = new CopyOnWriteArrayList<StartPoint>();
    Collection<ResidueElement> testResult = new ArrayList<ResidueElement>();
    double alpha = 0.5;

//    static double r_max = 0.05;


    Semaphore mutex = new Semaphore(1);

//    public void insertEdge(double endStep1, Node endNode) {
//        this.old_step2 = this.step2;
//        this.outList.add(endNode);
//        endNode.insertInNode(this);
//        this.step1++;
//        this.step2 = this.step2 + endStep1 + 1;
//        for (Node n : this.inList) {
//            n.step2Plus();
//        }
//    }

    public void r_maxPlus()
    {
          this.privateR_max += stride;
    }
    public void  r_maxMinus(){
            this.privateR_max -= stride;
    }



    public void insertInNode(Node startNode) {
        this.inList.add(startNode);
        this.inListMap.put(startNode.id,1);
    }

    public void deleteInNode(Node startNode) {
        this.inList.remove(startNode);
        this.inListMap.remove(startNode.id,1);
    }

    public void step2Plus() {
        this.old_step2 = this.step2;
        this.step2++;
    }

    public Collection<ResidueElement> collectResidue() {
        Map<Integer, Double> map = this.residueElementCollection.stream().collect(Collectors.groupingBy(ResidueElement::getDestination, Collectors.summingDouble(ResidueElement::getResidue)));
        Collection<ResidueElement> updateResidue = new ArrayList<ResidueElement>();
        for (int key : map.keySet()) {
            if (map.get(key) >= this.privateR_max) {
                updateResidue.add(new ResidueElement(this.id, key, map.get(key)));
            }
        }
        return updateResidue;
    }


    public Object getLock() {
        return this.lock;
    }

    public Node(int id,double startR_max) {
        this.id = id;
        this.outList = new CopyOnWriteArrayList<>();
        this.inList = new CopyOnWriteArrayList<>();
        this.pointList = new CopyOnWriteArrayList<>();
        this.clusterList = new CopyOnWriteArrayList<>();
        this.p_min = 1;
        this.p_invert = 0;
        this.step1 = 0;
        this.step2 = 0;
        this.residueMap = new HashMap<>();
        this.piMap = new HashMap<>();
        this.privateR_max = startR_max;
    }

    public void pass(Node endNode, int stage, Collection<ResidueElement> backElement, int updateNumber, String type) throws InterruptedException {

        this.mutex.acquire();
        if (stage == 1) {
            int nextId;
            double endStep1;
            // System.out.println("stage 1 : " + nextId + " pass to " + this.getId());
            synchronized (lock) {
                totalExecute++;
            }
            if (type.equals("add")) {
                synchronized (lock) {
                    totalInsert++;
                }
                synchronized (this.stepLock){
                    nextId = endNode.getId();
                    endStep1 = endNode.step1;
//                    System.out.println("step1 : " + nextId + " to " +this.id);
                    this.old_step2 = this.step2;
                    this.outList.add(endNode);
                    this.outListMap.put(endNode.id,1);
//                this.r_maxUp = 0.5 / this.outList.size();
                    this.step1++;
                    this.step2 += (endStep1 + 1) ;
                }


                endNode.insertInNode(this);

//                if(endStep1 > this.maxStep1){
//                    this.maxStep1 = endStep1;
//                    this.r_maxUp = 0.5 * this.maxStep1 / this.step2 ;
//                }
               // synchronized (this.residueElementCollectionLock) {
                    if (this.step2 > 0) {
                        for (ResidueElement item : this.residueElementCollection) {
                            item.setResidue(item.getResidue() * this.old_step2 / this.step2);
//                            if(item.getResidue() < r_max){
//                                this.residueElementCollection.remove(item);
//                            }
                        }
                    }
                //}

                Collection<ResidueElement> newCollection = endNode.collectResidue();
                double basicResidue = this.alpha * (endNode.step1 + 1) / this.step2;
                //StartPoint newPoint = new StartPoint(nextId, this.residueElementCollection.size(), itemNumber);
               // synchronized (this.residueElementCollectionLock) {
                    for (ResidueElement item : newCollection) {
                        ResidueElement newElement = new ResidueElement(item.getNextStep(), item.getDestination(), item.getResidue() * basicResidue);
                        if (item.getDestination() != this.id && newElement.getResidue() > this.privateR_max) {
                            this.residueElementCollection.add(newElement);
                        }
                    }
                    this.residueElementCollection.add(new ResidueElement(nextId, nextId, basicResidue));
                //}
                Collection<ResidueElement> changedElement = this.collectResidue();
                changedElement.add(new ResidueElement(this.id, this.id, 1));
                for (Cluster c : this.clusterList) {
                    c.getNewVisitor(changedElement, this.id,1);
                }
                this.mutex.release();
                for (Node backNode : this.inList) {
                    backNode.pass(this, 2, changedElement, 0, type);
                }

                //System.out.println("end add " + this.id + " " + nextId);
            } else if (type.equals("minus")) {

                synchronized (this.stepLock){
                    nextId = endNode.getId();
                    endStep1 = endNode.step1;
                    this.old_step2 = this.step2;
                    this.outList.remove(endNode);
                    this.step1--;
                    this.step2 -= (endStep1 + 1);
                }

                this.outListMap.remove(endNode.id);
                //this.r_maxUp = 0.5 / this.outList.size();
                endNode.deleteInNode(this);

                //synchronized (this.residueElementCollectionLock) {
                    for (ResidueElement item : this.residueElementCollection) {
                        if (item.getNextStep() == nextId) {
                            //System.out.println("remove " + item.getNextStep() + " " + item.getDestination());
                            this.residueElementCollection.remove(item);
                        } else {
                            if (this.step2 > 0) {
                                item.setResidue(item.getResidue() * this.old_step2 / this.step2);
                                if(item.getResidue() < this.privateR_max){
                                    this.residueElementCollection.remove(item);
                                }
                            }
                        }
                    }
                //}
                Collection<ResidueElement> changedElement = this.collectResidue();
                changedElement.add(new ResidueElement(this.id, this.id, 1));

                this.mutex.release();
                for (Cluster c : this.clusterList) {
                    c.getNewVisitor(changedElement, this.id,1);
                }
                for (Node backNode : this.inList) {
                    backNode.pass(this, 2, changedElement, 0, type);
                }
            }
        } //end if stage == 1
        else if (stage == 2) {
            int nextId;
            double endStep1 ;

            // System.out.println("stage 2 : " + nextId + " pass to " + this.getId());
            synchronized (this.stepLock){
                nextId = endNode.getId();
                endStep1 = endNode.step1;
//                System.out.println("step2 : " + nextId + " to " +this.id);
                if (type.equals("add")) {
                    this.step2++;
                } else if (type.equals("minus")) {
                    //System.out.println("step2 minus");
                    this.step2--;
                }
            }

            //synchronized (this.residueElementCollectionLock) {
                for (ResidueElement item : this.residueElementCollection) {
                    if (item.getNextStep() == nextId) {
                        this.residueElementCollection.remove(item);
                    } else {
                        if (this.step2 > 0) {
                            item.setResidue(item.getResidue() * (this.step2 - 1) / this.step2);
                            if(item.getResidue() < this.privateR_max){
                                this.residueElementCollection.remove(item);
                            }
                        }
                    }
                }
            //}
            //synchronized (this.residueElementCollectionLock) {
                if (this.step2 > 0 && this.checkHasEndNode(nextId)) {
//                    Collection<ResidueElement> updateElements = endNode.collectResidue();
//                    updateElements.add(new ResidueElement(nextId, nextId, 1));
                    for (ResidueElement item : backElement) {
                        //System.out.println(this.id + " "+"add:" + item.getNextStep() + "," + item.getDestination()+","+ item.getResidue());
                        ResidueElement newElement = new ResidueElement(item.getNextStep(), item.getDestination(), item.getResidue() * (endStep1 + 1) / this.step2 * this.alpha);
                        if (item.getDestination() != this.id && newElement.getResidue() > this.privateR_max) {
                            this.residueElementCollection.add(newElement);
                        }
                    }
                }
            //}

            Collection<ResidueElement> changedElement = this.collectResidue();
            changedElement.add(new ResidueElement(this.id, this.id, 1));

            this.mutex.release();

            for (Cluster c : this.clusterList) {
                c.getNewVisitor(changedElement, this.id,2);
            }

            for (Node n : this.inList) {
                n.pass(this, stage+1, changedElement, changedElement.size()-1, type);
            }
        }//end if stage == 2
        else {
            int nextId;
            double endStep1;
            synchronized (this.stepLock){
                nextId = endNode.getId();
                endStep1 = endNode.step1;
//                System.out.println("step3 : " + nextId + " to " +this.id);
            }

            //System.out.println("stage 3 : " + nextId + " pass to " + this.getId() + "type :" + type);
            int upN = updateNumber;

           // synchronized (this.residueElementCollectionLock) {
                for (ResidueElement item : this.residueElementCollection) {
                    if (item.getNextStep() == nextId) {
                        this.residueElementCollection.remove(item);
                    }
                }
            //}
            if (this.step2 > 0 && this.checkHasEndNode(nextId)) {
//                Collection<ResidueElement> updateElements = endNode.collectResidue();
//                updateElements.add(new ResidueElement(nextId, nextId, 1));
                for (ResidueElement item : backElement) {
                    ResidueElement newElement = new ResidueElement(item.getNextStep(), item.getDestination(), item.getResidue() * (endStep1 + 1) / this.step2 * this.alpha);
                    if (item.getDestination() != this.id && newElement.getResidue() > this.privateR_max) {
                        this.residueElementCollection.add(newElement);
                    } else {
                        upN--;
                    }
                }
            }
            Collection<ResidueElement> Element = this.collectResidue();
            Element.add(new ResidueElement(this.id, this.id, 1));


            this.mutex.release();
            if (upN > 1 && stage < 6 ) {
                for (Cluster c : this.clusterList) {
                    c.getNewVisitor(Element, this.id,2);
                }
                for (Node backNode : this.inList) {
                     backNode.pass(this, stage+1, Element, upN - 1, type);
                }
            }
        }//end if stage = 3
    }

    public Boolean checkHasEndNode(int endId) {
        if (this.outListMap.containsKey(endId)){
            return true;
        }else
            return false;

    }

    public Boolean checkHasInNode(int inId) {
        for (Node node : this.inList) {
            if (node.getId() == inId)
                return true;
        }
        return false;
    }

    public void visitPoint(Point point) throws InterruptedException {
        this.pointList.add(point);
        int currentClusterId = point.getBelongTo().getId();
        boolean haveCluster = false;
        for (Cluster c : this.clusterList) {
            if (c.getId() == currentClusterId) {
                haveCluster = true;
                break;
            }
        }
        if (!haveCluster) {
            // System.out.println("add new visit : "+ this.id + " visit " + currentClusterId);
            this.clusterList.add(point.getBelongTo());
            Cluster cluster = point.belongTo;
            Collection<ResidueElement> newElements = this.collectResidue();
            int visitId = this.id;
            cluster.getNewVisitor(newElements, visitId,0);
        }
    }

    public void passToCluster() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Node> getOutList() {
        return outList;
    }

    public void setOutList(CopyOnWriteArrayList<Node> outList) {
        this.outList = outList;
    }

    public List<Node> getInList() {
        return inList;
    }

    public void setInList(CopyOnWriteArrayList<Node> inList) {
        this.inList = inList;
    }


    public double getP_min() {
        return p_min;
    }

    public void setP_min(double p_min) {
        this.p_min = p_min;
    }

    public double getP_invert() {
        return p_invert;
    }

    public void setP_invert(double p_invert) {
        this.p_invert = p_invert;
    }

    public double getStep1() {
        return step1;
    }

    public void setStep1(double step1) {
        this.step1 = step1;
    }

    public double getStep2() {
        return step2;
    }

    public void setStep2(double step2) {
        this.step2 = step2;
    }

    public Map<Integer, ResidueElement> getResidueMap() {
        return residueMap;
    }

    public void setResidueMap(Map<Integer, ResidueElement> residueMap) {
        this.residueMap = residueMap;
    }

    public Map<Integer, Double> getPiMap() {
        return piMap;
    }

    public void setPiMap(Map<Integer, Double> piMap) {
        this.piMap = piMap;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getOld_step2() {
        return old_step2;
    }

    public void setOld_step2(double old_step2) {
        this.old_step2 = old_step2;
    }

    public CopyOnWriteArrayList<ResidueElement> getResidueElementCollection() {
        return residueElementCollection;
    }

    public void setResidueElementCollection(CopyOnWriteArrayList<ResidueElement> residueElementCollection) {
        this.residueElementCollection = residueElementCollection;
    }

    public CopyOnWriteArrayList<StartPoint> getStartPoints() {
        return startPoints;
    }

    public void setStartPoints(CopyOnWriteArrayList<StartPoint> startPoints) {
        this.startPoints = startPoints;
    }

    public Collection<ResidueElement> getTestResult() {
        return testResult;
    }

    public void setTestResult(Collection<ResidueElement> testResult) {
        this.testResult = testResult;
    }

    public double getPrivateR_max() {
        return privateR_max;
    }

    public void setPrivateR_max(double privateR_max) {
        this.privateR_max = privateR_max;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(CopyOnWriteArrayList<Point> pointList) {
        this.pointList = pointList;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", outList=" + outList +
                ", inList=" + inList +
                ", p_min=" + p_min +
                ", p_invert=" + p_invert +
                ", step1=" + step1 +
                ", step2=" + step2 +
                '}';
    }

    public void printNode() {
        System.out.println("node id :" + this.id);
        for (ResidueElement item : this.residueElementCollection) {
            item.print();
        }
    }

    public void printInList() {
        System.out.println("node " + this.id);
        System.out.println("in-list:");
        for (Node node : this.inList) {
            System.out.println(node.id + " ");
        }
    }

    public void printOutList() {
        System.out.println("out-list:");
        for (Node node : this.outList) {
            System.out.println(node.id + " ");
        }
    }

    public void printStartPoint() {
        System.out.println("start Point:");
        for (StartPoint sp : this.startPoints) {
            System.out.println(sp.toString());
        }
    }
}
