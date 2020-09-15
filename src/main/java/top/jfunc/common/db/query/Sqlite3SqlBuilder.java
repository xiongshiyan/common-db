package top.jfunc.common.db.query;

import static top.jfunc.common.db.query.SqlUtil.COMMA;

/**
 * @author xiongshiyan at 2020/9/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class Sqlite3SqlBuilder implements SqlBuilder {
    @Override
    public String sqlWithPage(String selectClause, String sqlExceptSelect, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        String limit = SqlUtil.leftRightBlank(SqlKeyword.LIMIT.getKeyword());
        String limitClause = limit + offset + COMMA + pageSize;
        return selectClause + sqlExceptSelect + limitClause;
    }
}
