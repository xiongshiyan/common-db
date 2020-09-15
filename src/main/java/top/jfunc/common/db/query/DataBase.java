package top.jfunc.common.db.query;

/**
 * @author xiongshiyan at 2020/9/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public enum DataBase {
    MYSQL(new MySqlSqlBuilder()),
    ORACLE(new OracleSqlBuilder()),
    POSTGRE(new PostgreSqlSqlBuilder()),
    SQLITE3(new Sqlite3SqlBuilder()),
    SQLSERVER(new SqlServerSqlBuilder());

    private SqlBuilder sqlBuilder;

    DataBase(SqlBuilder sqlBuilder){
        this.sqlBuilder = sqlBuilder;
    }

    public SqlBuilder getSqlBuilder() {
        return sqlBuilder;
    }
}
