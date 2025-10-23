package mining;
import data.*;

import java.io.Serializable;
import java.util.*;
import java.lang.Iterable;

/**
 * Classe che rappresenta uno insieme di Cluster determinati da QT.
 */
public class ClusterSet implements Iterable<Cluster>, Serializable {

    private Set<Cluster> C;

    /**
     * Costruttore della classe ClusterSet. Inizializza un TreeSet.
     */
    ClusterSet() {
        C = new TreeSet<>(); //ricordarsi di controllare

    }

    /**
     * Crea lo iteratore di Cluster.
     * @return lo iteratore.
     */
    public Iterator<Cluster> iterator(){
        return C.iterator();
    }

    /**
     * Aggiunge un cluster al clusterSet.
     * @param c, oggetto di Cluster
     */
    void add (Cluster c){
        C.add(c);
    }


    /**
     * Restituisce una stringa fatta da ciascun centroide dell'insieme dei cluster.
     * @return str.
     */
    public String toString(){
        String str = "";
        for(Cluster c : C) {
            str += c + "\n";
        }
        return str;
    }

    /**
     * Crea la stringa che descriva lo stato di ciascun cluster nel clusterSet
     * @param data, oggetto di Data.
     * @return la strnga.
     */
    public String toString(Data data){
        String str = "";
        int i = 1;
        for(Cluster c : C) {
                str+= i + " : " + c.toString(data) +"\n";
                i++;

        }
        return str;
    }





}
