package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Classe che modella lo schema di una tabella nel database relazionale.
 */
public class TableSchema {
	DbAccess db;


	public class Column{
		private String name;
		private String type;

        /**
         * Costruttore dellla classe Column. Inizializza il nome della colonna e il tipo di elementi che contiene.
         * @param name nome della colonna.
         * @param type tipo degli elementi.
         */
		public Column(String name,String type){
			this.name=name;
			this.type=type;
		}

        /**
         * Prende il nome della colonna.
         * @return il nome.
         */
		public String getColumnName(){
			return name;
		}

        /**
         * Verifica che il valore del tipo sia un numero.
         * @return
         */
		public boolean isNumber(){
			return type.equals("number");
		}

        /**
         * Crea la stringa con il nome della tabella e il tipo degli elementi.
         * @return la stringa.
         */
		public String toString(){
			return name+":"+type;
		}
	}
	public List<Column> tableSchema=new ArrayList<Column>();

    /**
     * Costruttore che modella lo schema di una tabella nel database.
     * @param db database.
     * @param tableName nome della tabella.
     * @throws SQLException
     */
	public TableSchema(DbAccess db, String tableName) throws SQLException{
		this.db=db;
		HashMap<String,String> mapSQL_JAVATypes=new HashMap<String, String>();
		//http://java.sun.com/j2se/1.3/docs/guide/jdbc/getstart/mapping.html
		mapSQL_JAVATypes.put("CHAR","string");
		mapSQL_JAVATypes.put("VARCHAR","string");
		mapSQL_JAVATypes.put("LONGVARCHAR","string");
		mapSQL_JAVATypes.put("BIT","string");
		mapSQL_JAVATypes.put("SHORT","number");
		mapSQL_JAVATypes.put("INT","number");
		mapSQL_JAVATypes.put("LONG","number");
		mapSQL_JAVATypes.put("FLOAT","number");
		mapSQL_JAVATypes.put("DOUBLE","number");
		
		
	
		 Connection con=db.getConnection();
		 DatabaseMetaData meta = con.getMetaData();
	     ResultSet res = meta.getColumns(null, null, tableName, null);
		   
	     while (res.next()) {
	         
	         if(mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME")))
	        		 tableSchema.add(new Column(
	        				 res.getString("COLUMN_NAME"),
	        				 mapSQL_JAVATypes.get(res.getString("TYPE_NAME")))
	        				 );
	
	         
	         
	      }
	      res.close();
	
	
	    
	    }

    /**
     * Prende il numero di attributo della tabella.
     * @return dimensione della tabella.
     */
		public int getNumberOfAttributes(){
			return tableSchema.size();
		}

    /**
     * Prende la colonna associata allo indice della tabella.
     * @param index indice.
     * @return la colonna.
     */
    public Column getColumn(int index){
			return tableSchema.get(index);
		}

		
	}

		     


