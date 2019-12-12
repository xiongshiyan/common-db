package top.jfunc.common.db.query;

import static top.jfunc.common.db.query.SqlUtil.COMMA;


/**
 * Sqlite3的模式
 *   SELECT .. FROM .. (LEFT|RIGHT|INNER) JOIN .. ON .. WHERE .... GROUP BY .. HAVING .. ORDER BY ... LIMIT offset , size
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class Sqlite3QueryBuilder extends AbstractQueryBuilder implements QueryBuilder {
    /**
     * limit子句
     */
    private String       limitClause    = "";

    public Sqlite3QueryBuilder(String select, String tableName, String alias){
        super(select, tableName, alias);
    }
    public Sqlite3QueryBuilder(String select, String... froms){
        super(select, froms);
    }

    @Override
    public Sqlite3QueryBuilder page(int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        String limit = leftRightBlankWithCase(SqlKeyword.LIMIT.getKeyword());
        limitClause = limit + offset + COMMA + pageSize;
        return this;
    }
    /**
     * From后面的所有语句 , 没有处理 ? ，添加limit处理
     * @see AbstractQueryBuilder#getSqlExceptSelect()
     */
    @Override
    public String getSqlExceptSelectWithoutPadding(){
        String sqlExceptSelectWithoutPadding = super.getSqlExceptSelectWithoutPadding();
        return sqlExceptSelectWithoutPadding + limitClause;
    }
}
