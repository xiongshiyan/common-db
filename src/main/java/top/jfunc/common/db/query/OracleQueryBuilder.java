package top.jfunc.common.db.query;

/**
 * 用于oracle
 * @author xiongshiyan
 */
public class OracleQueryBuilder extends AbstractQueryBuilder implements QueryBuilder {
    private int pageNumber = -1;
    private int pageSize = 10;

    public OracleQueryBuilder(String select, String tableName, String alias){
        super(select, tableName, alias);
    }
    public OracleQueryBuilder(String select, String... froms){
        super(select, froms);
    }

    @Override
    public OracleQueryBuilder page(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public String getSql() {
        //没有设置分页参数
        if(-1 == pageNumber){
            return super.getSql();
        }

        String select = getSelect();
        String sqlExceptSelect = getSqlExceptSelect();

        return withPageParams(select, sqlExceptSelect, this.pageNumber, this.pageSize);
    }

    @Override
    public String getSqlWithoutPadding() {
        //没有设置分页参数
        if(-1 == pageNumber){
            return super.getSqlWithoutPadding();
        }
        String select = getSelect();
        String sqlExceptSelectWithoutPadding = getSqlExceptSelectWithoutPadding();

        return withPageParams(select, sqlExceptSelectWithoutPadding, this.pageNumber, this.pageSize);
    }

    private String withPageParams(String sqlSelect , String sqlExceptSelect , int pageNumber , int pageSize){
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
