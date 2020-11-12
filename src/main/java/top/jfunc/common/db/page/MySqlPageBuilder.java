package top.jfunc.common.db.page;

import top.jfunc.common.db.query.SqlKeyword;
import top.jfunc.common.db.query.SqlUtil;

import static top.jfunc.common.db.query.SqlUtil.COMMA;

/**
 * @author xiongshiyan at 2020/9/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class MySqlPageBuilder implements PageBuilder {
    private static final PageBuilder PAGE_BUILDER = new MySqlPageBuilder();
    public static PageBuilder getInstance() {
        return PAGE_BUILDER;
    }

    @Override
    public String sqlWithPage(String selectClause, String sqlExceptSelect, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        String limit = SqlUtil.leftRightBlank(SqlKeyword.LIMIT.getKeyword());
        String limitClause = limit + offset + COMMA + pageSize;
        return SqlUtil.middleBlank(selectClause , sqlExceptSelect) + limitClause;
    }
}
