package data;
import java.io.Serializable;
import java.util.*;
import java.util.HashSet;

public class Tuple implements Serializable {

    private Item[] tuple;

    Tuple(int size) {
        //vedi bene dopo che se qualcosa va tolto
        tuple = new Item[size];
    }


    public int getLenght() {
        return tuple.length;
    }

    public Item get(int i) {
        return tuple[i];
    }

    public void add(int i, Item c) {
        tuple[i] = c;
    }

   /* double distance(Object a) {
        if (this.getValue().equals(a)) {
            return 0;
        } else {
            return 1;
        }
    }*/

    public double getDistance(Tuple obj) {
        double sum = 0.0;
        for (int i = 0; i < obj.getLenght(); i++) {
            //if(this.getValue().equals(obj.get(i))){}
            //sum = sum + this.distance(obj);
            sum +=this.get(i).distance(obj.get(i));
        }
        return sum;
    }

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

