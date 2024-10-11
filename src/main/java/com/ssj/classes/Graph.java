package com.ssj.classes;


import java.io.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Graph extends Thread {

    static List<Node> nodeList = new ArrayList<>();
    static List<Point> pointList = new ArrayList<>();
    static List<Cluster> clusterList = new ArrayList<>();
    static int executeCount = 0;

    static List<Job> executeJobList = new ArrayList<>();
    static boolean flag = false;
    static final Object threadStartLock = new Object();
    Boolean hasChild = false;
    public static int NodeNumber = 0;
    public static int ClusterNumber = 0;
    public static int ThreadNumber = 0;
    public static int PerUserVisit = 0;
    public static int SteadyPerUserConnection = 0;
    public static double StartR_max = 0;
    public static double StartHeapTreeCut = 0;
    public static Boolean IsInitR_maxAndHeapTreeCut = false;
    public static Boolean IsDynamicChange = true;
    public static Boolean hasVisit = true;
    public static Boolean isBalanced = true;
    public static String InitJobListPath = "";
    public static String InitVisitPath = "";
    public static String InitClusterPath = "";
    public static String InitR_maxPath = "";
    public static String InitHeapTreeCutPath = "";
    public static String R_maxResultPath = "";
    public static String HeapTreeCutResultPath = "";
    public static String NodeResultPath = "";
    public static String ClusterResultPath = "";

    public static int WalkSetNum = 0;

    public static int SteadyDoor = 0 ;
    //public static ArrayList<Job> executeJobList = new ArrayList<>();
    static final Object executeJobListLock = new Object();

    static int pointNumber = 0;
    static int generateThread = 14;
    static int finifhThread = 0;
    static double d = 0;
    static Collection<ResidueElement> forwardPushResult = new ArrayList<>();
    static double epsilon = 0;
    static double garmma = 0;
    //public static CountDownLatch latch;


    static int edgeCount = 0;
    static final Object edgeCountLock = new Object();

    static int totalInsert = 0;
    static long startTime;
    static long endTime;
    static int perUserEdges;

    static boolean isOutput = true;
    long upTime;
    long bottomTime;

    public static volatile int threadCount = 0;


    static final Object latchLock = new Object();

    boolean writeResult = false;


    public Map<Integer, Integer> executeIdMap = new HashMap<>();

    public Map<Integer, Integer> getExecuteIdMap() {
        return executeIdMap;
    }

    public void setExecuteIdMap(Map<Integer, Integer> executeIdMap) {
        this.executeIdMap = executeIdMap;
    }

    public ArrayList<Job> jobArrayList = new ArrayList<>();

    public void addJobList(String type, int startId, int endId) {
        this.jobArrayList.add(new Job(type, startId, endId));
    }

    public ArrayList<Job> getJobArrayList() {
        return jobArrayList;
    }

    public void setJobArrayList(ArrayList<Job> jobArrayList) {
        this.jobArrayList = jobArrayList;
    }

    public int getJobCount() {
        return jobCount;
    }

    public void setJobCount(int jobCount) {
        this.jobCount = jobCount;
    }

    static Semaphore threadCountMutex = new Semaphore(1);

    public Graph() {
        super(null, null, "Graph", 32 * 1024 * 1024);
    }

    //    private final Object jobListLock = new Object();
    public void insertNode(int id) {
        Node newNode = new Node(id, StartR_max);
        nodeList.add(newNode);
    }

    public void insertEdge(int startId, int endId) throws InterruptedException {
        Node startNode = nodeList.get(startId);
        Node endNode = nodeList.get(endId);
        synchronized (edgeCountLock) {
            edgeCount++;
        }
        startNode.pass(endNode, 1, null, 0, "add");
    }

    public void deleteEdge(int startId, int endId) throws InterruptedException {
        Node startNode = nodeList.get(startId);
        Node endNode = nodeList.get(endId);
        synchronized (edgeCountLock) {
            edgeCount--;
        }

        startNode.pass(endNode, 1, null, 0, "minus");
    }

    public void insertVisit(int nodeId, int pointId) throws InterruptedException {

        Node node = this.getNodeList().get(nodeId);
        Point point = pointList.get(pointId);
        Collection<ResidueElement> backElement = node.getResidueElementCollection();
        node.visitPoint(point);
        //System.out.println("insert:" + startId + "--" + endId);
        //System.out.println("---------------------");
    }

    public Job startJobListNode = new Job("0", 0, 0);

    public int jobCount = 0;

    final Semaphore threadLock = new Semaphore(1);

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public double getGarmma() {
        return garmma;
    }

    public void setGarmma(double garmma) {
        this.garmma = garmma;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    public int getPointNumber() {
        return pointNumber;
    }

    public void setPointNumber(int pointNumber) {
        this.pointNumber = pointNumber;
    }

    public Collection<ResidueElement> getForwardPushResult() {
        return forwardPushResult;
    }

    public void setForwardPushResult(Collection<ResidueElement> forwardPushResult) {
        this.forwardPushResult = forwardPushResult;
    }

    public Job getStartJobListNode() {
        return startJobListNode;
    }

    public void setStartJobListNode(Job startJobListNode) {
        this.startJobListNode = startJobListNode;
    }

    public static List<Cluster> getClusterList() {
        return clusterList;
    }

    public static void setClusterList(List<Cluster> clusterList) {
        Graph.clusterList = clusterList;
    }

    public boolean isAdd = false;
    public boolean isSub = false;

    public void checkResult() throws IOException {
        double totalItems = 0;
        double totalR_max = 0;
        int nodeNum = 0;
        int clusterNum = 0;
        double clusterTotalItems = 0;
        double averageHeapTreeCut = 0;
        for (Node node : nodeList) {
            if (node.outList.size() > 0) {
                nodeNum++;
                totalItems += node.residueElementCollection.size();
                totalR_max += node.getPrivateR_max();
            }
        }
        double totalVisit = 0;
        for (Cluster cluster : clusterList) {
            if (cluster.heapTree.size() > 0) {
                for (Point point : cluster.pointList) {
                    totalVisit += point.visitNodeList.size();
                }
                clusterNum++;
                clusterTotalItems += cluster.heapTree.size();
                averageHeapTreeCut += cluster.private_heapTreeCut;
            }
        }
        double averageItems = totalItems / nodeNum;
        double averageR_max = totalR_max / nodeNum;

        System.out.println("social-network: ----------------");
        System.out.println(nodeNum + "nodes");
        System.out.println("totalItems : " + totalItems);
        System.out.println("averageItems : " + averageItems);
        System.out.println("totalR_max : " + totalR_max);
        System.out.println("averageR_max : " + averageR_max);
        System.out.println("--------------");
        System.out.println("point-user network: --------------");
        System.out.println(clusterNum + "clusters");
        System.out.println("totalItems : " + clusterTotalItems);
        System.out.println("averageItems : " + clusterTotalItems / clusterNum);
        System.out.println("averageHeapTreeCut : " + averageHeapTreeCut / clusterNum);
        System.out.println("perCluster has visit : " + totalVisit / clusterNum);
        FileWriter fw = null;

        if (true) {
            File f = new File(R_maxResultPath);
            fw = new FileWriter(f, false);
            for (Node node : nodeList) {
                if (node.outList.size() > 0) {
                    String writeString = node.id + "\t" + node.getPrivateR_max() + "\n";
                    fw.write(writeString);
                    fw.flush();
                }
            }
            f = new File(HeapTreeCutResultPath);
            fw = new FileWriter(f, false);
            for (Cluster cluster : clusterList) {
                if (cluster.heapTree.size() > 0) {
                    String writeString = cluster.getId() + "\t" + cluster.private_heapTreeCut + "\n";
                    fw.write(writeString);
                    fw.flush();
                }
            }
            f = new File("ExecuteList.txt");
            fw = new FileWriter(f, true);
            for (Job job : executeJobList){
                String writeString =job.getType()+" "+ job.getStartId() + " "+job.getEndId()+"\n";
                fw.write(writeString);
                fw.flush();
            }
            System.out.println("Write finish");
        }
    }

    public void nodeInforWrite() throws IOException {
        FileWriter fw = null;
        File f = new File(NodeResultPath);
        fw = new FileWriter(f, false);
        for (Node node : nodeList) {
            if (!node.residueElementCollection.isEmpty()) {
                String writeString = "nodeID : " + String.valueOf(node.id) + "\n";
                writeString += "ResElements : \n";
                double totalR_max = 0;
                for (ResidueElement residueElement : node.residueElementCollection) {
                    String itemString = "<" + residueElement.getNextStep() + "," + residueElement.getDestination() + "," + residueElement.getResidue() + ">\n";
                    writeString += itemString;
                    totalR_max += residueElement.getResidue();
                }
                writeString += ("totalR_max : " + totalR_max + "\n");
                writeString += ("step1 : " + node.step1 + "\n");
                writeString += ("step2 : " + node.step2 + "\n");
                writeString += "end\n\n";
                fw.write(writeString);
                fw.flush();
            }
        }
        System.out.println("Write Information finish");
    }

    public void clusterInforWrite() throws IOException {
        FileWriter fw = null;
        File f = new File(ClusterResultPath);
        fw = new FileWriter(f, false);
        for (Cluster cluster : clusterList) {
            if (!cluster.heapTree.isEmpty()) {
                String writeString = "clusterID : " + String.valueOf(cluster.getId()) + "\n";
                writeString += "heapTreeNodes : \n";
                double totalHeapTree = 0;
                for (HeapTreeNode heapTreeNode : cluster.heapTree) {
                    String itemString = "<" + heapTreeNode.getEndId() + "," + heapTreeNode.getRes() + ">\n";
                    writeString += itemString;
                    totalHeapTree += heapTreeNode.getRes();
                }
                writeString += ("totalRes : " + totalHeapTree);
                writeString += "end\n\n";
                fw.write(writeString);
                fw.flush();
            }
        }
        System.out.println("Write Information finish");
    }

    public void r_maxInit() {
        System.out.println("start init R_max");
        BufferedReader reader = null;
        String filePath = InitR_maxPath;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(filePath));
            while ((line = reader.readLine()) != null) {
                String lineInfo[] = line.split("\t");
                int nodeId = Integer.parseInt(lineInfo[0]);
                double privateR_max = Double.parseDouble(lineInfo[1]);
                nodeList.get(nodeId).setPrivateR_max(privateR_max);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("end init R_max");
    }

    public void heapTreeCutInit() {
        System.out.println("start init R_max");
        BufferedReader reader = null;
        String filePath = InitHeapTreeCutPath;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(filePath));
            while ((line = reader.readLine()) != null) {
                String lineInfo[] = line.split("\t");
                int clusterID = Integer.parseInt(lineInfo[0]);
                double privateHeapTreeCut = Double.parseDouble(lineInfo[1]);
                clusterList.get(clusterID).private_heapTreeCut = privateHeapTreeCut;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("end init heapTreeCut");
    }

    public void visitInit() {
        System.out.println("start init visit");
        BufferedReader reader = null;
        String filePath = InitVisitPath;
        String line = "";
        int totalVisit = 0;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            while ((line = reader.readLine()) != null) {
                String lineInfo[] = line.split(" ");
                String[] info = lineInfo[1].split("\t");
                int nodeId = Integer.parseInt(info[0]);
                int pointId = Integer.parseInt(info[1]);
                this.insertVisit(nodeId, pointId);
                totalVisit++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("end init visit , totalVisit : " + totalVisit);
    }

    public void midtermOutput(int insertNum) {
        String writeString = "";
        LocalDateTime currentTime = LocalDateTime.now();
        // 定义日期时间格式（可选）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        writeString += ("current insert : " + insertNum + " in " + this.getName() + " currentTime : " + formattedTime + "\n");
        long currentEndTime = System.currentTimeMillis();
        long executeSeconds = currentEndTime - startTime;
        writeString += ("execute Time = " + executeSeconds + "\n");
        double perInsertTime = (double) executeSeconds / Node.totalExecute;
        String str = String.format("%.4f", perInsertTime);
        writeString += ("per Insert time : " + str + "ms" + "\n");
        writeString += ("thread" + this.getName() + " remines " + this.jobCount + " jobs" + "\n");
        writeString += ("current total execute : " + Node.totalExecute + "\n");
        writeString += ("current Edge Count : " + edgeCount + "\n");
        writeString += ("executeCount  : " + executeCount + "\n");
        writeString += ("current threads " + threadCount + "\n\n");

        System.out.println(writeString);
        String filePath = "processOutPut.txt"; // 指定文件路径
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(writeString); // 写入内容
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newJobListInit() {

        this.startJobListNode = new Job("0", 0, 0);
        Job indexJob = this.startJobListNode;
        int count = 0;
        for (Job job : this.jobArrayList) {
            job.nextJob = indexJob.nextJob;
            indexJob.nextJob = job;
            indexJob = indexJob.nextJob;
            count++;
        }
        indexJob.nextJob = null;

        System.out.println(this.getName() + " init finish : jobCount : " + count);

    }

    void jobListInit() throws InterruptedException {
        System.out.println("Init jobList with : Rmax = " + nodeList.get(0).getPrivateR_max());
        InputStream f = null;
        BufferedReader reader = null;
        System.out.println(perUserEdges + " edges, ");
        String filePath = InitJobListPath + "part" + this.getName() + ".txt";
        int insertNum = 0;
        String line = "";
        Job currentJob = this.startJobListNode;
        try {
            reader = new BufferedReader(new FileReader(filePath));

            while ((line = reader.readLine()) != null && !line.equals("")) {
                insertNum++;
                String[] types = line.split(" ");
                String[] values = types[1].split("\t");
                int startId = Integer.parseInt(values[0]);
                int endId = Integer.parseInt(values[1]);
                String type = types[0];
                Job newJob = new Job(type, startId, endId);
                newJob.nextJob = currentJob.nextJob;
                currentJob.nextJob = newJob;
                currentJob = newJob;
                this.jobCount++;
                if (!this.executeIdMap.containsKey(startId)) {
                    this.executeIdMap.put(startId, 1);
                }
            }
            System.out.println(this.getName() + " has key " + this.executeIdMap.size());
            //currentJob.nextJob = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("thread" + this.getName() + " job list init finish : " + insertNum + " jobs");
        Thread.sleep(1000);
    }

    public synchronized void generateSubThread() throws InterruptedException {
        this.isSub = true;

        System.out.println(this.getName() + " add new thread with " + this.jobCount);
        Graph newGraph1 = new Graph();
        Graph newGraph2 = new Graph();
        newGraph1.setName("new1-" + this.getName());
        newGraph2.setName("new2-" + this.getName());

        Map<Integer, Integer> newExecuteMap1 = new HashMap<>();
        Map<Integer, Integer> newExecuteMap2 = new HashMap<>();

        boolean flag = true;
        System.out.println("totalKey " + this.executeIdMap.size());
        for (Integer key : this.executeIdMap.keySet()) {
            if (flag) {
                newExecuteMap1.put(key, 1);
                flag = false;
            } else {
                newExecuteMap2.put(key, 1);
                flag = true;
            }
        }

        System.out.println(newGraph1.getName() + " has key " + newExecuteMap1.size());
        System.out.println(newGraph2.getName() + " has key " + newExecuteMap2.size());

        newGraph1.setExecuteIdMap(newExecuteMap1);
        newGraph2.setExecuteIdMap(newExecuteMap2);
        int count1 = 0;
        int count2 = 0;

        while (this.startJobListNode != null) {
            String type = this.startJobListNode.getType();
            int startId = this.startJobListNode.getStartId();
            int endId = this.startJobListNode.getEndId();

            if (newExecuteMap1.containsKey(startId)) {
                newGraph1.addJobList(type, startId, endId);
                count1++;
            } else {
                newGraph2.addJobList(type, startId, endId);
                count2++;
            }
            this.jobCount--;
            this.startJobListNode = this.startJobListNode.nextJob;
        }
        newGraph1.setJobCount(count1);
        newGraph1.newJobListInit();

        newGraph2.setJobCount(count2);
        newGraph2.newJobListInit();

        newGraph1.start();
        newGraph2.start();

        System.out.println(this.getName() + " add new thread finish : " + this.jobCount);
    }


    @Override
    public void run() {
        BufferedReader reader = null;
        if (this.getName().equals("print")) {

            while (threadCount > 0) {
                isOutput = false;
                try {
                    Thread.sleep(1200000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (!isOutput) {
                    System.out.println("-------------break!-------------");
                    break;
                }
            }

            System.out.println("end all , total insert : " + (Node.totalExecute));
//            System.out.println("executeListCount : " + executeJobList.size());
            endTime = System.currentTimeMillis();
            System.out.println("startTime : " + startTime);
            System.out.println("endTime : " + endTime);
            long executeSeconds = endTime - startTime;
            System.out.println("execute Time = " + executeSeconds);
            double perInsertTime = (double) executeSeconds / Node.totalExecute;
            String str = String.format("%.4f", perInsertTime);
            System.out.println("per Insert time : " + str + "ms");
            System.out.println("executeCount : " + executeCount);
            try {
                this.checkResult();
                this.nodeInforWrite();
                this.clusterInforWrite();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            this.printResidueMap();
//            this.printClusters();
//            this.printInAndOut();
        } else if (this.getName().equals("init")) {// init the graph
            int nodeNum = NodeNumber;//nodeNum
            //----------------------
            int perUserVisits = PerUserVisit;// visit, 0 = test
            int clusterNum = ClusterNumber;
           // String filePath = InitClusterPath;//clusterNums
            perUserEdges = 0;//edges, 0 = no cut, 100 = test
//            Cluster.heapTreeCut = 0;
//            Node.r_max = 0.05;
            this.writeResult = true;
            //----------------------
            for (int i = 0; i <= nodeNum; i++) {
                this.insertNode(i);
            }// init users
            //init cluster and points
//            int clusterNum = 105000;

//            for (int i = 0; i < (clusterNum + 1); i++) {
//                clusterList.add(new Cluster(i, StartHeapTreeCut));
//            }

            pointList.add(new Point(0, new ArrayList<>()));
            pointList.get(0).setBelongTo(new Cluster(0, StartHeapTreeCut));
            //

//            String line = "";
//            try {
//                reader = new BufferedReader(new FileReader(filePath));
//                while ((line = reader.readLine()) != null) {
//                    String lineInfo[] = line.split("\t");
//                    int pointId = Integer.parseInt(lineInfo[0]);
//                    int clusterId = Integer.parseInt(lineInfo[1]);
//                    pointList.add(new Point(pointId, new ArrayList<>()));
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            int exClusterNum = 0;
//            try {
//                reader = new BufferedReader(new FileReader(filePath));
//                while ((line = reader.readLine()) != null) {
//                    String lineInfo[] = line.split("\t");
//                    int pointId = Integer.parseInt(lineInfo[0]);
//                    int clusterId = Integer.parseInt(lineInfo[1]);
//                    pointList.get(pointId).setBelongTo(clusterList.get(clusterId));
//                    clusterList.get(clusterId).pointList.add(pointList.get(pointId));
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            System.out.println(this.getName() + " end , read " + clusterNum + "clusters");

            //--------visit-----------
            if(hasVisit){
                this.visitInit();
            }
            if (IsInitR_maxAndHeapTreeCut) {
                this.r_maxInit();
                this.heapTreeCutInit();
            }
        } else {
            // RUN  thread
            if (!this.getName().startsWith("new")) {
                try {
                    this.jobListInit();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 格式化当前时间（可选）
                LocalDateTime currentTimeS = LocalDateTime.now();
                DateTimeFormatter formatterS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedTimeS = currentTimeS.format(formatterS);

                startTime = System.currentTimeMillis();
                System.out.println("StartTime : " + formattedTimeS + " " + startTime);
                // 打印当前时间
                // System.out.println(this.getName() + " start " + nodeNumber);
            } else {
                //this.newJobListInit();
                System.out.println(this.getName() + " " + this.jobCount + " " + this.jobArrayList.size()); //none-sub thread should be init job list
            }


            int insertNum = 0;

            this.startJobListNode = this.startJobListNode.nextJob;
            while (this.startJobListNode != null) {

                Job job = this.startJobListNode;
//                synchronized (executeJobListLock){
//                    executeJobList.add(new Job(job.type,job.startId,job.endId));
//                }
                int startId = job.getStartId();
                int endId = job.getEndId();
                if (job.getType().equals("add")) {
                    try {
                        this.insertEdge(startId, endId);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if (job.getType().equals("minus")) {
                    try {
                        this.deleteEdge(startId, endId);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                isOutput = true;
                this.jobCount--;

                int currentWalkSetNum = nodeList.get(startId).residueElementCollection.size();
                if (IsDynamicChange) {
                    if (currentWalkSetNum > WalkSetNum) {
                        nodeList.get(startId).overTimeLimit();
                    } else if (currentWalkSetNum < WalkSetNum) {
                        nodeList.get(startId).lowerTimeLimit();
                    }
                }

                insertNum++;
                this.startJobListNode = this.startJobListNode.nextJob;

                synchronized (edgeCountLock) {
                    executeCount ++;
                    if (Node.totalExecute > SteadyDoor && !flag) {
                        System.out.println("----------------entry stable state----------------");
                        this.midtermOutput(insertNum);
                        try {
                            this.checkResult();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        startTime = System.currentTimeMillis();
                        Node.totalExecute = 0;
                        flag = true;
                    }
                }


//                if (threadCount < 3) {
//                    System.out.println("I am working");
//                }

                if (insertNum % 10000 == 0) {
                    midtermOutput(insertNum);
                }


                if(isBalanced){
                    synchronized (latchLock) {
                        if (threadCount < (ThreadNumber - 1) && this.jobCount > 10000 && this.executeIdMap.size() > 3) {
                            threadCount += 2;
                            break;
                        }
                    }
                }
            }
            if (this.jobCount > 10000 && this.executeIdMap.size() > 3 && isBalanced) {
                try {
                    this.generateSubThread();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            midtermOutput(insertNum);
            System.out.println(this.getName() + " finish ");
            System.out.println("current insert : " + Node.totalInsert);

            synchronized (latchLock) {
                threadCount--;
                finifhThread++;
            }

            System.out.println("generate : " + generateThread + " finish : " + finifhThread);
            System.out.println("current threads : " + threadCount + "\n");

        }
    }
}
