package data;
import java.sql.SQLException;
import java.util.*;
import java.util.TreeSet;
import java.util.List;
import database.*;

import java.io.IOException;
/*import java.util.Arrays;
import java.util.LinkedList;*/

/**
 * Classe concreta utilizzata per modellare lo insieme delle transazioni indicati come tuple.
 */
public class Data {

    private List<Example> data;
    private int numberOfExamples;
    //private Attribute attributeSet;
    private List<Attribute> attributeSet;


    /**
     * Costruttore che si occupa di caricare i dati di addestramento da una tabella della base di dati.
     * @param table, utilizza il nome della tabella.
     * @throws EmptyDatasetException
     */
    public Data(String table) throws EmptyDatasetException {

        //data

        data = new ArrayList<Example>();
        attributeSet = new LinkedList<>();

        try {
            //Crea il database e accede ai dati inizializzando la connessione
            DbAccess db = new DbAccess();
            db.initConnection();
            //costruisce la tabella ricavata dal database
            TableData td = new TableData(db);
            data = td.getDistinctTransazioni(table);
            //crea lo schema da modellare
            TableSchema ts = new TableSchema(db, table);
            List<TableSchema.Column> schema = ts.tableSchema;
            for (int i = 0; i < schema.size(); i++) {
                //controlla se l'elemento della tupla è un numero (temperatura)
                if (schema.get(i).isNumber()) {
                    //prende i valori di minimo e massimo e aggiunge i valori nell'attributeSet
                    Object minVal = td.getAggregateColumnValue(table, schema.get(i), QUERY_TYPE.MIN);
                    Object maxVal = td.getAggregateColumnValue(table, schema.get(i), QUERY_TYPE.MAX);
                    double min = ((Number) minVal).doubleValue();
                    double max = ((Number) maxVal).doubleValue();
                    attributeSet.add(new ContinuousAttribute(schema.get(i).getColumnName(), i, min, max));
                } else {
                    //prende i valori testuali e li assegna nell'attributeSet
                    Set<Object> distinctValues = td.getDistinctColumnValues(table, schema.get(i));
                    TreeSet<String> discreteValues = new TreeSet<>();
                    for (Object v : distinctValues) {
                        discreteValues.add(v.toString());
                    }

                    attributeSet.add(new DiscreteAttribute(schema.get(i).getColumnName(), i, discreteValues));
                }

            }
            //chiude la connessione con il database
            db.closeConnection();
        } catch (DatabaseConnectionException | SQLException | EmptySetException | NoValueException e) {
            System.out.println(e.getMessage());
        }
        numberOfExamples = 14;

    }

    /**
     * Prende il numero di esempi.
     * @return numero di esempi
     */
    public int getNumberOfExamples() {
        return numberOfExamples;
    }

    public int getNumberOfAttributes() {
        return attributeSet.size();
    }

    /**
     * Prende l'indice corrispondente allo attributo e restituisce il valore di data ad esso associato.
     * @param attributeIndex, indice dello attributo.
     * @return il valore di data in posizione attributeIndex.
     */
    public Object getAttributeValue(int attributeIndex) {
        return data.get(attributeIndex);
    }

    /*Attribute getAttribute(int index){
        return attributeSet[index];
    }

    Attribute[] getAttributeSchema(){
        return attributeSet;
    }*/

    /**
     * Crea e restituisce uno oggetto di Tuple che distingue tra attributo discreto o continuo.
     * @param index, indice dello attributeSet dal quale prendere i valori.
     * @return la tupla.
     */
    public Tuple getItemSet(int index) {
        Tuple tuple = new Tuple(attributeSet.size());
        for (int i = 0; i < attributeSet.size(); i++) {
            if (attributeSet.get(i) instanceof DiscreteAttribute) {
                tuple.add(i, new DiscreteItem((DiscreteAttribute) attributeSet.get(i), (String) data.get(index).get(i)));
            } else if (attributeSet.get(i) instanceof ContinuousAttribute) {
                //String value = String.valueOf(data.get(i));
                tuple.add(i, new ContinuousItem((ContinuousAttribute) attributeSet.get(i), (Double) data.get(index).get(i)));
            }
        }
        return tuple;
    }

    /**
     * Crea una stringa contenente tutte le tuple di Data.
     * @return stringa.
     */
    public String toString() {

        String str="";
        for (Example example : data) {
            str+= example + "\n";
        }
       return str;
    }

    /**
     * Crea e stampa un oggetto di Data contenente tutti i valori specificati dalla tabella.
     * @param args
     */
    public static void main(String args[]){
        Data trainingSet = null;

        try {
            trainingSet = new Data("playtennis");
        }catch(EmptyDatasetException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(trainingSet);

    }

}