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
        out =new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        start();
    }

    public void run(){
        try {
            while (true) {
                
                Object o =in.readObject();
                //Integer str = (Integer) o;
                if (o.equals("END")) break;
                System.out.println("Echoing: " + o);
                out.writeObject("Echoing: " + o);
                out.flush();

                //out.writeChars(str);
                //out.println();
            }
            System.out.println("closing...");
        } catch(IOException | ClassNotFoundException e) {
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
