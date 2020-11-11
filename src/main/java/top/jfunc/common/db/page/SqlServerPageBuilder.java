package top.jfunc.common.db.query;

/**
 * @author xiongshiyan at 2020/9/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SqlServerPageBuilder implements PageBuilder {
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
        return "SELECT * FROM ( SELECT row_number() over (order by tempcolumn) temprownumber, * FROM " +
                " ( SELECT TOP " + end + " tempcolumn=0," +
                selectClause.replaceFirst("(?i)select", "") + " " + sqlExceptSelect +
                ")vip)mvp where temprownumber>" + begin;
    }
}
