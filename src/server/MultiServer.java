package server;

import java.io.IOException;
import java.net.*;

/**
 * Classe che permette la comunicazione tra client e server con molteplici connessioni.
 */
public class MultiServer extends ServerSocket {
    private int PORT = 8080;

    /**
     * Costruttore della classe MultiServer. Inizializza la porta ed invoca il metodo run().
     * @param port, la porta sulla quale avviene la connessione con il server.
     * @throws IOException
     */
    public MultiServer(int port)throws IOException{
        this.PORT = port;
        run();
    }

    /**
     * Istanzia un oggetto della classe ServerSocket che pone in attesa di richiesta di connessioni da parte del client-
     * Ad ogni nuova richesta di connessione si istanzia la classe ServerOneClient.
     * @throws IOException
     */
    void run()throws IOException {

        ServerSocket s = new ServerSocket(PORT);
        System.out.println( "Connessione iniziata: ");
        try{
            while(true){
                Socket socket = s.accept();
                try{
                    new ServerOneClient(socket);
                }catch(IOException e){
                    socket.close();
                }
            }
        } finally{
            s.close();
            System.out.println( "Connessione chiusa: ");
        }
    }

    /**
     * Istanzia un oggetto di tipo MultiServer.
     * @param args
     */
    public static void main(String[] args) {
        
        try {
            MultiServer ms = new MultiServer(8080);
        }catch(IOException e){
            e.printStackTrace();
        }
    }


}

