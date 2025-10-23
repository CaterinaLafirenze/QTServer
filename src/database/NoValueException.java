package database;

/**
 * Eccezione che estende Exception e viene sollevata se non trova il valore nella colonna corrispondente.
 */
public class NoValueException extends Exception {
    public NoValueException (String msg){
        super(msg);
    }
}
