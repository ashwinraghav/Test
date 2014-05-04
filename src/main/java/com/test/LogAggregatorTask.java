package com.test;

import java.io.*;

/**
 * Created by amohanganesh on 5/3/14.
 */
public class LogAggregatorTask implements Runnable {
    String input;
    public LogAggregatorTask(String input){
        this.input = input;
    }

    //@Override
    public void run() {

        String parts[] = this.input.split(Constants.delimiter);
        String clientId = "global";
        String content = "";

        if(parts[0] != null){ //Client identified
            clientId = parts[0];
        }


        if(parts[1] != null){ //Content identified
            content = parts[1];
        }

        String fileName = Constants.logFolder + clientId;
        appendToFile(content, fileName);

        System.out.println("Appended to " + fileName);
    }

    public void appendToFile(String content, String fileName) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
            out.println(content);
            out.close();
        } catch (IOException e) {
            System.out.println("Unable to append to the log file");
        }
    }


}
