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

    /**
     * Costruttore della classe QTMiner che crea l'oggetto riferito dal ClusterSet e inizializza il raggio
     * @param radius, raggio del ClusterSet.
     */
    public QTMiner(double radius){
        this.C =new ClusterSet();
        this.radius = radius;
    }

    /**
     * Apre il file identificato da filename, legge lo oggetto ivi memorizzato e lo assegna al ClusterSet.
     * @param fileName, nome del file da caricare.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
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


    /**
     * Prende il ClusterSet.
     * @return il ClusterSet.
     */
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

    /**
     * Costruisce un cluster per ciascuna tupla di data non ancora clusterizzata in un cluster del ClusterSet.
     * @param data, insieme di tuple da raggruppare in cluster.
     * @param isClustered, informazione booleana sullo stato di clusterizzazione della tupla.
     * @return il cluster caditato più popoloso
     */
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
    }

}
