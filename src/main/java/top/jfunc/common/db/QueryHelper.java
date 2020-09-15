package top.jfunc.common.db;

import top.jfunc.common.db.query.AbstractQueryBuilder;
import top.jfunc.common.db.query.DataBase;
import top.jfunc.common.db.query.QueryBuilder;


/**
 * 全能
 * @see top.jfunc.common.db.query.MysqlQueryBuilder
 * @see top.jfunc.common.db.query.OracleQueryBuilder
 * @see top.jfunc.common.db.query.PostgreSqlQueryBuilder
 * @see top.jfunc.common.db.query.Sqlite3QueryBuilder
 * @see top.jfunc.common.db.query.SqlServerQueryBuilder
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class QueryHelper extends AbstractQueryBuilder<QueryHelper> implements QueryBuilder {

    public QueryHelper(){this(DataBase.MYSQL);}
    public QueryHelper(String select, String tableName, String alias) {
        this(DataBase.MYSQL, select, tableName, alias);
    }
    public QueryHelper(String select, String... froms) {
        this(DataBase.MYSQL, select, froms);
    }



    public QueryHelper(DataBase dataBase){super(dataBase.getSqlBuilder());}
    public QueryHelper(DataBase dataBase,String select, String tableName, String alias) {
        super(dataBase.getSqlBuilder(), select, tableName, alias);
    }
    public QueryHelper(DataBase dataBase, String select, String... froms) {
        super(dataBase.getSqlBuilder(), select, froms);
    }
}