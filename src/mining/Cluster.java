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

		
	Tuple getCentroid(){
		return centroid;
	}
	
	//return true if the tuple is changing cluster
	boolean addData(int id){
		return clusteredData.add(id);
		
	}
	
	//verifica se una transazione � clusterizzata nell'array corrente
	/*boolean contain(int id){
		return clusteredData.(id);
	}
	*/

	/*void removeTuple(int id){
		clusteredData.delete(id);
		
	}*/

    /**
     * Prende la dimensione di clusteredData.
     * @return dimensione di clusteredData.
     */
	int  getSize(){
		return clusteredData.size();
	}
	
	/*
    Object[] iterator(){
		return clusteredData.toArray();
	}*/

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



	
	public String toString(){
		String str="Centroid=(";
		for(int i=0;i<centroid.getLenght();i++)
			str += centroid.get(i);
		str+=")";
		return str;
		
	}
	


	
	public String toString(Data data){
		String str="Centroid=(";
		for(int i=0;i<centroid.getLenght();i++)
			str+=centroid.get(i)+ " ";
		str+=")\nExamples:\n";
		Set<Integer> hSet = clusteredData;
		for(Integer hSets : hSet){
			str+="[";
			//for(int j=0;j<data.getNumberOfAttributes();j++)
				str+=data.getAttributeValue(hSets) + " ";
			str+="] dist="+getCentroid().getDistance(data.getItemSet(hSets))+"\n";
			
		}
		str+="\nAvgDistance="+getCentroid().avgDistance(data, (HashSet <Integer>) hSet);
		return str;
		
	}



}
