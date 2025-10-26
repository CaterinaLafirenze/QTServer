package bot;

import database.DatabaseConnectionException;
import database.DbAccess;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpEntity;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.invoke.StringConcatFactory;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Paths;
import java.rmi.ServerException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.DbAccess;
import server.MultiServer;


public class QTMiner_Bot extends TelegramLongPollingBot{
    private final String botToken;
    private final String serverIp;
    private final int serverPort ;
    private final Map<String, ClientSession> userSession = new HashMap<>();

    public QTMiner_Bot(String botToken, String serverIp, int serverPort) {
        this.botToken = System.getenv("TOKEN");
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public String getBotUsername(){
        return "WimbledonMiner_bot";
    }

    public String getBotToken(){
        return botToken;
    }



    public void onUpdateReceived(Update update) {
        System.out.println(update);
        if(update.hasMessage() && update.getMessage().hasText()){
            String chatId = update.getMessage().getChatId().toString();
            String receivedMessage = update.getMessage().getText();

            try{
                switch(receivedMessage){
                    case "/end": // va in IOException, in toeria è normale ma meglio ricordarcelo.
                        if(this.userSession.containsKey(chatId)){
                            this.closeSession(chatId);
                            this.sendMessage(chatId, "Connessione terminata. Digita /start per iniziare un nuova sessione.");
                        }else{
                            this.sendMessage(chatId, "Nessuna sessione attiva. Digita /start per iniziare una nuova sessione.");
                        }
                        break;
                    case "/start":
                        this.handleMessage(chatId, receivedMessage);
                        break;
                    default:
                        if(this.userSession.containsKey(chatId)){
                            this.handleMessage(chatId, receivedMessage);
                        } else {
                            this.sendMessage(chatId,"Comando non valido. Digita /start per inziare una nuova sessione.");
                        }

                }
            }catch(ClassNotFoundException | IOException e){
                e.printStackTrace();
            }
        }
    }


    private void handleMessage(String chatId, String receivedMessage) throws IOException, ClassNotFoundException{
        ClientSession session = this.getSession(chatId);
        if(session.state == null){
            session.state = "START";
        }
        if(receivedMessage.equals("/start")){
            session.state="START";
        }
        switch (session.state){
            case "START":
                this.sendMessage(chatId, "Benvenut* a Wimbledon.");
                this.sendMessage(chatId, "(1) Carica il cluster dal file\n(2) Carica i dati dal database\n(1/2): ");
                session.state="MENU";
                break;
            case "MENU":
                    if(receivedMessage.equals("1")){
                        session.state= "LOAD_FILE";
                        this.sendMessage(chatId, "Inserire il nome del file:");
                    }else if(receivedMessage.equals("2")){
                        session.state="LOAD_TABLE";
                        this.sendMessage(chatId,"Table name: ");
                    }else{
                        this.sendMessage(chatId, "Comando non valido.");
                        session.state="MENU";
                    }
                    break;
            case "SAVE_FILE":
                this.handleSaveFile(chatId, receivedMessage);
                break;
            case "LOAD_FILE":
                this.handleLoadClusterFromFile(chatId, receivedMessage);
                break;
            case "LOAD_DATA":
                this.handleLoadData(chatId, receivedMessage);
                break;
            case "LOAD_TABLE":
                this.handleTable(chatId,receivedMessage);
                break;
            case "NEW_EXECUTION":
                this.handleExecution(chatId, receivedMessage);
                break;
            case "LOAD_OPERATION":
                this.handleOperation(chatId, receivedMessage);
                break;
            default:
                this.sendMessage(chatId, "Comando non valido");
        }
    }


    private void closeSession(String chatId) throws IOException {
        ClientSession session = this.userSession.remove(chatId);
        if (session!= null){
            session.out.close();
            session.in.close();
            session.socket.close();
        }
    }



    private ClientSession getSession (String chatId) throws IOException{
        if (!this.userSession.containsKey(chatId)){
            ClientSession session = new ClientSession(this.serverIp, this.serverPort);
            this.userSession.put(chatId, session);
        }
        return this.userSession.get(chatId);
    }



    private void sendMessage(String chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try{
            this.execute(message);
        }catch(TelegramApiException e){
            e.printStackTrace();

        }

    }

    private void handleTable(String chatId, String receivedTableName) throws IOException, ClassNotFoundException {
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(0);
        session.out.writeObject(receivedTableName);
        String result = (String) session.in.readObject();
        if(!result.equals("OK")){
            throw new ServerException(result);
        }
        session.state = "LOAD_DATA";
        this.sendMessage(chatId,"Raggio:");

    }

    private void handleLoadData(String chatId, String receivedRadius) throws IOException, ClassNotFoundException {
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(1);
        if(Double.valueOf(receivedRadius) > 0){
            if(Double.valueOf(receivedRadius) >= 4){
                session.out.writeObject(Double.valueOf(receivedRadius));
                String result = (String) session.in.readObject();
                if(result.equals("OK")){
                    this.sendMessage(chatId, (String) session.in.readObject());
                    session.state = "NEW_EXECUTION";
                    this.sendMessage(chatId, "Vuoi ripetere l'esecuzione?(y/n)");
                }
            }else{
                session.out.writeObject(Double.valueOf(receivedRadius));
                String result = (String)session.in.readObject();
                if(result.equals("OK")){
                    this.sendMessage(chatId,"Number of Clusters:"+  session.in.readObject());
                    this.sendMessage(chatId, (String) session.in.readObject());
                    session.state = "SAVE_FILE";
                    this.sendMessage(chatId, "Nome del file su cui salvare: ");
                }
            }
        }else{
            session.state="LOAD_DATA";
            this.sendMessage(chatId,"Valore del raggio non valido, inserire un raggio >0: ");
        }
    }

    private void handleSaveFile(String chatId, String receivedFileName) throws IOException, ClassNotFoundException{
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(2);
        session.out.writeObject(receivedFileName);
        this.sendMessage(chatId, "\n Salvataggio del cluster in " + receivedFileName + ".dmp\nSalvataggio terminato!\n");
        String result = (String) session.in.readObject();
        if(!result.equals("OK"))
            throw new ServerException(result);
        session.state= "NEW_EXECUTION";
        this.sendMessage(chatId, "Vuoi ripetere l'esecuzione?(y/n)");
    }

//controllare
    private void handleLoadClusterFromFile(String chatId, String receivedMessage) throws IOException, ClassNotFoundException{
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(3);
        //this.sendMessage(chatId, "Inserire il nome del file");
        session.out.writeObject(receivedMessage);
        String result = (String) session.in.readObject();
        if(result .equals("OK")) {
            this.sendMessage(chatId, (String) session.in.readObject());
            session.state = "LOAD_OPERATION";
            this.sendMessage(chatId,"Vuoi scegliere una nuova operazione dal menu?");
        }
        throw new ServerException(result);

    }





    private void handleExecution (String chatId, String receivedChoice) throws IOException, ClassNotFoundException {
        ClientSession session = this.getSession(chatId);
        if(receivedChoice.equals("y")){
            session.state = "LOAD_DATA";
            this.sendMessage(chatId,"Raggio:");
        }else if (receivedChoice.equals("n")){
            session.state = "LOAD_OPERATION";
            this.sendMessage(chatId,"Vuoi scegliere una nuova operazione dal menu?");

        }

    }

    private void handleOperation (String chatId, String receivedChoice) throws IOException, ClassNotFoundException{
        ClientSession session = this.getSession(chatId);
        if (receivedChoice.equals("y")){
            session.state = "MENU";
            this.sendMessage(chatId, "(1) Carica il cluster dal file\n(2) Carica i dati dal database\n(1/2): ");
        }else if(receivedChoice.equals("n")){
            this.sendMessage(chatId,"Connessione chiusa.");
            this.closeSession(chatId);
        }else{
            this.sendMessage(chatId, "Comando non valido.");
        }
    }


    static class ClientSession {
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
        String state;

        /**
         * Costruttore per ClientSession.
         *
         * @param serverIp   L'indirizzo IP del server.
         * @param serverPort La porta del server.
         * @throws IOException Se si verifica un errore di I/O.
         */
        public ClientSession(String serverIp, int serverPort) throws IOException {
            this.socket = new Socket(InetAddress.getByName(serverIp), serverPort);
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
            this.state = null;
        }
    }
    public static void main(String[] args) throws IOException {
        String botToken = args[0];
        String address = args[1];
        int port=Integer.parseInt(args[2]);
        System.out.println("Server avviato sulla porta " + port);
        MultiServer.instanceMultiServer(botToken, address, port);
    }

}