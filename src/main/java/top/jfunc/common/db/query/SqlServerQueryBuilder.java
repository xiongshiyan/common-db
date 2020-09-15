package top.jfunc.common.db.query;

/**
 * sql server的模式
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SqlServerQueryBuilder extends AbstractQueryBuilder<SqlServerQueryBuilder> {
    public SqlServerQueryBuilder() {
        super(DataBase.SQLSERVER.getSqlBuilder());
    }
    public SqlServerQueryBuilder(String select, String tableName, String alias){
        super(DataBase.SQLSERVER.getSqlBuilder(), select, tableName, alias);
    }
    public SqlServerQueryBuilder(String select, String... froms){
        super(DataBase.SQLSERVER.getSqlBuilder(), select, froms);
    }
}
