package top.jfunc.common.db.query;

/**
 * @author xiongshiyan at 2020/9/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class PostgreSqlSqlBuilder implements SqlBuilder {
    @Override
    public String sqlWithPage(String selectClause, String sqlExceptSelect, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        String limit = SqlUtil.leftRightBlank(SqlKeyword.LIMIT.getKeyword());
        String limitClause = limit + pageSize + SqlUtil.leftRightBlank("offset") + offset;
        return selectClause + sqlExceptSelect + limitClause;
    }
}
