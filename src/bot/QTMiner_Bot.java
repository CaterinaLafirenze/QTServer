package bot;

import database.DatabaseConnectionException;
import database.DbAccess;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.apache.httpcomponents.client5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Paths;
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
    //ricordiamoci di metterlo nelle variabili d'ambiente
    private final String botToken;
    private final String serverIp;
    private final int serverPort ;
    private final Map<String, ClientSession> userSession = new HashMap<>();

    public QTMiner_Bot(String botToken, String serverIp, int serverPort) {
        this.botToken = System.getenv("TOKEN");
        serverIp =  "127.0.0.1";
        this.serverIp = serverIp;
        serverPort = 8080;
        this.serverPort = serverPort;
    }

    public String getBotUsername(){
        return "WimbledonMiner_bot";
    }

    public String getBotToken(){
        return botToken;
    }



    public void onUpdateReceived(Update update) {
        //System.out.println(update);
        if(update.hasMessage() && update.getMessage().hasText()){
            String chatId = update.getMessage().getChatId().toString();
            String receivedMessage = update.getMessage().getText();

            try{
                switch(receivedMessage){
                    case "/end":
                        if(this.userSession.containsKey(chatId)){
                            this.closeSession(chatId);
                            this.sendMessage(chatId, "Connessione terminata. Digita /start per iniziare un nuova sessione.");
                        }else{
                            this.sendMessage(chatId, "Nessuna sessione attiva. Digita /start per iniziare una nuova sessione.");
                        }
                    case "/start":
                        this.handleMessage(chatId, receivedMessage);
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

    /**
     * Invia il contentuo di un file all'utente tramite bot Telegram.
     * @param chatId, identificativo della chat.
     * @param filePath, percorso del file da leggere.
     */
    private void sendFileContent(String chatId, String filePath){
        try{
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            String content = String.join("\n", lines);
            this.sendMessage(chatId, content);
        } catch(IOException e){
            e.printStackTrace();
            this.sendMessage(chatId, "Errore durante la lettura del file.");
        }
    }
//da fare dopo
    private void handleMessage(String chatId, String receivedMessage) throws IOException, ClassNotFoundException{
        ClientSession session = this.getSession(chatId);
        String infoFilePath = System.getProperty("user.dir") + "/info.txt";
        if(session.state == null){
            session.state = "START";
        }
        switch (session.state){
            case "START":
                if(receivedMessage.equals("/start")){
                    this.sendFileContent(chatId, infoFilePath);
                    session.out.writeObject(0);
                    List <String> tableName = getTableNames();
                    this.sendMessage(chatId, "Tabella del database: playtennis");
                    this.sendMessage(chatId, "Inserire il nome della tabella da caricare.");
                    session.state = "LOAD_DATA";
                }
                break;
            case "MENU":
                if(receivedMessage.equals("1")){
                    session.out.writeObject(3);
                    this.sendMessage(chatId, "Inserisci il nome del file da caricare: ");
                    session.state = "LOAD_FILE";
                } else if(receivedMessage.equals("2")){
                    session.out.writeObject(0);
                    this.sendMessage(chatId, "Inserisci il raggio (da 1 a 4): ");
                    session.state = "ENTER_RADIUS";
                } else{
                this.sendMessage(chatId, "Comando non valido. \n1. Carica Cluster dal file\n2. Carica dati dal database.");
                session.state = "MENU";
                }

                break;
            case "SALVA_FILE":
                session.out.writeObject(2);
                this.handleSaveFile(chatId, infoFilePath);
                break;
            case "LOAD_FILE":
                this.handleLoadClusterFromFile(chatId, receivedMessage);
                break;
            case "LOAD_DATA":
                this.handleLoadData(chatId, receivedMessage);
                break;
            case "ENTER_RADIUS":
                this.handleRadius(chatId, receivedMessage);
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

    private void handleRadius(String chatId, String depthStr) throws IOException, ClassNotFoundException {
        int radius;
        try{
            radius = Integer.parseInt(depthStr);
        }catch(NumberFormatException e){
            this.sendMessage(chatId, "Raggio inserito non valido. \n Inserisci un valore compreso tra 1 e 4");
            return;
        }
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(radius);
        String answer = (String) session.in.readObject();
        if (answer.equals("OK")){
            this.sendMessage(chatId, (String) session.in.readObject());
            this.sendMessage(chatId, "Inserisci il nome del file.");
            session.state = "SAVE_FILE";
        }else{
            this.sendMessage(chatId, answer);
            session.out.writeObject(1);
            this.sendMessage(chatId, "Inserisci il raggio (valore compreso tra 1 e 4): ");
            session.state = "ENTER_RADIUS";
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

    private void handleLoadClusterFromFile(String chatId, String fileName) throws IOException, ClassNotFoundException{
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(fileName);
        String risposta = (String)  session.in.readObject();
        if(risposta.equals("OK")){
            this.sendMessage(chatId, (String) session.in.readObject());
            this.sendMessage(chatId, "Caricamento completato, digita /start per iniziare una nuova sessione.");
            this.closeSession(chatId);
            session.state = "START";
        }else{
            this.sendMessage(chatId, risposta);
            this.sendMessage(chatId, "Inserisci il nome del file da caricare: ");
            session.out.writeObject(2);
            session.state = "LOAD_FILE";
        }
    }

    private void handleLoadData(String chatId, String tableName) throws IOException, ClassNotFoundException{
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(tableName);
        String answer = (String) session.in.readObject();
        if (answer.equals("OK")){
            this.sendMessage(chatId, "Indicare l'opzione: \n1. Carica Cluster da File\n2. Apprendi Cluster da Database");
            session.state = "MENU";
        }else{
            this.sendMessage(chatId, answer);
            this.sendMessage(chatId, "Inserire il nome della tabella da caricare.");
            session.out.writeObject(0);
            session.state = "LOAD_DATA";
        }
    }

    private void handleSaveFile(String chatId, String fileName) throws IOException, ClassNotFoundException{
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(fileName);
        String answer = (String) session.in.readObject();
        if(answer.equals("OK")){
            this.sendMessage(chatId, (String) session.in.readObject());
            this.sendMessage(chatId, "Caricamento completato, digita /start per una nuova sessione.");
            this.closeSession(chatId);
            session.state = "START";
        }else{
            this.sendMessage(chatId, answer);
            this.sendMessage(chatId, "Inserire il nome della tabella da caricare.");
            session.state = "SAVE_FILE";
        }
    }

    private List<String> getTableNames(){
        List<String> tableNames = new ArrayList<>();
        DbAccess dbAccess = new DbAccess();
        try{
            dbAccess.initConnection();
            DatabaseMetaData meta = dbAccess.getConnection().getMetaData();
            ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"});
            while(rs.next()){
                tableNames.add(rs.getString("TABLE_NAME"));
            }
            rs.close();
            dbAccess.closeConnection();
        }catch(DatabaseConnectionException | SQLException e){
            e.printStackTrace();
        }
        return tableNames;
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
    public static void main(String[] args) {
        if (args.length == 0 || args.length > 3) {
            System.err.println("Utilizzo: java Main <BOT_TOKEN> <IP_ADDRESS> <SERVER_PORT>");
            System.exit(1);
        }

        String botToken = args[0];
        // Controllo sul bot token (lunghezza minima e non null)
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot token non valido.");
            System.exit(1);
        }

        String address = args[1];
        if (!isValidIPAddress(address)) {
            System.err.println("Indirizzo IP non valido: " + address);
            System.exit(1);
        }

        int port;
        try {
            port = Integer.parseInt(args[2]);
            if (port < 0 || port > 65535) {
                System.err.println("Numero di porta non valido: " + args[2]);
                System.exit(1);
            }
        } catch (NumberFormatException e) {
            System.err.println("Numero di porta non valido: " + args[2]);
            System.exit(1);
            return;
        }

        System.out.println("Server avviato sulla porta " + port);
        new QTMiner_Bot(botToken, address, port);
    }

    private static boolean isValidIPAddress(String ip) {
        String ipPattern =
                "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-5][0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-5][0-5])$";
        Pattern pattern = Pattern.compile(ipPattern);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

}
