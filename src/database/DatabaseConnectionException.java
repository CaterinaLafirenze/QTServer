package database;

/**
 * Eccezione che estende Exception e viene sollevata quando si verifica il fallimento nella connessione del database.
 */
public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException (String msg){
        super(msg);
    }
}
