package com.test;

import org.joda.time.DateTime;

import java.io.*;
import java.net.Socket;

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

    public static LogClient build(String filename) {
        DateTime dateTime = new DateTime().now();
        long threadId = Thread.currentThread().getId();
        String ID = dateTime.toString() + String.valueOf(threadId);
        LogClient logClient = null;

        try {
            //BufferedReader logTail = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader br = new BufferedReader(new FileReader(filename));

            //start the flusher
            (new Thread(new ClientFlusher())).start();

            logClient = new LogClient(ID, br, filename);
        } finally {
            return logClient;
        }
    }

    /**
     * cannot be invoked externally
     *
     * @param id
     * @param logTail
     * @param filename
     */
    private LogClient(String id, BufferedReader logTail, String filename) {
        this.logTail = logTail;
        this.ID = id;
        this.fileName = filename;
    }

    public void tailLogFile() throws InterruptedException {
        String userInput;
        try {
            while (true) {

                userInput = this.logTail.readLine();
                if (userInput == null) {
                    System.out.println(Thread.currentThread().getId() + " : No contents in log file. Sleeping ....");

                    //wait until there is more of the file for us to read
                    Thread.sleep(1000);
                } else {
                    //System.out.println(Thread.currentThread().getId() + " : tailing...");

                    synchronized (ClientFlusher.linkedBlockingDeque){
                        StringBuffer writable = new StringBuffer();
                        writable.append(this.ID).append(Constants.delimiter).append(userInput);

                        //append to the queue to flush
                        ClientFlusher.linkedBlockingDeque.offer(writable.toString());
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
