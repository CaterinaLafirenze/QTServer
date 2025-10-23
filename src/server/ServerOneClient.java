package server;

import java.net.*;
import java.io.*;
import java.sql.SQLOutput;
import data.*;
import mining.*;

/**
 *  Classe che permette la comunicazione tra client e server con singola connessione.
 */
public class ServerOneClient extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private QTMiner kmeans;

    /**
     * Costruttore della classe ServerOneClient. Inizializza gli attributi socket, in e out. Avvia il thread.
     * @param s, oggetto di Socket.
     * @throws IOException
     */
    ServerOneClient(Socket s) throws IOException {
        this.socket = s;
        out =new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        start();
    }

    /**
     * Riscrive il metodo run della superclasse Thread al fine di gestire le richieste del client.
     */
    public void run(){

        try {
            Data data= null;
            while(true){
                //legge dal client la scelta effetuata dall'utente nel menu
                Object o = in.readObject();
                System.out.println("Echoing: " + o);

                switch (o.toString()) {
                    //caso in cui l'utente ha scelto di caricare i dati dal datbase.
                    //Riceve il nome della tabella da controllare e manda OK come feedback.
                    case "0":
                        //storeTableFromDB
                        String table = (String) in.readObject();
                        System.out.println("Echoing: " + table);
                        data = new Data(table);
                        out.writeObject("OK");
                        break;


                    case "1":
                        //learningFromBbTable
                        //riceve il valore del raggio definito dall'utente
                        Double radius = (Double) in.readObject();
                        System.out.println("Echoing: " + radius);
                        out.writeObject("OK");
                        //utilizza il raggio per istanziare un nuovo QTminer
                        kmeans = new QTMiner(radius);
                        try {
                            //calcola il centroide e determina il piu popoloso
                            int numIter = kmeans.compute(data);
                            System.out.println("Number of clusters:" + numIter);
                            out.writeObject(numIter);
                        } catch (ClusteringRadiusException e) {
                            out.writeObject(e.getMessage());
                        }
                        //stampa a video nel run del client il valore dei centroidi
                        out.writeObject(kmeans.getC().toString(data));

                        break;
                    //legge il nome del file inviato dal client, salva i centroidi del ClusterSet nel file
                    // e invia OK come feedback.
                    case "2":
                        //storeClusterInFile
                        String filename = (String) in.readObject();
                        kmeans.salva(filename);
                        out.writeObject("OK");
                        break;

                    //carica il file che contiene i valori dei centoridi salvati nella precedente esecuzione
                    //invia un OK come feedback e stampa a video il file.
                    case "3":
                        //learningFromFile
                        kmeans = new QTMiner("/home/paprika/IdeaProjects/QTServer/" + in.readObject());
                        out.writeObject("OK");
                        out.writeObject(kmeans.getC().toString());
                        break;
                }
            }
        } catch(IOException | ClassNotFoundException | EmptyDatasetException e) {
            System.err.println("IO Exception");
        } finally {
            try {
                //chiude lo stream di dati
                socket.close();
            } catch(IOException e) {
                System.err.println("Socket not closed");
            }
        }
    }

}
