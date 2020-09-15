package top.jfunc.common.db.query;

/**
 * postgreSQL的模式
 *   SELECT .. FROM .. (LEFT|RIGHT|INNER) JOIN .. ON .. WHERE .... GROUP BY .. HAVING .. ORDER BY ... LIMIT size offset o
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class PostgreSqlQueryBuilder extends AbstractQueryBuilder<PostgreSqlQueryBuilder> {
    public PostgreSqlQueryBuilder() {
        super(DataBase.POSTGRE.getSqlBuilder());
    }
    public PostgreSqlQueryBuilder(String select, String tableName, String alias){
        super(DataBase.POSTGRE.getSqlBuilder(),select, tableName, alias);
    }
    public PostgreSqlQueryBuilder(String select, String... froms){
        super(DataBase.POSTGRE.getSqlBuilder(),select, froms);
    }
}
