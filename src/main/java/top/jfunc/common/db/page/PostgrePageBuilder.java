package top.jfunc.common.db.page;

import top.jfunc.common.db.query.SqlKeyword;
import top.jfunc.common.db.query.SqlUtil;

/**
 * postgreSQL的模式
 *   SELECT .. FROM .. (LEFT|RIGHT|INNER) JOIN .. ON .. WHERE .... GROUP BY .. HAVING .. ORDER BY ... LIMIT size OFFSET o
 * @author xiongshiyan at 2020/9/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class PostgrePageBuilder implements PageBuilder {
    @Override
    public String sqlWithPage(String selectClause, String sqlExceptSelect, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        String limit = SqlUtil.leftRightBlank(SqlKeyword.LIMIT.getKeyword());
        String limitClause = limit + pageSize + SqlUtil.leftRightBlank("OFFSET") + offset;
        return SqlUtil.middleBlank(selectClause , sqlExceptSelect) + limitClause;
    }
}
