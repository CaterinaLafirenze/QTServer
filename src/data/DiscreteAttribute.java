package data;

import java.util.*;
//import java.util.TreeSet;

/**
 * Classe che estende la classe Attribute e rappresenta un attributo discreto (categorico).
 */
public class DiscreteAttribute extends Attribute implements Iterable<String>{

    private TreeSet<String> values;

    /**
     * Metodo iteratore sui valori degli attributi discreti.
     */
    public Iterator<String> iterator(){
        return values.iterator();
    }

    /**
     * Costruttore che inizializza i valori di name, index e values.
     * @param name nome dello attributo
     * @param index indice dello attributo
     * @param values valore dello attributo
     */
    public DiscreteAttribute(String name, int index, TreeSet<String> values) {

        super(name,index);
        this.values=values;
    }


    int getNumberOfDistinctValues() {
        return values.size();
    }

    /*String getValue(int i){
        return values[i];
    }*/


}