package top.jfunc.common.db;

import top.jfunc.common.db.query.MysqlQueryBuilder;


/**
 * MYSQL的模式
 *   SELECT .. FROM .. (LEFT|RIGHT|INNER) JOIN .. ON .. WHERE .... GROUP BY .. HAVING .. ORDER BY ... LIMIT offset , size
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class QueryHelper extends MysqlQueryBuilder {
    public QueryHelper(String select, String tableName, String alias) {
        super(select, tableName, alias);
    }
    public QueryHelper(String select, String... froms) {
        super(select, froms);
    }
}