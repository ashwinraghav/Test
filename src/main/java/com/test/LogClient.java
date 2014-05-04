package com.test;

import org.joda.time.DateTime;

import java.io.*;

/**
 * Created by amohanganesh on 5/3/14.
 */
public class LogClient {
    String ID;
    BufferedReader logTail;
    String fileName;

    /**
     * a combination of the current time and threadID should provided sufficiently unique IDs
     * the static constructor to return null if the log file is inaccessible
     */

    private String generateUniqueClientID(){
        DateTime dateTime = new DateTime().now();
        long threadId = Thread.currentThread().getId();
        return dateTime.toString() + String.valueOf(threadId);
    }

    public static LogClient build(String filename) {
        LogClient logClient = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            //start the Batcher
            (new Thread(new LogBatcher(Constants.batchingTimeInterval))).start();

            logClient = new LogClient(br, filename);
        } finally {
            return logClient;
        }
    }

    /**
     * cannot be invoked externally
     *
     * @param logTail
     * @param filename
     */
    private LogClient(BufferedReader logTail, String filename) {
        this.logTail = logTail;
        this.ID = generateUniqueClientID();
        this.fileName = filename;
    }

    public void tailLogFile() throws InterruptedException {
        String userInput;
        try {
            while (true) {

                userInput = this.logTail.readLine();

                if (userInput == null) { //Sleep for a bit. Save some CPU
                    System.out.println(Thread.currentThread().getId() + " : No contents in log file. Sleeping ....");
                    Thread.sleep(Constants.dumpingTimeInterval);
                } else {

                    synchronized (LogBatcher.batchingQueue){//enqueue for dispatch
                        StringBuffer writable = new StringBuffer();
                        writable.append(this.ID).append(Constants.delimiter).append(userInput);

                        //append to the queue to flush
                        LogBatcher.batchingQueue.offer(writable.toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String a[]) throws InterruptedException {

        String logFile;
        if (a.length != 1) {
            System.out.println("Input format should be : filename");
            logFile = "/tmp/foo";
            //return;
        }else{
            logFile = a[0];
        }

        LogClient logClient = LogClient.build(logFile);
        logClient.tailLogFile();
    }
}
