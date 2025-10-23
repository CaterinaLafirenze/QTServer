package data;
import java.sql.SQLException;
import java.util.*;
import java.util.TreeSet;
import java.util.List;
import database.*;

import java.io.IOException;
/*import java.util.Arrays;
import java.util.LinkedList;*/


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
            TableData td = new TableData(db);
            data = td.getDistinctTransazioni(table);
            TableSchema ts = new TableSchema(db,table);
            List<TableSchema.Column> schema = ts.tableSchema;
            for(int i =0; i<schema.size(); i++){
                if(schema.get(i).isNumber()){
                    Object minVal = td.getAggregateColumnValue(table, schema.get(i), QUERY_TYPE.MIN );
                    Object maxVal = td.getAggregateColumnValue(table, schema.get(i), QUERY_TYPE.MAX );
                    double min =((Number)minVal).doubleValue();
                    double max =((Number)maxVal).doubleValue();
                    attributeSet.add(new ContinuousAttribute(schema.get(i).getColumnName(), i, min, max));
                }else {
                    //cambiare nomi NO
                    Set<Object> distinctValues = td.getDistinctColumnValues(table, schema.get(i));
                    TreeSet<String> discreteValues = new TreeSet<>();
                    for(Object val : distinctValues){
                        discreteValues.add(val.toString());
                    }

                    attributeSet.add(new DiscreteAttribute(schema.get(i).getColumnName(), i, discreteValues));
                }

            }
            db.closeConnection();
        } catch (DatabaseConnectionException | SQLException | EmptySetException | NoValueException e) {
            System.out.println(e.getMessage());
        }

        /*
        data[0][0] = "Sunny";
        data[0][1] = 30.3;
        data[0][2] = "High";
        data[0][3] = "Weak";
        data[0][4] = "No";
        data[1][0] = "Sunny";
        data[1][1] = 30.3;
        data[1][2] = "High";
        data[1][3] = "Strong";
        data[1][4] = "No";
        data[2][0] = "Overcast";
        data[2][1] = 30.0;
        data[2][2] = "High";
        data[2][3] = "Weak";
        data[2][4] = "Yes";
        data[3][0] = "Rain";
        data[3][1] = 13.0;
        data[3][2] = "High";
        data[3][3] = "Weak";
        data[3][4] = "Yes";
        data[4][0] = "Rain";
        data[4][1] = 0.0;
        data[4][2] = "Normal";
        data[4][3] = "Weak";
        data[4][4] = "Yes";
        data[5][0] = "Rain";
        data[5][1] = 0.0;
        data[5][2] = "Normal";
        data[5][3] = "Strong";
        data[5][4] = "No";
        data[6][0] = "Overcast";
        data[6][1] = 0.1;
        data[6][2] = "Normal";
        data[6][3] = "Strong";
        data[6][4] = "Yes";
        data[7][0] = "Sunny";
        data[7][1] = 13.0;
        data[7][2] = "High";
        data[7][3] = "Weak";
        data[7][4] = "No";
        data[8][0] = "Sunny";
        data[8][1] = 0.1;
        data[8][2] = "Normal";
        data[8][3] = "Weak";
        data[8][4] = "Yes";
        data[9][0] = "Rain";
        data[9][1] = 12.0;
        data[9][2] = "Normal";
        data[9][3] = "Weak";
        data[9][4] = "Yes";
        data[10][0] = "Sunny";
        data[10][1] = 12.5;
        data[10][2] = "Normal";
        data[10][3] = "Strong";
        data[10][4] = "Yes";
        data[11][0] = "Overcast";
        data[11][1] = 12.5;
        data[11][2] = "High";
        data[11][3] = "Strong";
        data[11][4] = "Yes";
        data[12][0] = "Overcast";
        data[12][1] = 29.21;
        data[12][2] = "Normal";
        data[12][3] = "Weak";
        data[12][4] = "Yes";
        data[13][0] = "Rain";
        data[13][1] = 12.5;
        data[13][2] = "High";
        data[13][3] = "Strong";
        data[13][4] = "No";
        */


        // numberOfExamples

        numberOfExamples = 14;


        //explanatory Set



        // TO DO : avvalorare ciascune elemento di attributeSet con un oggetto della classe DiscreteAttribute che modella il corrispondente attributo (e.g. outlook, temperature,etc)
        // nel seguito si fornisce l'esempio per outlook
        /*
        TreeSet<String> outLookValues = new TreeSet<>();
        outLookValues.add("overcast");
        outLookValues.add("rain");
        outLookValues.add("sunny");
        attributeSet.add(new DiscreteAttribute("Outlook", 0, outLookValues));

        //TreeSet<String> temperatureValues = new TreeSet<>();
        /*temperatureValues.add("hot");
        temperatureValues.add("mild");
        temperatureValues.add("cool");
        attributeSet.add(new ContinuousAttribute("Temperature", 1, 3.2, 38.7));

        TreeSet<String> humidityValues = new TreeSet<>();
        humidityValues.add("high");
        humidityValues.add("normal");
        attributeSet.add(new DiscreteAttribute("Humidity", 2, humidityValues));

        TreeSet<String> windValues = new TreeSet<>();
        windValues.add("weak");
        windValues.add("strong");
        attributeSet.add(3, new DiscreteAttribute("Wind", 3, windValues));

        TreeSet<String> playTennisValues = new TreeSet<>();
        playTennisValues.add("no");
        playTennisValues.add("yes");
        attributeSet.add(4, new DiscreteAttribute("PlayTennis", 4, playTennisValues));
        // similmente per gli altri attributi

        try{
            DbAccess db = new DbAccess();
            db.initConnection();
            TableData td = new TableData(db);
            TableSchema ts = new TableSchema(db,table);
            List<TableSchema.Column> schema = ts.tableSchema;
            TableSchema.Column outLookValues = ts.new Column("outLookValues", "VARCHAR");
            TableSchema.Column temperatureValues = ts.new Column("temperatureValues", "DOUBLE");
            TableSchema.Column humidityValues = ts.new Column("humidityValues", "VARCHAR");
            TableSchema.Column windValues = ts.new Column("windValues", "VARCHAR");
            TableSchema.Column playTennisValues = ts.new Column("playTennis", "VARCHAR");
            attributeSet.add((Attribute) td.getDistinctColumnValues("playtennis", outLookValues));
            attributeSet.add((Attribute) td.getAggregateColumnValue("playtennis", temperatureValues, QUERY_TYPE));
            attributeSet.add((Attribute) td.getDistinctColumnValues("playtennis", humidityValues));
            attributeSet.add((Attribute) td.getDistinctColumnValues("playtennis", windValues));
            attributeSet.add((Attribute)td.getDistinctColumnValues("playtennis",  playTennisValues));
        }catch(DatabaseConnectionException | SQLException e) {
            System.out.println(e.getMessage());
        }*/

        /*
        ho una classe dentro un'altra classe, chiamiamole classe1 e classe 2. classe 2, che si trova all'interno
        di classe1, ha il costruttore dentro classe 2. come chiamo il costruttore di classe 2 in un'altra classe,
        chiamata classe 3 che si trova da un'altra parte?
        */


    }

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


    public String toString() {

        /*String [] attribute;
        attribute = new String[getNumberOfAttributes()];*/
        /*for(int i=0;i<getNumberOfAttributes();i++){
            System.out.print(getAttribute(i)+",");
        }
        System.out.println();*/
        //int i = 0;
        String str="";
        for (Example example : data) {
            /*System.out.print(data.lastIndexOf(example));
            String string = data.set(i, example).toString();
            data[i][j]= getAttributeValue(i,j).toString();
            System.out.println(i+1 + ": " + string);
            i++;*/
            str+= example + "\n";
            //System.out.println(str);
        }
        //System.out.println();

        //https://www.youtube.com/watch?v=SufH1q1PnAw
        //https://www.youtube.com/watch?v=Kzs4dbPMHDs
        //No

        return str;
    }


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