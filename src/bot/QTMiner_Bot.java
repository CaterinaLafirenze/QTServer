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

import java.io.FileNotFoundException;
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

/**
 * Classe che permette la comunicazione tra il server e il bot Telegram.
 */
public class QTMiner_Bot extends TelegramLongPollingBot{
    private final String botToken;
    private final String serverIp;
    private final int serverPort ;
    private final Map<String, ClientSession> userSession = new HashMap<>();
    List<String> fileList= new ArrayList<>();

    /**
     * Costruttore che inizializza il token, ip e porta contenuti nelle variabili di ambiente.
     * @param botToken valore ricevuto dal botFather.
     * @param serverIp 127.0.0.1.
     * @param serverPort 8080.
     */
    public QTMiner_Bot(String botToken, String serverIp, int serverPort) {
        this.botToken = System.getenv("TOKEN");
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    /**
     * Metodo che prende lo username del bot.
     * @return lo username.
     */
    public String getBotUsername(){
        return "WimbledonMiner_bot";
    }

    /**
     * Metodo che prende il token identificativo del bot.
     * @return il token.
     */
    public String getBotToken(){
        return botToken;
    }

    /**
     * Metodo che viene richiamato automaticamente quando lo utente invia un messaggio al server tramite il bot.
     * Nel caso in cui il comando inviato è "/end" il server chiude la connessione, nel caso in cui è "/start" richiama
     * un altro metodo per avviare la comunicazione. Nel caso in cui il comando inviato non sia valido, stampa un errore.
     * @param update contiene sia il messaggio dello utente che le sue informazioni.
     */

    public void onUpdateReceived(Update update) {
        System.out.println(update);
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

    /**
     * Il metodo inizia una nuova sessione prendendo in carico il valore del chatId. Dopo aver ricevuto il messaggio di /start
     * saluta lo utente e gli sottopone le due scelte di caricamento da file o da database. In base alla scelta dello utente
     * verranno richiamati i vari metodi.
     * @param chatId  identificativo della chat su Telegram.
     * @param receivedMessage stringa che contiene il messaggio inviato dallo utente al bot.
     * @throws IOException
     * @throws ClassNotFoundException
     */
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
                    this.sendMessage(chatId, "(1) Carica il cluster dal file\n(2) Carica i dati dal database\n(1/2): ");
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
        }
    }

    /**
     * Metodo che chiude la connessione.
     * @param chatId  identificativo della chat su Telegram.
     * @throws IOException
     */
    private void closeSession(String chatId) throws IOException {
        ClientSession session = this.userSession.remove(chatId);
        if (session!= null){
            session.out.close();
            session.in.close();
            session.socket.close();
        }
    }

    /**
     * Metodo che prende la connessione.
     * @param chatId identificativo della chat su Telegram.
     * @return identificativo della chat.
     * @throws IOException
     */

    private ClientSession getSession (String chatId) throws IOException{
        if (!this.userSession.containsKey(chatId)){
            ClientSession session = new ClientSession(this.serverIp, this.serverPort);
            this.userSession.put(chatId, session);
        }
        return this.userSession.get(chatId);
    }


/**
 * Metodo che invia messaggi allo utente tramite il bot.
 */
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

    /**
     * Metodo che dopo aver ricevuto in nome della tabella dallo utente, confronta con quelle presenti allo intenro del database e se non presente
     * gestisce il caso, altrimenti procede con la esecuzione richiedendo il valore del raggio e prosegue nel nuovo stato.
     * @param chatId  identificativo della chat su Telegram.
     * @param receivedTableName nome della tabella ricevuta in input dalla chat Telegram.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void handleTable(String chatId, String receivedTableName) throws IOException, ClassNotFoundException {
        ClientSession session = this.getSession(chatId);
        if(receivedTableName.equals("playtennis")){
            session.out.writeObject(0);
            session.out.writeObject(receivedTableName);
            String result = (String) session.in.readObject();
            if(!result.equals("OK")){
                throw new ServerException(result);
            }
            session.state = "LOAD_DATA";
            this.sendMessage(chatId,"Raggio:");
        }else{
            session.state = "LOAD_TABLE";
            this.sendMessage(chatId, "Tabella non presente nel database. Inserire tabella esistente:");
        }
    }

    /**
     * Metodo che viene chiamato dopo aver inserito il nome della tabella. Si aspetta in input il valore del raggio.
     * Fa dei controlli sul raggio nel caso in cui: il valore non è un numero, il numero è minore o uguale a 0, in entrambi
     * i casi fa reinserire il valore del raggio. Poi si divide in altri casi: il valore è compreso tra 1 e 3, il server
     * invia il numero di cluster relativi al raggio inserito e successivamente la stampa, poi chiede il nome del file
     * su cui salvare la transazione; il valore è maggiore o uguale a 4, stampa una stringa e chiede se si vuole effettuare
     * una nuova esecuzione.
     * @param chatId identificativo della chat su Telegram.
     * @param receivedRadius raggio ricevuto dall'utente.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void handleLoadData(String chatId, String receivedRadius) throws IOException, ClassNotFoundException {
        ClientSession session = this.getSession(chatId);

        double radius;
        try{
            radius = Double.valueOf(receivedRadius);
        } catch (NumberFormatException e) {
            session.state = "LOAD_DATA";
            this.sendMessage(chatId, "Valore del raggio non valido. Inserire un raggio >0:");
            return;
        }

        if(radius <= 0){
            session.state= "LOAD_DATA";
            this.sendMessage(chatId,"Valore del raggio non valido, inserire un raggio >0: ");
            return;
        }
        session.out.writeObject(1);
        session.out.writeObject(radius);
        String result= (String) session.in.readObject();
        if(result.equals("OK")){
            this.sendMessage(chatId, "Number of Clusters:"+  session.in.readObject());
            String cluster = (String) session.in.readObject();
            if(!cluster.isEmpty()){
                this.sendMessage(chatId, cluster);
            } else{
                this.sendMessage(chatId, "nessuna descrizione del cluster");
            }

            if(radius >= 4){
                session.state = "NEW_EXECUTION";
                this.sendMessage(chatId,"Vuoi ripetere l'esecuzione?(y/n)");
            } else{
                session.state = "SAVE_FILE";
                this.sendMessage(chatId, "Nome del file su cui salvare: ");
            }
        }else {
            // Gestione errore del server
            this.sendMessage(chatId, "Errore dal server: " + result);
            session.state = "MENU";
            this.sendMessage(chatId, "(1) Carica il cluster dal file\n(2) Carica i dati dal database\n(1/2): ");
        }
    }

    //aggiungere il fatto che salva il file nella lista
    /**
     * Metodo che permette il salvataggio dei cluster in un file con nome determinato dallo utente. Al termine della operazione avvisa lo utente del formato del file e del
     * corretto salvataggio e richiede se vuole effettuare una nuova esecuzione sulla stessa tabella.
     * @param chatId  identificativo della chat su Telegram.
     * @param receivedFileName valore ricevuto in input dalla chat e utilizzato per nominare il file di salvataggio.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void handleSaveFile(String chatId, String receivedFileName) throws IOException, ClassNotFoundException{
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(2);
        fileList.add(receivedFileName);
        session.out.writeObject(receivedFileName);
        this.sendMessage(chatId, "\n Salvataggio del cluster in " + receivedFileName + ".dmp\nSalvataggio terminato!\n");
        String result = (String) session.in.readObject();
        if(!result.equals("OK"))
            throw new ServerException(result);
        session.state= "NEW_EXECUTION";
        this.sendMessage(chatId, "Vuoi ripetere l'esecuzione?(y/n)");
    }

    /**
     * Metodo che viene chiamato nel caso in cui l'utente, dal menu, sceglie l'opzione 2.
     * Riceve in input il nome del file da caricare e controlla che sia contenuto nella lista dei file salvati.
     * Se è presente nella lista, stampa il contenuto del file, altriemnti chiede di inserire il nome di un altro file.
     * @param chatId identificativo della chat di Telegram.
     * @param receivedMessage valore ricevuto in input dalla chat e utilizzato per caricare il file.
     * @throws IOException
     * @throws ClassNotFoundException
     */

    private void handleLoadClusterFromFile(String chatId, String receivedMessage) throws IOException, ClassNotFoundException {
        ClientSession session = this.getSession(chatId);

            if (fileList.contains(receivedMessage)) {
                session.out.writeObject(3);
                session.out.writeObject(receivedMessage);
                String result = (String) session.in.readObject();
                if (result.equals("OK")) {
                    this.sendMessage(chatId, (String) session.in.readObject());
                    session.state = "LOAD_OPERATION";
                    this.sendMessage(chatId, "Vuoi scegliere una nuova operazione dal menu (y/n)?");
                } else {
                    throw new ServerException(result);
                }
            }else{
                session.state="LOAD_FILE";
                this.sendMessage(chatId, "File non esistente. Inserire un nuovo file:");
            }


    }


    /**
     * Metodo che gestisce le nuove esecuzioni nel caso di un nuovo calcolo tramite lo input di un nuovo raggio o la scelta
     * di una nunova operazione.
     * @param chatId  identificativo della chat su Telegram.
     * @param receivedChoice stringa che contine la scelta effettauta dallo utente.
     * @throws IOException
     */
    private void handleExecution (String chatId, String receivedChoice) throws IOException{
        ClientSession session = this.getSession(chatId);
        if(receivedChoice.equals("y")){
            session.state = "LOAD_DATA";
            this.sendMessage(chatId,"Raggio:");
        }else if (receivedChoice.equals("n")){
            session.state = "LOAD_OPERATION";
            this.sendMessage(chatId,"Vuoi scegliere una nuova operazione dal menu?(y/n)");

        }else{
            this.sendMessage(chatId, "Comando non valido. Inserire (y/n):");
        }

    }

    /**
     * Metodo che riceve in input la scelta dello utente di voler selezionare una nuova operazione dal menu.
     * Se la risposta è "y" stampa il menu e va nello stato MENU, altrimenti chiude la connesisone.
     * Se viene inserito un comando non valido, chiede di inserirne uno tra y e n.
     * @param chatId identificativo della chat su Telegram.
     * @param receivedChoice
     * @throws IOException
     */
    private void handleOperation (String chatId, String receivedChoice) throws IOException{
        ClientSession session = this.getSession(chatId);
        if (receivedChoice.equals("y")){
            session.state = "MENU";
            this.sendMessage(chatId, "(1) Carica il cluster dal file\n(2) Carica i dati dal database\n(1/2): ");
        }else if(receivedChoice.equals("n")){
            this.sendMessage(chatId,"Connessione chiusa.");
            this.closeSession(chatId);
        }else{
            this.sendMessage(chatId, "Comando non valido. Inserire y/n:");
        }
    }

    /**
     * Classe che si occupa della nuova connessione utente Telegram e server.
     */
    static class ClientSession {
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
        String state;

        /**
         * Costruttore per ClientSession.
         *
         * @param serverIp   lo indirizzo IP del server.
         * @param serverPort la porta del server.
         * @throws IOException
         */
        public ClientSession(String serverIp, int serverPort) throws IOException {
            this.socket = new Socket(InetAddress.getByName(serverIp), serverPort);
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
            this.state = null;
        }
    }

    /**
     * Istanzia il multiserver.
     * @param args botToken, adress, port.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String botToken = args[0];
        String address = args[1];
        int port=Integer.parseInt(args[2]);
        System.out.println("Server avviato sulla porta " + port);
        MultiServer.instanceMultiServer(botToken, address, port);
    }

}