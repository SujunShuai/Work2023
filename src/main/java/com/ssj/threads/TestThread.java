package com.ssj.threads;

import com.ssj.classes.Node;

import java.util.concurrent.CountDownLatch;

public class TestThread extends Thread implements AutoCloseable{

    private Node startNode = null;
    private Node endNode = null;

    private CountDownLatch latch;
    public  TestThread(Node startNode,Node endNode,CountDownLatch latch){
        this.startNode = startNode;
        this.endNode = endNode;
        this.latch = latch;
    }
    Object key = new Object();
    @Override
    public void run(){
        synchronized (key){
           // this.startNode.insertEdge(this.endNode.getStep1(),this.endNode);
            //this.startNode.updateResidueStep1(this.endNode);
            System.out.println("insert " + startNode.getId() + " to " + endNode.getId() + " in " + Thread.currentThread().getName());
            //this.startNode.printCollectResidue();
            latch.countDown();
            //System.out.println(latch.getCount());
        }
    }

    @Override
    public void close() throws Exception {

    }
}
