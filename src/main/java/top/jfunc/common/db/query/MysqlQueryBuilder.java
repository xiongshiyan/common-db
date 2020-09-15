package top.jfunc.common.db.query;

/**
 * MYSQL的模式
 *   SELECT .. FROM .. (LEFT|RIGHT|INNER) JOIN .. ON .. WHERE .... GROUP BY .. HAVING .. ORDER BY ... LIMIT offset , size
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class MysqlQueryBuilder extends AbstractQueryBuilder<MysqlQueryBuilder> {
    public MysqlQueryBuilder() {
        super(DataBase.MYSQL.getSqlBuilder());
    }
    public MysqlQueryBuilder(String select, String tableName, String alias){
        super(DataBase.MYSQL.getSqlBuilder(),select, tableName, alias);
    }
    public MysqlQueryBuilder(String select, String... froms){
        super(DataBase.MYSQL.getSqlBuilder(),select, froms);
    }

    /**
     * 添加Limit子句
     * @param pageNumber Base on 1
     * @param pageSize pageSize
     */
    public MysqlQueryBuilder addLimit(int pageNumber , int pageSize){
        paging(pageNumber, pageSize);
        return myself();
    }
}
