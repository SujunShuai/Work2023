package com.ssj.runable;

import com.ssj.classes.Node;
import com.ssj.classes.ResidueElement;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class InsertRunable implements Callable<Integer> {
    private Node startNode = null;
    private Node endNode = null;

    private CountDownLatch latch;
    private Collection<ResidueElement> Element = null;

    static Object key = new Object();

    private Object mutex = null;
    public InsertRunable(Node startNode, Node endNode) {
        this.startNode = startNode;
        this.endNode = endNode;
//        this.mutex = mutex;
//        this.latch = latch;
    }

    static public int exeNum = 0;

    public static int getExeNum() {
        return exeNum;
    }




    @Override
    public Integer call() throws Exception {
        synchronized (this.startNode.getLock()){
            this.startNode.pass(this.endNode,1,this.Element,0,"add");
            exeNum ++ ;
            //System.out.println("excute " + this.startNode.getId() + "\t" + this.endNode.getId() + " " + exeNum);
            if(exeNum%100000 == 0){
                System.out.println(exeNum);
            }
        }
        return 1;
    }
}
