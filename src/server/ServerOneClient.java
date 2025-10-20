package server;

import mining.QTMiner;
import java.net.*;
import java.io.*;


public class ServerOneClient extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private QTMiner kmeans;

    ServerOneClient(Socket s) throws IOException {
        this.socket = s;
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
        start();
    }

    public void run(){
        try {
            while (true) {
                String str = in.readLine();
                if (str.equals("END")) break;
                System.out.println("Echoing: " + str);
                out.writeChars(str);
                System.out.println();
            }
            System.out.println("closing...");
        } catch(IOException e) {
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
