package mining;
import data.*;
//import utility.*;
import java.io.Serializable;
import java.util.*;
import java.lang.*;

/**
 * Classe che modella cluster.
 */
class Cluster implements Iterable<Integer>, Comparable <Cluster>, Serializable {

	private Tuple centroid;

	private Set<Integer> clusteredData =  new HashSet<>();

    /**
     * Costruttore della classe Cluster, inizializza il centroide e il clusteredData.
     */
	Cluster(){
		this.centroid = centroid;
        this.clusteredData = clusteredData;
	}

    /**
     * Costruttore della classe Cluster che prende come parametro una Tupla e inizializza centroide e clusteredData.
     * @param centroid rappresenta una tupla.
     */
	Cluster(Tuple centroid){
		this.centroid=centroid;
		clusteredData=new HashSet<>();
		
	}

    /**
     * Prende il centroide dal Cluster.
     * @return il centroide.
     */
	Tuple getCentroid(){
		return centroid;
	}

    /**
     * Verifica se la tupla che viene aggiunta modifica il cluster
     * @param id identificativo della tupla.
     * @return se la tupla modifica il cluster restituisce true, altrimenti false.
     */
    boolean addData(int id){
		return clusteredData.add(id);
		
	}

    /**
     * Prende la dimensione di clusteredData.
     * @return dimensione di clusteredData.
     */
	int  getSize(){
		return clusteredData.size();
	}


    /**
     * iteratore di clusteredData.
     * @return l'iteratore.
     */
    public Iterator <Integer> iterator(){
        return clusteredData.iterator();
    }

    /**
     * Compara gli elementi del cluster.
     * @param o cluster.
     * @return restituisce 1 se la dimensione del cluster corrente è maggiore
     * di quello indicato dal parametro altrimenti -1.
     */
    public int compareTo(Cluster o){

            if(this.getSize()>o.getSize()){
                return 1;
            } else if ( this.getSize()<o.getSize()) {
                return -1;
            }
            return this.getCentroid().toString().compareTo(o.getCentroid().toString());
    }


    /**
     * Crea la stringa dei centroidi con le tuple appartenenti al centroide.
     * @return la stringa.
     */
	public String toString(){
		String str="Centroid=(";
		for(int i=0;i<centroid.getLenght();i++)
			str += centroid.get(i);
		str+=")";
		return str;
		
	}


    /**
     * Crea la stringa con i centroidi e i rispettivi esempi, distanze e alla fine la distanza media.
     * @param data oggetto di Data.
     * @return la stringa.
     */
	public String toString(Data data){
		String str="Centroid=(";
		for(int i=0;i<centroid.getLenght();i++)
			str+=centroid.get(i)+ " ";
		str+=")\nExamples:\n";
		Set<Integer> hSet = clusteredData;
		for(Integer hSets : hSet){
			str+="[";
            str+=data.getAttributeValue(hSets) + " ";
			str+="] dist="+getCentroid().getDistance(data.getItemSet(hSets))+"\n";
			
		}
		str+="\nAvgDistance="+getCentroid().avgDistance(data, (HashSet <Integer>) hSet);
		return str;
		
	}



}
