package com.test;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
//import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by amohanganesh on 5/3/14.
 */
public class LogClient {
    String ID;
    BufferedReader logTail;

    /**
     * a combination of the current time and threadID should provided sufficiently unique IDs
     * the static constructor to return null if the log file is inaccessible
     */

    public static LogClient build(){
        DateTime dateTime = new DateTime().now();
        long threadId = Thread.currentThread().getId();
        String ID = dateTime.toString() + String.valueOf(threadId);
        LogClient logClient = null;

        try{
        BufferedReader logTail = new BufferedReader(new InputStreamReader(System.in));

        logClient = new LogClient(ID, logTail);
        }finally {
            return logClient;
        }
    }

    /**
     * cannot be invoked externally
     * @param id
     * @param logTail
     */
    private LogClient(String id, BufferedReader logTail){
        this.logTail = logTail;
        this.ID = id;
    }

    public void log(String userInput) throws IOException {
        StringBuffer writable = new StringBuffer();

        Socket sock = new Socket(Constants.hostName, Constants.portNumber);
        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
        writable.append(this.ID).append(Constants.delimiter).append(userInput);

        out.println(writable.toString());
        out.close();
        System.out.println("Logged: " + writable.toString());

    }

    public void tailLogFile(){
        String userInput;

        try {
            while ((userInput = this.logTail.readLine()) != null) {
                this.log(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error Occured while tailing file");
        }
    }

    public static void main(String a[]){
        LogClient logClient = LogClient.build();
        logClient.tailLogFile();
    }
}
