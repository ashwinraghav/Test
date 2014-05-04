package com.test;

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by amohanganesh on 5/3/14.
 */
public class ClientFlusher implements Runnable {
    //public static ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();
    public static LinkedBlockingDeque<String> linkedBlockingDeque = new LinkedBlockingDeque<String>();

    //The loggers wait for a max of batchingTimeInterval
    @Override
    public void run() {
        while (true) {
            try {
                //weakly consistent
                ArrayList<String> list = new ArrayList<String>();
                System.out.println(Thread.currentThread().getId() + " : Attempting to read from Batch Queue");
                long start = new DateTime().now().getMillis();
                long current = new DateTime().now().getMillis();


                while (current - start < Constants.batchingTimeInterval) {
                    //Blocking call for a duration of 'dumpingTimeInterval'
                    String s;
                    synchronized (linkedBlockingDeque) {
                        s = linkedBlockingDeque.poll(Constants.batchingTimeInterval, TimeUnit.MILLISECONDS);
                    }
                    if (s != null) {
                        list.add(s);
                    }
                    current = new DateTime().now().getMillis();
                }

                if (list.size() > 0) {
                    System.out.println(Thread.currentThread().getId() + " : Batch Sending .. ");
                    this.log(list);
                } else {
                    System.out.println(Thread.currentThread().getId() + " : Batch Queue Empty");
                }

            } catch (Exception e) {
                System.out.println(Thread.currentThread().getId() + " : Client Flusher Interrupted");
            }
        }
    }

    public void log(ArrayList<String> userInput) throws IOException {
        Socket sock = new Socket(Constants.hostName, Constants.portNumber);
        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);

        for (String str : userInput) {
            out.println(str.toString());
            System.out.println(Thread.currentThread().getId() + " : Logged: " + str.toString());
        }

        out.close();
    }
}
