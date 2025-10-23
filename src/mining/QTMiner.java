package mining;

import data.*;

import java.io.*;
import java.util.Iterator;
import java.util.*;

/**
 * Classe che include l'implementazione dello algoritmo QT utilizzato per calcolare i centroidi
 * e ricercare il più poloso.
 */
public class QTMiner implements Serializable {

    private ClusterSet C;
    private double radius;

    public QTMiner(double radius){
        this.C =new ClusterSet();
        this.radius = radius;
    }
    /*
    * Scritto come carica, ricontrollare
    * */
    public QTMiner (String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))){
            this.C = (ClusterSet) ois.readObject();
        }catch(IOException|ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     * Apre il file identificato da fileName e salva lo oggetto riferito dal ClusterSet in tale file.
     * @param fileName
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void salva(String fileName) throws FileNotFoundException, IOException{
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))){
            oos.writeObject(this.C);
        }catch(IOException e){
            e.printStackTrace();
        }
    }




    public ClusterSet getC() {
        return C;
    }

    /**
     * Esegue lo algoritmo QT, costruisce un clsuter per ciascuna tupla non ancora clusterizzata, include nel cluster
     * i punti che ricadono nel vicinato sferico delle tuple aventi raggio radius.
     * Salva il candidato cluster più popoloso e rimuove tutti i punti di tale cluster dallo elenco
     * delle tuple ancora da clsuterizzare.
     * Ritorna al primo passo finche ci sono ancota tuple da assegnare ad un cluster.
     * @param data
     * @return Numero di cluster scoperti.
     * @throws ClusteringRadiusException
     */
    public int compute(Data data) throws ClusteringRadiusException{

        int numclusters=0;
        boolean[] isClustered =new boolean[data.getNumberOfExamples()];
        for(int i = 0; i<isClustered.length; i++)
            isClustered[i]=false;
        int countClustered=0;
        while(countClustered!=data.getNumberOfExamples())
        {
            //Ricerca cluster più popoloso
            Cluster c=buildCandidateCluster(data, isClustered);
            if(c.getSize() == 14){
                throw new ClusteringRadiusException("14 tuples in one cluster!");
            }
            C.add(c);
            numclusters++;
            //Rimuovo le tuple clusterizzate da dataset
            Iterator<Integer> clusteredTupleId =c.iterator();
            while(clusteredTupleId.hasNext()){
                isClustered[clusteredTupleId.next()]=true;
            }
            countClustered+=c.getSize();
        }
        return numclusters;
    }

    public Cluster buildCandidateCluster(Data data, boolean[] isClustered){

        Cluster candidate=new Cluster();
        int countClusterset = 0;
        for (int i =0; i<data.getNumberOfExamples(); i++){
            if(!isClustered[i]){
                Cluster cl = new Cluster(data.getItemSet(i));
                for(int j=0; j<data.getNumberOfExamples(); j++) {
                    if(!isClustered[j]) {
                        double d=data.getItemSet(i).getDistance(data.getItemSet(j));
                        if(d <= radius){
                            cl.addData(j);
                        }
                    }
                }
                if(cl.getSize() > countClusterset){
                    countClusterset = cl.getSize();
                    candidate=cl;
                }

            }
        }
        return candidate;
        /*
        double max = 0;
        int pos = 0;
        for(int i=0; i<countClusterset; i++){
             if(max < CS.get(i).getSize()) {
                 max = CS.get(i).getSize();
                 pos = i;
             }
        } return CS.get(pos);

         */
        /*
        Cluster max = new Cluster();
        for(Cluster c: CS){
             if(max.compareTo(c)>0)
                 max = c;
        } return max;*/

    }

}
