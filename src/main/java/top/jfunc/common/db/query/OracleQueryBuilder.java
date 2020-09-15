package top.jfunc.common.db.query;

/**
 * 用于oracle
 * @author xiongshiyan
 */
public class OracleQueryBuilder extends AbstractQueryBuilder<OracleQueryBuilder> {
    public OracleQueryBuilder() {
        super(DataBase.ORACLE.getSqlBuilder());
    }
    public OracleQueryBuilder(String select, String tableName, String alias){
        super(DataBase.ORACLE.getSqlBuilder(), select, tableName, alias);
    }
    public OracleQueryBuilder(String select, String... froms){
        super(DataBase.ORACLE.getSqlBuilder(), select, froms);
    }
}
