package data;
import database.*;

/**
 * Classe che estende la classe Item e rappresenta la coppia attributo valore discrto.
 */
public class DiscreteItem extends Item{

    /**
     * Costruttore della classe DiscreteItem che inizializza il valore dello attributo e del valore discreto.
     */
    DiscreteItem(DiscreteAttribute attribute, String value){
        super(attribute, value);

    }

    /**
     * Restituisce la distanza calcolata tra due item.
     * @param a, oggetto.
     * @return 0 se i valori sono uguali e 1 se i valori sono diversi.
     */
    double distance (Object a){
        Item item = (Item) a;
        if (this.getValue().equals(item.getValue())){
            return 0;
        }else{
            return 1;
        }
    }


}
