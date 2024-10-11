package com.ssj;

import com.ssj.classes.*;
import com.ssj.threads.InputThread;
import com.ssj.threads.TestThread;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static public ArrayList<Graph> GraphList = new ArrayList<>();
    static public ArrayList<String> genereateNameList = new ArrayList<>();
    static public final Object generateNameListLock = new Object();


    static int partNum = 0;

    public static void main(String[] args) throws IOException, InterruptedException {
        readSetting();

        Graph.threadCount = partNum;
        Graph G = new Graph();
        G.setName("init");

        G.start();
        G.join();

        for (int i = 0; i < partNum; i++) {
            Graph GE = new Graph();
            GE.setName(Integer.toString((i + 1)));
            synchronized (generateNameListLock){
                genereateNameList.add(Integer.toString((i + 1)));
                GraphList.add(GE);
            }
            GE.start();
        }
        Graph GP = new Graph();
        GP.setName("print");
        GP.start();
        System.out.println("main finish");
    }
    public static void readSetting(){
        System.out.println("Reading Setting : ");
        BufferedReader reader = null;
        String filePath = "examSetting.txt";
        String line = "";
        int totalVisit = 0;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.startsWith("NodeNumber")){
                    Graph.NodeNumber = Integer.parseInt(line.split("\t")[1]);
                } else if (line.startsWith("ClusterNumber")) {
                    Graph.ClusterNumber = Integer.parseInt(line.split("\t")[1]);
                }else if (line.startsWith("ThreadNumber")) {
                    Graph.ThreadNumber = Integer.parseInt(line.split("\t")[1]);
                    partNum = Integer.parseInt(line.split("\t")[1]);
                }else if (line.startsWith("PerUserVisits")) {
                    Graph.PerUserVisit = Integer.parseInt(line.split("\t")[1]);
                }else if (line.startsWith("SteadyPerUserConnection")) {
                    Graph.SteadyPerUserConnection = Integer.parseInt(line.split("\t")[1]);
                }else if (line.startsWith("StartR_max")) {
                    Graph.StartR_max = Double.parseDouble(line.split("\t")[1]);
                }else if (line.startsWith("StartHeapTreeCut")) {
                    Graph.StartHeapTreeCut = Double.parseDouble(line.split("\t")[1]);
                }else if (line.startsWith("DynamicR_maxStride")) {
                    Node.stride = Double.parseDouble(line.split(" ")[1]);
                }else if (line.startsWith("DynamicHeapTreeCutStride")) {
                    Cluster.stride = Double.parseDouble(line.split("\t")[1]);
                }
                else if (line.startsWith("SteadyDoor")) {
                    Graph.SteadyDoor = Integer.parseInt(line.split("\t")[1]);
                }else if (line.startsWith("IsInitR_maxAndHeapTreeCut")) {
                    if(line.split("\t")[1].equals("true")){
                        Graph.IsInitR_maxAndHeapTreeCut = true;
                    }else {
                        Graph.IsInitR_maxAndHeapTreeCut = false;
                    }
                }else if (line.startsWith("IsBalanced")) {
                    if(line.split("\t")[1].equals("true")){
                        Graph.isBalanced = true;
                    }else {
                        Graph.isBalanced = false;
                    }
                }else if (line.startsWith("IsDynamicChange")) {
                    if(line.split("\t")[1].equals("true")){
                        Graph.IsDynamicChange = true;
                    }else {
                        Graph.IsDynamicChange = false;
                    }
                }else if (line.startsWith("InitJobListPath")) {
                    Graph.InitJobListPath = line.split("\t")[1];
                }else if (line.startsWith("InitVisitPath")) {
                    Graph.InitVisitPath = line.split("\t")[1];
                }else if (line.startsWith("InitClusterPath")) {
                    Graph.InitClusterPath = line.split("\t")[1];
                }else if (line.startsWith("InitR_maxPath")) {
                    Graph.InitR_maxPath = line.split("\t")[1];
                }else if (line.startsWith("InitHeapTreeCutPath")) {
                    Graph.InitR_maxPath = line.split("\t")[1];
                }else if (line.startsWith("R_maxResultPath")) {
                    Graph.R_maxResultPath = line.split("\t")[1];
                }else if (line.startsWith("HeapTreeCutResultPath")) {
                    Graph.HeapTreeCutResultPath = line.split("\t")[1];
                }else if (line.startsWith("NodeResultPath")) {
                    Graph.NodeResultPath = line.split("\t")[1];
                }else if (line.startsWith("ClusterResultPath")) {
                    Graph.ClusterResultPath = line.split("\t")[1];
                }else if (line.startsWith("HasVisit")) {
                    if(line.split("\t")[1].equals("true")){
                        Graph.hasVisit = true;
                    }else {
                        Graph.hasVisit = false;
                    }
                }else if (line.startsWith("WalkSetNum")) {
                    Graph.WalkSetNum = Integer.parseInt(line.split("\t")[1]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}