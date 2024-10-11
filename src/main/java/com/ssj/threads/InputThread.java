package com.ssj.threads;

import com.ssj.classes.Graph;
import com.ssj.classes.ResidueElement;

import java.io.*;

public class InputThread implements Runnable{
    public Graph G;
    String fileAddr;

    InputStream f1;
    public InputThread(Graph g,InputStream f1) {
        this.G = g;
        this.f1 = f1;
    }

    @Override
    public void run() {
        int insertNum = 0;
        System.out.println();
        InputStreamReader reader = new InputStreamReader(f1);
        BufferedReader br = new BufferedReader(reader);
        String strTmp = "";
        while (true) {
            insertNum ++;
            try {
                if (!((strTmp = br.readLine()) != null)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String[] values = strTmp.split("\t");
            int startId = Integer.parseInt(values[0]);
            int endId = Integer.parseInt(values[1]);

            //System.out.println("insert " +startId +" to "+ endId + " in " + Thread.currentThread().getName());
            try {
                G.insertEdge(startId, endId);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (insertNum % 10000 == 0){
                System.out.println("current thread : " + Thread.currentThread().getName() + " current insertNum : " + insertNum);
                System.out.println("current insert : " + startId + " to " + endId);
            }
            //System.out.println(startId + "\t" + endId);

        }

//        for (ResidueElement ele : G.getNodeList().get(1).getResidueElementCollection()) {
//            ele.print();
//        }
        /*for (ResidueElement ele : G.getNodeList().get(2).getResidueElementCollection()) {
            ele.print();
        }*/
    }
}
