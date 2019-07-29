package top.jfunc.common.db.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;


/**
 * SQL的模式
 *   SELECT .. FROM .. (LEFT|RIGHT|INNER) JOIN .. ON .. WHERE .... GROUP BY .. HAVING .. ORDER BY .. LIMIT ..
 * @author xiongshiyan
 */
public interface QueryBuilder {

    boolean ASC = true;
    boolean DESC = false;
    /**
     *  替换掉SQL注入的那些字符 ['|;|--| and | or ]
     */
    String SQL_INJECT_CHARS = "([';]+|(--)+|(\\s+([aA][nN][dD])\\s+)+|(\\s+([oO][rR])\\s+)+)";
    /**
     *  开头是否包含关键字SELECT[不算空格],没有就加上
     */
    /*Pattern INCLUDE_SELECT = Pattern.compile("^(\\s*[sS][eE][lL][eE][cC][tT]\\s+)+(.|(\\r)?\\n)*");*/

    /**
     *  开头是否包含FROM关键字[不算空格],没有就加上
     */
    Pattern INCLUDE_FROM   = Pattern.compile("^(\\s*[fF][rR][oO][mM]\\s+)+(.|(\\r)?\\n)*");
    /**
     *  SQL语句的关键字
     */
    String BLANK           = " ";
    String SELECT          = "SELECT";
    String KW_SELECT       = SELECT + BLANK;
    String KW_FROM         = " FROM ";
    String KW_LEFT_JOIN    = " LEFT JOIN ";
    String KW_RIGHT_JOIN   = " RIGHT JOIN ";
    String KW_INNER_JOIN   = " INNER JOIN ";
    String KW_ON           = " ON ";
    String KW_WHERE        = " WHERE ";
    String KW_AND          = " AND ";
    String KW_OR           = " OR ";
    String KW_IN           = " IN ";
    String KW_GROUP_BY     = " GROUP BY ";
    String KW_HAVING       = " HAVING ";
    String KW_ORDER_BY     = " ORDER BY ";
    String KW_ASC          = " ASC ";
    String KW_DESC         = " DESC ";
    String KW_LIMIT        = " LIMIT ";
    String KW_UNION        = " UNION ";
    String KW_UNION_ALL    = " UNION ALL ";
    String COMMA           = " , ";
    String QUOTE           = "'";
    String LEFT_BRAKET     = " ( ";
    String RIGHT_BRAKET    = " ) ";

    QueryBuilder keyWordUpper();
    QueryBuilder keyWordLower();
    boolean isKeyWordUpper();
    QueryBuilder leftJoin(String joinClause, String on);
    QueryBuilder leftJoin(String joinClause);
    QueryBuilder rightJoin(String joinClause, String on);
    QueryBuilder rightJoin(String joinClause);
    QueryBuilder innerJoin(String joinClause, String on);
    QueryBuilder innerJoin(String joinClause);
    QueryBuilder on(String onClause);
    QueryBuilder addCondition(String condition, Object... params);
    public QueryBuilder or(String condition, Object... params);
    public <T> QueryBuilder addIn(String what, T... ins);
    public QueryBuilder addOrderProperty(String propertyName, boolean asc);
    public QueryBuilder addGroupProperty(String groupByName);
    public QueryBuilder addHaving(String having, Object... params);
    public QueryBuilder addLimit(int pageNumber, int pageSize);
    public String getSelect();
    public String getSqlExceptSelectWithoutPadding();
    public String getSqlWithoutPadding();
    public String getCountQuerySqlWithoutPadding();
    public List<Object> getListParameters();
    public Object[] getArrayParameters();


    default QueryBuilder and(String condition, Object... params){
        return addCondition(condition , params);
    }
    default QueryBuilder addCondition(boolean append, String condition, Object... params){
        if(append){
            addCondition(condition, params);
        }
        return this;
    }
    default QueryBuilder and(boolean append, String condition, Object... params){
        if(append){
            and(condition, params);
        }
        return this;
    }
    default QueryBuilder or(boolean append, String condition, Object... params){
        if(append){
            or(condition, params);
        }
        return this;
    }
    default QueryBuilder addAscOrderProperty(String propertyName){
        return addOrderProperty(propertyName , ASC);
    }
    default QueryBuilder addDescOrderProperty(String propertyName){
        return addOrderProperty(propertyName , DESC);
    }
    default QueryBuilder addOrderProperty(boolean append, String propertyName, boolean asc){
        if(append){
            addOrderProperty(propertyName, asc);
        }
        return this;
    }
    default QueryBuilder addAscOrderProperty(boolean append, String propertyName){
        if(append){
            addOrderProperty(propertyName, ASC);
        }
        return this;
    }
    default QueryBuilder addDescOrderProperty(boolean append, String propertyName){
        if(append){
            addOrderProperty(propertyName, DESC);
        }
        return this;
    }
    default QueryBuilder addHaving(boolean append, String having, Object... params){
        if(!append){
            return this;
        }
        return addHaving(having , params);
    }
    default String unionWithoutPadding(QueryBuilder other){
        QueryBuilder helper = Objects.requireNonNull(other);
        String sql1 = this.getSqlWithoutPadding();
        String sql2 = helper.getSqlWithoutPadding();
        return union(isKeyWordUpper() ? KW_UNION : KW_UNION.toLowerCase() , sql1 , sql2);
    }
    default String union(QueryBuilder other){
        QueryBuilder helper = Objects.requireNonNull(other);
        String sql1 = this.getSql();
        String sql2 = helper.getSql();
        return union(isKeyWordUpper() ? KW_UNION : KW_UNION.toLowerCase() , sql1 , sql2);
    }
    default String unionAllWithoutPadding(QueryBuilder other){
        QueryBuilder helper = Objects.requireNonNull(other);
        String sql1 = this.getSqlWithoutPadding();
        String sql2 = helper.getSqlWithoutPadding();
        return union(isKeyWordUpper() ? KW_UNION_ALL : KW_UNION_ALL.toLowerCase() , sql1 , sql2);
    }
    default String unionAll(QueryBuilder other){
        QueryBuilder helper = Objects.requireNonNull(other);
        String sql1 = this.getSql();
        String sql2 = helper.getSql();
        return union(isKeyWordUpper() ? KW_UNION_ALL : KW_UNION_ALL.toLowerCase() , sql1 , sql2);
    }
    default String union(String unionType, String oneSQL, String twoSQL, String... otherSQLs){
        //        String temp = (LEFT_BRAKET + oneSQL + RIGHT_BRAKET + unionType + LEFT_BRAKET + twoSQL + RIGHT_BRAKET );
        String temp = (oneSQL + unionType + twoSQL );
        if(null == otherSQLs || otherSQLs.length == 0){
            return temp.trim();
        }
        //给每个sql语句添加()
        List<String> withBraket = new ArrayList<>(otherSQLs.length);
        withBraket.addAll(Arrays.asList(otherSQLs));

        String join = join(unionType, unionType + BLANK, withBraket.toArray(new String[otherSQLs.length]));
        return (temp + join).trim();
    }
    default String getSqlExceptSelect(){
        return paddingParam(getSqlExceptSelectWithoutPadding());
    }
    default String getSql(){
        return paddingParam(getSqlWithoutPadding());
    }
    default String getCountQuerySql(){
        return paddingParam(getCountQuerySqlWithoutPadding());
    }
    default String paddingParam(String sql) {
        List<Object> params = getListParameters();
        // 填充参数
        if(params != null){
            for(int i = 0 , size = params.size(); i < size; i++){
                // 1.巧妙利用替换一次之后，后面的?就自动往前移动一位，那么replaceFirst每次替换的就是下一个?
                // 2.去掉某些特殊符号，防注入
                String param = (params.get(i) instanceof Number) ? params.get(i) + "" :
                        QUOTE + params.get(i).toString().replaceAll(SQL_INJECT_CHARS, "")
                                + QUOTE;
                sql = sql.replaceFirst("\\?", param);
            }
        }
        return sql;
    }

    public static String join(String separator, String prefix, String... parts){
        if(null == parts || 0 == parts.length){ throw new IllegalArgumentException("parts must more than one");}
        StringBuilder buffer = new StringBuilder(prefix);
        if(parts.length == 1){
            //只有一个的时候没必要添加了 separator 又删除
            return buffer.append(parts[0]).toString();
        }
        for(String part : parts){
            buffer.append(part).append(separator);
        }
        int i = buffer.lastIndexOf(separator);
        //去掉最后的separator
        return buffer.substring(0 , i);
    }
    @Override
    public String toString();
}
