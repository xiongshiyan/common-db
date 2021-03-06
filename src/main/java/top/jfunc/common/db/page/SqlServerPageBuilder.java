package top.jfunc.common.db.page;

import top.jfunc.common.db.query.SqlUtil;

/**
 * @author xiongshiyan at 2020/9/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SqlServerPageBuilder implements PageBuilder {
    private static final PageBuilder PAGE_BUILDER = new SqlServerPageBuilder();
    public static PageBuilder getInstance() {
        return PAGE_BUILDER;
    }
    @Override
    public String sqlWithPage(String selectClause, String sqlExceptSelect, int pageNumber, int pageSize) {
        int end = pageNumber * pageSize;
        if (end <= 0) {
            end = pageSize;
        }
        int begin = (pageNumber - 1) * pageSize;
        if (begin < 0) {
            begin = 0;
        }
        String select = selectClause.replaceFirst("(?i)select", "");
        return "SELECT * FROM ( SELECT row_number() over (order by tempcolumn) temprownumber, * FROM " +
                " ( SELECT TOP " + end + " tempcolumn=0," +
                    SqlUtil.middleBlank(select, sqlExceptSelect)
                + ")vip)mvp where temprownumber>" + begin;
    }
}
