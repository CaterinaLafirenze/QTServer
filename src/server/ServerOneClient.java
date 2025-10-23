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


        //QTMiner qt = null;



        try {
            Data data= null;
            while(true){

                //Data data = null;
                Object o = in.readObject();
                System.out.println("Echoing: " + o);
                switch (o.toString()) {
                    case "0": //storeTableFromDB
                        String table = (String) in.readObject();
                        System.out.println("Echoing: " + table);
                        data = new Data(table);
                        out.writeObject("OK");
                        break;
                    case "1":

                        Double radius = (Double) in.readObject();//learningFromBbTable
                        System.out.println("Echoing: " + radius);

                        out.writeObject("OK");

                        //inserire tutti i calcoli
                        kmeans = new QTMiner(radius);
                        try {
                            int numIter = kmeans.compute(data);
                            System.out.println("Number of clusters:" + numIter);
                            out.writeObject(numIter);
                        } catch (ClusteringRadiusException e) {
                            out.writeObject(e.getMessage());
                        }
                        System.out.println(kmeans.getC().toString(data));
                        out.writeObject(kmeans.getC().toString(data));

                        break;
                    case "2"://storeClusterInFile
                        //problemi qui
                        //out.writeObject("Backup file name:");
                        String filename = (String) in.readObject();
                        kmeans.salva(filename);
                        out.writeObject("OK");
                        //out.writeObject("\nSaving clusters in " + filename +
                                //".dmp\nSaving transaction ended!\n");
                        break;

                    case "3"://learningFromFile
                        //va in 2 poi torna in 3come mai?
                        kmeans = new QTMiner("/home/paprika/IdeaProjects/QTServer/" + in.readObject());
                        out.writeObject("OK");
                        out.writeObject(kmeans.getC().toString());//stampa solo i centroidi,
                        //???
                        break;
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
