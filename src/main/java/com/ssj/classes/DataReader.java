package com.ssj.classes;

import java.io.*;

public class DataReader {


    public void readData() throws FileNotFoundException {
        InputStream f1 = new FileInputStream("./data/flow_test.txt");
        InputStreamReader reader = new InputStreamReader(f1);
        BufferedReader br = new BufferedReader(reader);
    }

}
