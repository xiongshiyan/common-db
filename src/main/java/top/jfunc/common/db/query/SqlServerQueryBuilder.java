package top.jfunc.common.db.query;

/**
 * sql server的模式
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SqlServerQueryBuilder extends AbstractQueryBuilder implements QueryBuilder {
    public SqlServerQueryBuilder(String select, String tableName, String alias){
        super(select, tableName, alias);
    }
    public SqlServerQueryBuilder(String select, String... froms){
        super(select, froms);
    }
    @Override
    protected String sqlWithPage(String sqlSelect , String sqlExceptSelect , int pageNumber , int pageSize){
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
