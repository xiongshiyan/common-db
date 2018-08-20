package top.jfunc.common.db.bean;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Record
 * @author xiongshiyan
 */
public class Record implements Serializable {

    private static final long serialVersionUID = 905784513600884082L;
	
	private Map<String, Object> columns = new HashMap<>();

	public Record(){
    }

	public Record(Map<String, Object> columns){
        this.columns = columns;
    }

	public Map<String, Object> getColumns() {
		return columns;
	}
	public Record setColumns(Map<String, Object> columns) {
        this.getColumns().putAll(columns);
		return this;
	}
	
	public Record setColumns(Record record) {
		getColumns().putAll(record.getColumns());
		return this;
	}
	
	public Record remove(String column) {
		getColumns().remove(column);
		return this;
	}
	
	public Record remove(String... columns) {
		if (columns != null){
			for (String c : columns) {
				this.getColumns().remove(c);
			}
		}
		return this;
	}
	public Record removeNullValueColumns() {
		for (java.util.Iterator<Map.Entry<String, Object>> it = getColumns().entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Object> e = it.next();
			if (e.getValue() == null) {
				it.remove();
			}
		}
		return this;
	}
	
	/**
	 * Keep columns of this record and remove other columns.
	 * @param columns the column names of the record
	 */
	public Record keep(String... columns) {
		if (columns != null && columns.length > 0) {
			Map<String, Object> newColumns = new HashMap<String, Object>(columns.length);	// getConfig().containerFactory.getColumnsMap();
			for (String c : columns) {
                if (this.getColumns().containsKey(c)) {    // prevent put null value to the newColumns
                    newColumns.put(c, this.getColumns().get(c));
                }
            }
			
			this.getColumns().clear();
			this.getColumns().putAll(newColumns);
		}else {
            this.getColumns().clear();
        }
		return this;
	}
	
	/**
	 * Keep column of this record and remove other columns.
	 * @param column the column names of the record
	 */
	public Record keep(String column) {
		if (getColumns().containsKey(column)) {	// prevent put null value to the newColumns
			Object keepIt = getColumns().get(column);
			getColumns().clear();
			getColumns().put(column, keepIt);
		}else {
            getColumns().clear();
        }
		return this;
	}
	
	public Record clear() {
		getColumns().clear();
		return this;
	}
	
	public Record set(String column, Object value) {
		getColumns().put(column, value);
		return this;
	}
	
	public <T> T get(String column) {
		return (T)getColumns().get(column);
	}
	
	public <T> T get(String column, Object defaultValue) {
		Object result = getColumns().get(column);
		return (T)(result != null ? result : defaultValue);
	}
	
	/**
	 * Get column of mysql type: varchar, char, enum, set, text, tinytext, mediumtext, longtext
	 */
	public String getStr(String column) {
		return (String)getColumns().get(column);
	}
	
	/**
	 * Get column of mysql type: int, integer, tinyint(n) n > 1, smallint, mediumint
	 */
	public Integer getInt(String column) {
		return (Integer)getColumns().get(column);
	}
	
	/**
	 * Get column of mysql type: bigint
	 */
	public Long getLong(String column) {
		return (Long)getColumns().get(column);
	}

	/**
	 * Get column of mysql type: unsigned bigint
	 */
	public java.math.BigInteger getBigInteger(String column) {
		return (java.math.BigInteger)getColumns().get(column);
	}
	
	/**
	 * Get column of mysql type: date, year
	 */
	public java.util.Date getDate(String column) {
		return (java.util.Date)getColumns().get(column);
	}
	
	/**
	 * Get column of mysql type: time
	 */
	public java.sql.Time getTime(String column) {
		return (java.sql.Time)getColumns().get(column);
	}
	
	/**
	 * Get column of mysql type: timestamp, datetime
	 */
	public java.sql.Timestamp getTimestamp(String column) {
		return (java.sql.Timestamp)getColumns().get(column);
	}
	
	/**
	 * Get column of mysql type: real, double
	 */
	public Double getDouble(String column) {
		return (Double)getColumns().get(column);
	}
	
	/**
	 * Get column of mysql type: float
	 */
	public Float getFloat(String column) {
		return (Float)getColumns().get(column);
	}
	
	/**
	 * Get column of mysql type: bit, tinyint(1)
	 */
	public Boolean getBoolean(String column) {
		return (Boolean)getColumns().get(column);
	}
	
	/**
	 * Get column of mysql type: decimal, numeric
	 */
	public java.math.BigDecimal getBigDecimal(String column) {
		return (java.math.BigDecimal)getColumns().get(column);
	}
	
	/**
	 * Get column of mysql type: binary, varbinary, tinyblob, blob, mediumblob, longblob
	 * I have not finished the test.
	 */
	public byte[] getBytes(String column) {
		return (byte[])getColumns().get(column);
	}
	
	/**
	 * Get column of any type that extends from Number
	 */
	public Number getNumber(String column) {
		return (Number)getColumns().get(column);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(" {");
		boolean first = true;
		for (Map.Entry<String, Object> e : getColumns().entrySet()) {
			if (first){
				first = false;
			}else {
                sb.append(", ");
            }
			
			Object value = e.getValue();
			if (value != null) {
                value = value.toString();
            }
			sb.append(e.getKey()).append(":").append(value);
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Record)) {
            return false;
        }
		if (o == this) {
            return true;
        }
		return this.getColumns().equals(((Record)o).getColumns());
	}

	@Override
	public int hashCode() {
		return getColumns() == null ? 0 : getColumns().hashCode();
	}
	
	/**
	 * Return column names of this record.
	 */
	public String[] getColumnNames() {
		Set<String> attrNameSet = getColumns().keySet();
		return attrNameSet.toArray(new String[attrNameSet.size()]);
	}
	
	/**
	 * Return column values of this record.
	 */
	public Object[] getColumnValues() {
		java.util.Collection<Object> attrValueCollection = getColumns().values();
		return attrValueCollection.toArray(new Object[attrValueCollection.size()]);
	}
	
	/**
	 * Return json string of this record.
	 */
	public String toJson() {
		throw new UnsupportedOperationException("还未实现");
	}
}