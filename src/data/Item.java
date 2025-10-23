package data;

import java.io.Serializable;

/**
 * Classe astratta che modella un generico item formato dalla coppia attributo-valore.
 */
abstract class Item implements Serializable {

    private Attribute attribute;
    private Object value;

    /**
     * Costruttore della classe astratta Item, che inizializza lo attributo e il valore.
     * @param attribute, attributo.
     * @param value, valore.
     */
    Item(Attribute attribute, Object value) {
        this.attribute = attribute;
        this.value = value;
    }

    /**
     * Prende l'attributo.
     * @return l'attributo.
     */
    Attribute getAttribute() {
        return attribute;
    }

    /**
     * Prende il valore.
     * @return il valore.
     */
    Object getValue() {
        return value;
    }
    public String toString() {
        return value.toString();
    }

    /**
     * Metodo astratto che viene implementato nei casi discreti e continuo.
     * @param a, oggetto.
     */
    abstract double distance (Object a);




}
