package top.jfunc.common.db.query;

/**
 * Sqlite3的模式
 *   SELECT .. FROM .. (LEFT|RIGHT|INNER) JOIN .. ON .. WHERE .... GROUP BY .. HAVING .. ORDER BY ... LIMIT offset , size
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class Sqlite3QueryBuilder extends AbstractQueryBuilder<Sqlite3QueryBuilder> {
    public Sqlite3QueryBuilder() {
        super(DataBase.SQLITE3.getSqlBuilder());
    }
    public Sqlite3QueryBuilder(String select, String tableName, String alias){
        super(DataBase.SQLITE3.getSqlBuilder(), select, tableName, alias);
    }
    public Sqlite3QueryBuilder(String select, String... froms){
        super(DataBase.SQLITE3.getSqlBuilder(), select, froms);
    }
}
