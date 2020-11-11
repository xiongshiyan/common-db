package top.jfunc.common.db.query;

/**
 * @author xiongshiyan at 2020/9/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class OraclePageBuilder implements PageBuilder {
    @Override
    public String sqlWithPage(String selectClause, String sqlExceptSelect, int pageNumber, int pageSize) {
        int start = (pageNumber - 1) * pageSize;
        int end = pageNumber * pageSize;
        return  "select * from ( select row_.*, rownum rownum_ from (  " +
                selectClause + " " + sqlExceptSelect +
                " ) row_ where rownum <= " + end + ") table_alias" +
                " where table_alias.rownum_ > " + start;
    }
}
