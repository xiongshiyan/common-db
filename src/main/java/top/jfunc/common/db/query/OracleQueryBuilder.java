package top.jfunc.common.db.query;

/**
 * 用于oracle
 * @author xiongshiyan
 */
public class OracleQueryBuilder extends AbstractQueryBuilder implements QueryBuilder {
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
        StringBuilder ret = new StringBuilder();
        ret.append("select * from ( select row_.*, rownum rownum_ from (  ");
        ret.append(sqlSelect).append(" ").append(sqlExceptSelect);
        ret.append(" ) row_ where rownum <= ").append(end).append(") table_alias");
        ret.append(" where table_alias.rownum_ > ").append(start);
        return ret.toString();
    }
}
