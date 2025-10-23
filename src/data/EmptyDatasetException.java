package data;

/**
 * Eccezione che viene sollevata nel caso in cui il dataSet è vuoto.
 */
public class EmptyDatasetException extends Exception{
    public EmptyDatasetException (String msg){
        super(msg);
    }
}
