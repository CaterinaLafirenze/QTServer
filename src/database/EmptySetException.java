package database;

/**
 * Eccezione che stende la Exception per modellare la restituzione di resultset vuoto.
 */
public class EmptySetException extends Exception {
    public EmptySetException (String msg){
        super(msg);
    }
}
