package data;

/**
 * Classe che estende la classe Attribute e modella un attributo continuo.
 */
public class ContinuousAttribute extends Attribute{



    private double max;
    private double min;

    /**
     * Invoca il costruttore della classe madre e inizializza i membri aggiunti per estensione.
     */


    ContinuousAttribute(String name, int index, double min, double max){

        super(name, index);

        this.max = max;
        this.min = min;
    }


    /**
     * Metodo che restituisce il massimo valore dell'intervallo.
     * @return
     */
    public double getMax(){return max;}

    /**
     * Metodo che restituisce il minimo valore dell'intervallo.
     * @return
     */
    public double getMin(){return min;}


    /*public double getScaledValue(double v){
        double v1 =0.0;

        if(v>=getMin() && v<=getMax()){
            v1=(v-getMin())/(getMax()-getMin());
        }

        return v1;
    }*/

    //CHE SCHIFO
    /**
     * Calcola e restituisce il valore scalato del parametro passato in input.
     * @param v
     * @return il valore scalato.
     */
    public double getScaledValue(double v) {
        if (v == min) {
            return 0.0;
        } else {
            return (v - min) / (getMax() - getMin());
        }

    }





}
