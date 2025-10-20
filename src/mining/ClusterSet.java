package mining;
import data.*;

import java.io.Serializable;
import java.util.*;
import java.lang.Iterable;
public class ClusterSet implements Iterable<Cluster>, Serializable {

    private Set<Cluster> C;

    ClusterSet() {
        C = new TreeSet<>(); //ricordarsi di controllare

    }

    public Iterator<Cluster> iterator(){
        return C.iterator();
    }


    void add (Cluster c){
        C.add(c);
    }

    /*Cluster get(int i){
        return C[i];
    }*/

    //ricontrollare
    public String toString(){
        String str = "";
        for(Cluster c : C) {
            str += c + "\n"; //si può usare anche c. onesto iterator() è ancora un mistero per me
        }
        return str;
    }

    public String toString(Data data){
        String str = "";
        int i = 1;
        for(Cluster c : C) {
            //if(null != C.iterator()){
                str+= i + " : " + c.toString(data) +"\n";//abbiamo tolto la i...
                i++;
           // }

        }
        return str;
    }

    /*
    public String toString(Data data){
        String str = "";
        for(int i=0; i<C.size(); i++){
            if(C[i]!=null){
                str+=i+": "+C[i].toString(data)+"\n";
            }
        }
        return str;
    }*/




}
