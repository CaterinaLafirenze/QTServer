package database;

import java.util.ArrayList;
import java.util.List;


/**
 * Classe che modella una transazione letta dalla base di dati.
 */
public class Example implements Comparable<Example>{

	private List<Object> example=new ArrayList<Object>();

	public void add(Object o){
		example.add(o);
	}

    /**
     * Restituisce l'esempio della lista che si trova in posizione i.
     * @param i indice.
     * @return esempio in posizione i.
     */
	public Object get(int i){
		return example.get(i);
	}
	public int compareTo(Example ex) {
		
		int i=0;
		for(Object o:ex.example){
			if(!o.equals(this.example.get(i)))
				return ((Comparable)o).compareTo(example.get(i));
			i++;
		}
		return 0;
	}
	public String toString(){
		String str="";
		for(Object o:example)
			str+=o.toString()+ " ";
		return str;
	}
	
}