package data;

import java.util.*;
//import java.util.TreeSet;

/**
 * Classe che estende la classe Attribute e rappresenta un attributo discreto (categorico).
 */
public class DiscreteAttribute extends Attribute implements Iterable<String>{


    private TreeSet<String> values;

    public Iterator<String> iterator(){
        return values.iterator();
    }

    /**
     * Costruttore che inizializza i valori di name, index e values.
     * @param name
     * @param index
     * @param values
     */
    public DiscreteAttribute(String name, int index, TreeSet<String> values) {

        super(name,index);
        this.values=values;
    }

    /**
     * Metodo che restituisce la dimensione di values.
     * @return
     */

    int getNumberOfDistinctValues() {
        return values.size();
    }

    /**
     * Metodo che restituisce il valore nella posizione i.
     * @param i
     * @return
     */
    /*String getValue(int i){
        return values[i];
    }*/


}