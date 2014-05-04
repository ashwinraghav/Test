package com.test;

import java.io.*;

/**
 * Created by amohanganesh on 5/3/14.
 */
public class LogAggregatorTask implements Runnable {
    String input;

    public LogAggregatorTask(String input) {
        this.input = input;
    }

    /**
     * Splice the log as 1)ClientID and 2)Content
     * If an ID is not found log is appended to a "global" log file
     * TODO: Batching disk writes need to be implemented to increase throughput
     */
    @Override
    public void run() {
        try {
            String parts[] = this.input.split(Constants.delimiter);
            String clientId = "global";
            String content = "";

            if (parts[0] != null) { //Client identified
                clientId = parts[0];
            }


            if (parts[1] != null) { //Content identified
                content = parts[1];
            }

            String fileName = Constants.logFolder + clientId;
            appendToFile(content, fileName);

            System.out.println(Thread.currentThread().getId() + " : Appended to " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(Thread.currentThread().getId() + " : Unable to update store. Incorrect Log format");
        }
    }

    public void appendToFile(String content, String fileName) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
            out.println(content);
            out.close();
        } catch (IOException e) {
            System.out.println(Thread.currentThread().getId() + " : Unable to append to the log file");
        }
    }


}
