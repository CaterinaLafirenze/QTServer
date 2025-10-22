package server;

import java.net.*;
import java.io.*;
import java.sql.SQLOutput;
import data.*;
import mining.*;


public class ServerOneClient extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private QTMiner kmeans;

    ServerOneClient(Socket s) throws IOException {
        this.socket = s;
        out =new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        start();
    }

    public void run(){


        QTMiner qt = null;

        try {
            Data data= null;
            while (true) {
            QTMiner kmeans = new QTMiner("/home/paprika/dataSet.txt");
            //Data data = null;
            Object o = in.readObject();
            System.out.println("Echoing: " + o);
            switch(o.toString()){
                case "0": //storeTableFromDB
                    String table = (String) in.readObject();
                    System.out.println("Echoing: " + table);
                    data = new Data(table);
                    out.writeObject("OK");
                    break;
                case "1"://learningFromBbTable
                    Double radius = (Double) in.readObject();
                    System.out.println("Echoing: " + radius);
                    out.writeObject("OK");
                    //inserire tutti i calcoli
                    qt = new QTMiner(radius);
                    try {

                        int numIter = qt.compute(data);
                        System.out.println("Number of clusters:" + numIter);
                        out.writeObject(numIter);
                    } catch (ClusteringRadiusException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println(qt.getC().toString(data));
                    out.writeObject(qt.getC().toString(data) );
                    break;
                case "2"://storeClusterInFile
                    out.writeObject("Backup file name:");
                    String filename = (String) in.readObject();
                    System.out.print("\nSaving clusters in "+ filename +
                        ".dmp\nSaving transaction ended!\n");
                    System.out.println("New execution?(y/n)");
                    kmeans.salva(filename);
                    out.writeObject("OK");
                break;
                case "3"://learningFromFile
                    if (kmeans != null) {
                        System.out.println(kmeans);//stampa solo i centroidi,
                    }
                    out.writeObject("OK");
            }
            }
        //System.out.println("closing...");
        } catch(IOException | ClassNotFoundException | EmptyDatasetException e) {
            System.err.println("IO Exception");
        } finally {
            try {
                socket.close();
            } catch(IOException e) {
                System.err.println("Socket not closed");
            }
        }
    }

}
