package com.ssj.threads;

import com.ssj.classes.Graph;

import java.io.*;
import java.text.SimpleDateFormat;

public class Stream extends Thread{

    public String fileAddr;
    public Graph G;


    @Override
    public void run(){

        InputStream f1 = null;
        try {
            f1 = new FileInputStream(fileAddr);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        InputStreamReader reader = new InputStreamReader(f1);
        BufferedReader br = new BufferedReader(reader);
        String strTmp = "";
        while (true) {
            try {
                if (!((strTmp = br.readLine()) != null)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String[] values = strTmp.split("\t");
            int startId = Integer.parseInt(values[0]);
            int endId = Integer.parseInt(values[1]);

            //G.insertEdge(startId, endId);

            //System.out.println(startId + "\t" + endId);
        }
    }


}
