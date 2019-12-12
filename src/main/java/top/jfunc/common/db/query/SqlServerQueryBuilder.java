package top.jfunc.common.db.query;

/**
 * sql server的模式
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SqlServerQueryBuilder extends AbstractQueryBuilder implements QueryBuilder {
    private int pageNumber = -1;
    private int pageSize = 10;

    public SqlServerQueryBuilder(String select, String tableName, String alias){
        super(select, tableName, alias);
    }
    public SqlServerQueryBuilder(String select, String... froms){
        super(select, froms);
    }

    @Override
    public SqlServerQueryBuilder page(int pageNumber, int pageSize) {
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
        int end = pageNumber * pageSize;
        if (end <= 0) {
            end = pageSize;
        }
        int begin = (pageNumber - 1) * pageSize;
        if (begin < 0) {
            begin = 0;
        }
        StringBuilder ret = new StringBuilder();
        ret.append("SELECT * FROM ( SELECT row_number() over (order by tempcolumn) temprownumber, * FROM ");
        ret.append(" ( SELECT TOP ").append(end).append(" tempcolumn=0,");
        ret.append(sqlSelect.replaceFirst("(?i)select", "")).append(" ").append(sqlExceptSelect);
        ret.append(")vip)mvp where temprownumber>").append(begin);
        return ret.toString();
    }
}
