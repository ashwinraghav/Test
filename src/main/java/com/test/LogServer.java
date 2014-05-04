package com.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by amohanganesh on 5/3/14.
 * TODO: clean shutdown of thread pools
 */
public class LogServer {

    ServerSocket serverSocket;
    ExecutorService executorService;

    public static LogServer build() {
        LogServer logServer = null;
        try {
            ServerSocket serverSocket = new ServerSocket(Constants.portNumber);
            System.out.println(Thread.currentThread().getId() + " : Listening on port " + Constants.portNumber);

            logServer = new LogServer(serverSocket);

        } finally {//unable to create log server
            return logServer;
        }

    }

    private LogServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.executorService = Executors.newFixedThreadPool(10);

        //start periodic dumps
        (new Thread(new PeriodicStoreDumper(Constants.batchingTimeInterval))).start();
    }


    /**
     * This method currently does not return
     */

    private BufferedReader acceptNewConnection() throws IOException {
        Socket clientSocket = serverSocket.accept();
        System.out.println(Thread.currentThread().getId() + " : Received Connection from " + clientSocket.getInetAddress());
        return new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void listen() throws IOException {
        while (true) {
            BufferedReader in = this.acceptNewConnection();
            String inputLine;

            //read from the connection for as long as possible
            //assumes that no client will hog this thread since clients batch data worth 50ms only.
            while ((inputLine = in.readLine()) != null) {

                //created 2 runnable tasks that are enqueued for execution later
                //these methods return immediately
                //the logging is not reliable

                this.executorService.execute(new LogAggregatorTask(inputLine));
                this.executorService.execute(new KeyValueAggregator(inputLine));
            }
        }
    }


    public static void main(String a[]) throws IOException {
        LogServer logServer = LogServer.build();
        logServer.listen();
    }
}

//executor.shutdown();
// while (!executor.isTerminated()) {
//}

//            while ((inputLine = in.readLine()) != null) {
