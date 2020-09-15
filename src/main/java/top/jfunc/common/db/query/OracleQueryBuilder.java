package top.jfunc.common.db.query;

/**
 * 用于oracle
 * @author xiongshiyan
 */
public class OracleQueryBuilder extends AbstractQueryBuilder<OracleQueryBuilder> {
    public OracleQueryBuilder() {
    }
    public OracleQueryBuilder(String select, String tableName, String alias){
        super(select, tableName, alias);
    }
    public OracleQueryBuilder(String select, String... froms){
        super(select, froms);
    }

    @Override
    protected String sqlWithPage(String sqlSelect , String sqlExceptSelect , int pageNumber , int pageSize){
        int start = (pageNumber - 1) * pageSize;
        int end = pageNumber * pageSize;
        return  "select * from ( select row_.*, rownum rownum_ from (  " +
                sqlSelect + " " + sqlExceptSelect +
                " ) row_ where rownum <= " + end + ") table_alias" +
                " where table_alias.rownum_ > " + start;
    }
}
