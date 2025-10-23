package data;
import database.*;
public class DiscreteItem extends Item{

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
