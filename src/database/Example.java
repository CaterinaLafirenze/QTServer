package database;

import java.util.ArrayList;
import java.util.List;


/**
 * Classe che modella una transazione letta dalla base di dati.
 */
public class Example implements Comparable<Example>{

	private List<Object> example=new ArrayList<Object>();

    /**
     * Aggiunge un elemento agli esempi.
     * @param o, oggetto aggiunto
     */
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

    /**
     * Compara gli elementi della lista di esempi.
     * @param ex, lista di esempi.
     * @return se gli elementi non sono uguali restituisce 1, altrimenti 0
     */
	public int compareTo(Example ex) {
		
		int i=0;
		for(Object o:ex.example){
			if(!o.equals(this.example.get(i)))
				return ((Comparable)o).compareTo(example.get(i));
			i++;
		}
		return 0;
	}

    /**
     * Crea la stringa di esempi.
     * @return la stringa.
     */

	public String toString(){
		String str="";
		for(Object o:example)
			str+=o.toString()+ " ";
		return str;
	}
	
}