package server;

import java.io.IOException;
import java.net.*;


public class MultiServer extends ServerSocket {
    private int PORT = 8080;

    MultiServer( int port)throws IOException{
        this.PORT = port;
        run();
    }

    void run()throws IOException {
        ServerSocket s = new ServerSocket(PORT);
        Socket socket = s.accept();
        System.out.println( "Connessione iniziata: " + socket.getInetAddress());
        
        try{
            while(true){
                try{
                    new ServerOneClient(socket);
                }catch(IOException e){
                    socket.close();
                } 
            }
        } finally{
            s.close();
            System.out.println( "Connessione chiusa: " + socket.getInetAddress());
        }
    }


    public static void main(String[] args) {
        
        try {
            MultiServer ms = new MultiServer(8080);
        }catch(IOException e){
            e.printStackTrace();
        }
    }


}

