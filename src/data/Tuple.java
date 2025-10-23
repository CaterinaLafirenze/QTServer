package data;
import java.io.Serializable;
import java.util.*;
import java.util.HashSet;

/**
 * Classe che rappresenta una Tupla come sequenza di coppie attributo valore.
 */
public class Tuple implements Serializable {

    private Item[] tuple;

    Tuple(int size) {
        tuple = new Item[size];
    }

    /**
     * Prende la lunghezza della tupla.
     * @return lunghezza della tupla.
     */
    public int getLenght() {
        return tuple.length;
    }

    /**
     * Restituisce lo Item in posizione i.
     * @param i, indice.
     * @return Item in posizione i.
     */
    public Item get(int i) {
        return tuple[i];
    }

    /**
     * Memorizza lo Item c all'interno della tupla.
     * @param i, indice.
     * @param c, item.
     */
    public void add(int i, Item c) {
        tuple[i] = c;
    }

    /**
     * Determina la distanza tra la tupla riferita da obj e la tupla corrente. La distanza
     * è ottenuta come la somma delle distanze tra gli item in posizioni uguali nelle due tuple.
     * @param obj, oggetto di Tuple.
     * @return la somma delle distanze delle tuple.
     */
    public double getDistance(Tuple obj) {
        double sum = 0.0;
        for (int i = 0; i < obj.getLenght(); i++) {
            sum +=this.get(i).distance(obj.get(i));
        }
        return sum;
    }

    /**
     * Restituisce la media delle distanze tra la tupla corrente e quelle ottenibili dalla scansione degli elementi
     * del clusteredData.
     * @param data, oggetto di Data.
     * @param clusteredData
     * @return valore della distanza media.
     */
    public double avgDistance(Data data, HashSet <Integer> clusteredData) {
        double p = 0.0, sumD = 0.0;
        Tuple centroid;
        for (Integer CD : clusteredData) {
            centroid = data.getItemSet(CD);
            double d = getDistance(centroid);
            sumD += d;
        }
        p = sumD / clusteredData.size();
        return p;
    }




}

