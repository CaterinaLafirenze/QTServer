package data;
import database.*;
import java.lang.Math;

/**
 * Classe concreta che estende la classe Item e modella una coppia Attrinuto continuo e valore numerico.
 */
public class ContinuousItem extends Item{

    /**
     * Costruttore che estende la classe Item e modella la coppia attributo continuo e valore numerico.
     * @param attribute
     * @param value
     */

    ContinuousItem(Attribute attribute, Double value){
        super(attribute, value);
    }

    /**
     * Calcola la distanza delle tuple.
     * @param a
     * @return il valore assoluto della distanza.
     */

    double distance (Object  a){

        Item item = (Item) a;

        ContinuousAttribute attributeItem = (ContinuousAttribute) item.getAttribute();
        double sv = attributeItem.getScaledValue((double) item.getValue());
        ContinuousAttribute attributeThis = (ContinuousAttribute) this.getAttribute();
        double dv = attributeThis.getScaledValue((double) this.getValue());
        return Math.abs(dv - sv);

    }


}
