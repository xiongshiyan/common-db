package top.jfunc.common.db;

import top.jfunc.common.db.query.AbstractQueryBuilder;
import top.jfunc.common.db.query.QueryBuilder;
import top.jfunc.common.db.query.SqlKeyword;
import static top.jfunc.common.db.query.SqlUtil.*;


/**
 * 用于mysql
 * @author xiongshiyan
 */
public class QueryHelper extends AbstractQueryBuilder implements QueryBuilder {
    /**
     * limit子句
     */
    private String       limitClause    = "";

    /**
     * 用于一张表的情况，生成From子句
     * from topic t
     */
    public QueryHelper(String select, String tableName, String alias){
        super(select, tableName, alias);
    }

    /**
     * 用于两张表联合的情况，生成From子句，类似from table1 a,table2 b 然后添加where条件
     * 另外像left join 这种可以写在一个from字句中或者使用 leftJoin rightJoin innerJoin方法
     */
    public QueryHelper(String select, String... froms){
        super(select, froms);
    }

    /**
     * 添加Limit子句
     * @param pageNumber Base on 1
     * @param pageSize pageSize
     */
    public QueryHelper addLimit(int pageNumber , int pageSize){
        return page(pageNumber, pageSize);
    }

    @Override
    public QueryHelper page(int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        String limit = leftRightBlankWithCase(SqlKeyword.LIMIT.getKeyword());
        limitClause = limit + offset + COMMA + pageSize;
        return this;
    }
    /**
     * From后面的所有语句 , 没有处理 ?
     * @see AbstractQueryBuilder#getSqlExceptSelect()
     */
    @Override
    public String getSqlExceptSelectWithoutPadding(){
        StringBuilder builder = new StringBuilder(fromClause).append(whereClause);
        if(null != groupByClause){
            builder.append(groupByClause);
        }
        if(null != havingClause){
            builder.append(havingClause);
        }
        if(null != orderByClause){
            builder.append(orderByClause);
        }
        return builder.append(limitClause).toString();
    }
    /**
     * 获取最终拼装的SQL , 没有处理 ?
     * @see AbstractQueryBuilder#getSql()
     */
    @Override
    public String getSqlWithoutPadding(){
        String sqlExceptSelectWithoutPadding = getSqlExceptSelectWithoutPadding();
        return select + sqlExceptSelectWithoutPadding;
    }
}
