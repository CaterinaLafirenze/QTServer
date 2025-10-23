package mining;

/**
 * Eccezione che estende Exception che viene sollevata nel caso in cui uno unico Cluster contenga tutte le tuple.
 */
public class ClusteringRadiusException extends Exception{
    public ClusteringRadiusException (String msg){
        super(msg);
    }

}
