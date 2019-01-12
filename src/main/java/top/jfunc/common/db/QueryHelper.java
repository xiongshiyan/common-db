package top.jfunc.common.db;

import java.util.*;
import java.util.regex.Pattern;


/**
 * SQL的模式
 *   SELECT .. FROM .. (LEFT|RIGHT|INNER) JOIN .. ON .. WHERE .... GROUP BY .. HAVING .. ORDER BY .. LIMIT ..
 * @author xiongshiyan
 */
public class QueryHelper{

    public static final boolean ASC = true;
    public static final boolean DESC = false;
    /**
     *  替换掉SQL注入的那些字符 ['|;|--| and | or ]
     */
    private static final String SQL_INJECT_CHARS = "([';]+|(--)+|(\\s+([aA][nN][dD])\\s+)+|(\\s+([oO][rR])\\s+)+)";
    /**
     *  开头是否包含关键字SELECT[不算空格],没有就加上
     */
    private static final Pattern INCLUDE_SELECT = Pattern.compile("^(\\s*[sS][eE][lL][eE][cC][tT]\\s+)+(.|(\\r)?\\n)*");

    /**
     *  开头是否包含FROM关键字[不算空格],没有就加上
     */
    private static final Pattern INCLUDE_FROM   = Pattern.compile("^(\\s*[fF][rR][oO][mM]\\s+)+(.|(\\r)?\\n)*");

    /**
     *  SQL语句的关键字
     */
    private static final String KW_SELECT       = "SELECT ";
    private static final String KW_FROM         = " FROM ";
    private static final String KW_LEFT_JOIN    = " LEFT JOIN ";
    private static final String KW_RIGHT_JOIN   = " RIGHT JOIN ";
    private static final String KW_INNER_JOIN   = " INNER JOIN ";
    private static final String KW_ON           = " ON ";
    private static final String KW_WHERE        = " WHERE ";
    private static final String KW_AND          = " AND ";
    private static final String KW_OR           = " OR ";
    private static final String KW_IN           = " IN ";
    private static final String KW_GROUP_BY     = " GROUP BY ";
    private static final String KW_HAVING       = " HAVING ";
    private static final String KW_ORDER_BY     = " ORDER BY ";
    private static final String KW_ASC          = " ASC ";
    private static final String KW_DESC         = " DESC ";
    private static final String KW_LIMIT        = " LIMIT ";
    public  static final String KW_UNION        = " UNION ";
    public  static final String KW_UNION_ALL    = " UNION ALL ";
    private static final String COMMA           = " , ";
    private static final String BLANK           = " ";
    private static final String QUOTE           = "'";
    private static final String LEFT_BRAKET     = " ( ";
    private static final String RIGHT_BRAKET    = " ) ";

    /**
     * 关键字连接是否是大写 , 但是由于select 和 from 子句是在构造器中 , 需自行指定
     */
    private boolean isUpper = true;
    /**
     * select子句
     */
    private String       select;
    /**
     * from子句
     */
    private StringBuilder fromClause    = new StringBuilder();
    /**
     * where子句
     */
    private StringBuilder whereClause   = new StringBuilder();
    /**
     * group by子句
     */
    private StringBuilder groupByClause = null;
    /**
     * having子句
     */
    private StringBuilder havingClause  = null;
    /**
     * order by子句
     */
    private StringBuilder orderByClause = null;
    /**
     * limit子句
     */
    private String       limitClause    = "";
    /**
     * 参数列表
     */
    private List<Object> parameters;
    /**
     * map类型的参数
     */
    private Map<String , Object> mapParameters;

    //////////////////////////////////////1.构造方法,确定基本的表和查询字段/////////////////////////////////////
    /**
     * 用于一张表的情况，生成From子句
     * from topic t
     */
    public QueryHelper(String select, String tableName, String alias){
        this.select = addSelectIfNecessary(select);
        fromClause.append(KW_FROM).append(tableName).append(BLANK).append(alias);
    }

    /**
     * 用于两张表联合的情况，生成From子句，类似from table1 a,table2 b 然后添加where条件
     * 另外像left join 这种可以写在一个from字句中或者使用 leftJoin rightJoin innerJoin方法
     */
    public QueryHelper(String select, String... froms){
        this.select = addSelectIfNecessary(select);
        String prefix = KW_FROM ;
        if(INCLUDE_FROM.matcher(froms[0]).matches()){
            prefix = BLANK ;
        }
        fromClause.append(join(COMMA, prefix, froms));
    }
    private String addSelectIfNecessary(String select) {
        if(INCLUDE_SELECT.matcher(select).matches()){
            //包含了select
            return select;
        }else {
            //没有包含select
            return KW_SELECT + select;
        }
    }

    public QueryHelper keyWordUpper() {
        isUpper = true;
        return this;
    }
    public QueryHelper keyWordLower() {
        isUpper = false;
        return this;
    }
    //////////////////////////////////////2.1.leftJoin方法,添加LEFT JOIN子句/////////////////////////////////////
    /**
     * 添加left join子句
     * @param joinClause LEFT JOIN 子句
     * @param on on条件 有一个添加在后面 , 不要带 ON 了 , 没有必须使用on方法添加
     */
    public QueryHelper leftJoin(String joinClause , String on){
        fromClause.append(isUpper ? KW_LEFT_JOIN : KW_LEFT_JOIN.toLowerCase()).append(joinClause);
        fromClause.append(join(BLANK, isUpper ? KW_ON : KW_ON.toLowerCase(), on));
        return this;
    }
    public QueryHelper leftJoin(String joinClause){
        fromClause.append(isUpper ? KW_LEFT_JOIN : KW_LEFT_JOIN.toLowerCase()).append(joinClause);
        return this;
    }
    //////////////////////////////////////2.2.rightJoin方法,添加RIGHT JOIN子句/////////////////////////////////////
    /**
     * 添加right join子句
     * @param joinClause RIGHT JOIN 子句
     */
    public QueryHelper rightJoin(String joinClause , String on){
        fromClause.append(isUpper ? KW_RIGHT_JOIN : KW_RIGHT_JOIN.toLowerCase()).append(joinClause);
        fromClause.append(join(BLANK, isUpper ? KW_ON : KW_ON.toLowerCase() , on));
        return this;
    }
    public QueryHelper rightJoin(String joinClause){
        fromClause.append(isUpper ? KW_RIGHT_JOIN : KW_RIGHT_JOIN.toLowerCase()).append(joinClause);
        return this;
    }
    //////////////////////////////////////2.3.innerJoin方法,添加INNER JOIN子句/////////////////////////////////////
    /**
     * 添加inner join子句
     * @param joinClause INNER JOIN 子句
     */
    public QueryHelper innerJoin(String joinClause , String on){
        fromClause.append(isUpper ? KW_INNER_JOIN : KW_INNER_JOIN.toLowerCase()).append(joinClause);
        fromClause.append(join(BLANK, isUpper ? KW_ON : KW_ON.toLowerCase() , on));
        return this;
    }
    public QueryHelper innerJoin(String joinClause){
        fromClause.append(isUpper ? KW_INNER_JOIN : KW_INNER_JOIN.toLowerCase()).append(joinClause);
        return this;
    }
    //////////////////////////////////////2.4.on方法,join子句添加on条件/////////////////////////////////////

    /**
     * 添加on子句 , 不要带ON 了 , 可以被 left、right、inner join子句使用  , 但是必须紧跟在JOIN 子句后面
     * @param onClause ON 子句
     */
    public QueryHelper on(String onClause){
        fromClause.append(isUpper ? KW_ON : KW_ON.toLowerCase()).append(onClause);
        return this;
    }

    ////////////////////////////3.addCondition/and/andIf方法,添加条件,多个用 AND 连接////////////////////////////
    /**
     * 拼接where子句 d.id between ? and ?   d.parent=?    d.parent is null
     * 跟 and(String, Object...) 的意义完全一致
     * @see QueryHelper#and(String, Object...)
     * @param condition 具体条件
     * @param params 参数,QueryHelper只支持？参数，如果你想用Query的具名参数，就不要设置参数，产生{Query}后再调用setParameter设置
     */
    public QueryHelper addCondition(String condition, Object... params){
        // 拼接条件
        addWhere(condition);
        // 添加参数
        addParams(params);
        return this;
    }

    /**
     * 跟 addCondition(String, Object...) 的意义完全一致
     * @see QueryHelper#addCondition(String, Object...)
     */
    public QueryHelper and(String condition, Object... params){
        // 拼接条件
        addWhere(condition);
        // 添加参数
        addParams(params);
        return this;
    }

    /**
     * 根据条件决定是否添加条件
     * @see QueryHelper#and(boolean, String, Object...)
     */
    public QueryHelper addCondition(boolean append, String condition, Object... params){
        if(append){
            addCondition(condition, params);
        }
        return this;
    }

    /**
     * 根据条件决定是否添加条件
     * @see QueryHelper#addCondition(boolean, String, Object...)
     */
    public QueryHelper and(boolean append, String condition, Object... params){
        if(append){
            and(condition, params);
        }
        return this;
    }

    ////////////////////////////4.or/orIf方法,添加条件,多个用 OR 连接////////////////////////////
    /**
     * 添加 OR 子句
     */
    public QueryHelper or(String condition, Object... params){
        //OR 子句一般来说肯定不会是第一个，所以此时肯定存在了 WHERE
        whereClause.append(isUpper ? KW_OR : KW_OR.toLowerCase()).append(condition);
        // 添加参数
        addParams(params);
        return this;
    }
    /**
     * 根据条件决定是否添加条件
     * @see QueryHelper#or(String, Object...)
     */
    public QueryHelper or(boolean append , String condition, Object... params){
        if(append){
            or(condition, params);
        }
        return this;
    }
    ////////////////////////////5.addMapCondition方法,添加 Map 条件,多个用 AND 连接////////////////////////////
    /**
     * 主要是为了支持某些框架中的具名参数
     * @param condition 具体条件
     * @param keyValue 模式k1,v1,k2,v2...(k1,k2必须是String)
     */
    public QueryHelper addMapCondition(String condition, Object... keyValue){
        // 拼接参数
        addWhere(condition);

        //添加map类型参数k1,v1,k2,v2...
        addMapParams(keyValue);

        return this;
    }

    /**
     * @param append 是否拼装此条件
     * @param condition 具体条件
     * @param keyValue 参数
     */
    public QueryHelper addMapCondition(boolean append, String condition, Object... keyValue){
        if(append){
            addMapCondition(condition, keyValue);
        }
        return this;
    }

    private void addWhere(String condition) {
        // 拼接
        if(whereClause.length() == 0){
            whereClause = new StringBuilder(isUpper ? KW_WHERE : KW_WHERE.toLowerCase()).append(condition);
        } else{
            whereClause.append(isUpper ? KW_AND : KW_AND.toLowerCase()).append(condition);
        }
    }

    ///////////////////////////////////6.addIn方法,添加 IN 条件/////////////////////////////////////////////
    /**
     * addIn("d.id" , 1,2,3) - > d.id IN (1,2,3)
     * addIn("d.phone" , "1","2","3") - > d.id IN ('1','2','3')
     * @param what 添加 IN 语句
     * @param ins In条件
     */
    public <T> QueryHelper addIn(String what , T... ins){
        if(null == ins || ins.length == 0){
            throw new IllegalArgumentException("必须至少包含一个in条件");
        }
        // 拼接
        if(whereClause.length() == 0){
            whereClause = new StringBuilder(isUpper ? KW_WHERE : KW_WHERE.toLowerCase());
        } else{
            whereClause.append(isUpper ? KW_AND : KW_AND.toLowerCase());
        }
        // 添加左括号
        whereClause.append(what).append(isUpper ? KW_IN : KW_IN.toLowerCase()).append(LEFT_BRAKET);
        for(Object part : ins){
            //数字不需要'' , 其他就转化为字符串并加上''
            String x = part instanceof Number ? part.toString() : QUOTE + part + QUOTE;
            whereClause.append(x).append(COMMA);
        }
        // 去掉最后的 ,
        whereClause = new StringBuilder(whereClause.substring(0 , whereClause.lastIndexOf(COMMA)));
        //添加右括号
        whereClause.append(RIGHT_BRAKET);

        return this;
    }

    ///////////////////////////////////7.addOrderProperty方法,添加 ORDER BY 子句//////////////////////////////////
    /**
     * 拼接order by子句
     * @param propertyName 参与排序的属性名
     * @param asc true表示升序，false表示降序
     */
    public QueryHelper addOrderProperty(String propertyName, boolean asc){
        if(getOrderByClause().length() == 0){
            getOrderByClause().append(isUpper ? KW_ORDER_BY : KW_ORDER_BY.toLowerCase());
        } else{
            getOrderByClause().append(COMMA);
        }

        getOrderByClause().append(propertyName + (asc ? (isUpper ? KW_ASC : KW_ASC.toLowerCase())
                : (isUpper ? KW_DESC : KW_DESC.toLowerCase())));
        return this;
    }

    public QueryHelper addAscOrderProperty(String propertyName){
        return addOrderProperty(propertyName , ASC);
    }
    public QueryHelper addDescOrderProperty(String propertyName){
        return addOrderProperty(propertyName , DESC);
    }
    /**
     * @param append 是否拼装这个排序
     * @param propertyName 排序属性
     * @param asc true表示升序，false表示降序
     */
    public QueryHelper addOrderProperty(boolean append, String propertyName, boolean asc){
        if(append){
            addOrderProperty(propertyName, asc);
        }
        return this;
    }

    public QueryHelper addAscOrderProperty(boolean append, String propertyName){
        if(append){
            addOrderProperty(propertyName, ASC);
        }
        return this;
    }
    public QueryHelper addDescOrderProperty(boolean append, String propertyName){
        if(append){
            addOrderProperty(propertyName, DESC);
        }
        return this;
    }

    ///////////////////////////////////8.addGroupProperty方法,添加 GROUP BY 子句//////////////////////////////////

    /**
     * 添加GROUP BY子句
     * @param groupByName group by
     */
    public QueryHelper addGroupProperty(String groupByName){
        if(getGroupByClause().length() == 0){
            getGroupByClause().append(isUpper ? KW_GROUP_BY : KW_GROUP_BY.toLowerCase()).append(groupByName);
        } else{
            getGroupByClause().append(COMMA).append(groupByName);
        }
        return this;
    }

    ///////////////////////////////////9.addHaving方法,添加 HAVING 子句//////////////////////////////////

    /**
     * 是否添加此having子句
     * @see QueryHelper#addHaving(String, Object...)
     */
    public QueryHelper addHaving(boolean append , String having , Object... params){
        if(!append){
            return this;
        }
        return addHaving(having , params);
    }

    /**
     * @param having having子句
     * @param params 参数
     */
    public QueryHelper addHaving(String having , Object... params){
        if(getHavingClause().length() == 0){
            getHavingClause().append(isUpper ? KW_HAVING : KW_HAVING.toLowerCase()).append(having);
        } else{
            getHavingClause().append(isUpper ? KW_AND : KW_AND.toLowerCase()).append(having);
        }

        addParams(params);

        return this;
    }

    public QueryHelper addMapHaving(boolean append , String having, Object... keyValue){
        if(!append){return this;}

        return addMapHaving(having , keyValue);
    }

    /**
     * 主要是为了支持某些框架中的具名参数
     * @param having having子句
     * @param keyValue 模式k1,v1,k2,v2...
     */
    public QueryHelper addMapHaving(String having, Object... keyValue){
        // 拼接having
        addHaving(having);

        //增加map参数
        addMapParams(keyValue);

        return this;
    }

    private void addMapParams(Object... keyValue) {
        if(0 != (keyValue.length % 2)){
            throw new IllegalArgumentException("参数必须符合模式k1,v1,k2,v2...");
        }
        if(null == mapParameters){
            mapParameters = new LinkedHashMap<>();
        }
        int kvLen = keyValue.length / 2;
        for (int i = 0; i < kvLen; i++) {
            mapParameters.put(keyValue[i].toString() , keyValue[i+1]);
        }
    }


    private void addParams(Object... params) {
        // 参数
        if(params != null){
            if(null == parameters){
                parameters = new LinkedList<>();
            }
            if(params.length == 0){return;}

            for(Object p : params){
                if(p != null){
                    parameters.add(p);
                }
            }
        }
    }

    ///////////////////////////////////10.addHaving方法,添加 HAVING 子句//////////////////////////////////
    /**
     * 添加Limit子句
     * @param pageNumber Base on 1
     * @param pageSize pageSize
     */
    public QueryHelper addLimit(int pageNumber , int pageSize){
        int thisPage = (pageNumber - 1) * pageSize;
        limitClause = (isUpper ? KW_LIMIT : KW_LIMIT.toLowerCase()) + thisPage + COMMA + pageSize;
        return this;
    }


    ///////////////////////////////////11.union相关方法,将两个SQL语句union起来//////////////////////////////////

    /**
     * union
     */
    public String unionWithoutPadding(QueryHelper other){
        QueryHelper helper = Objects.requireNonNull(other);
        String sql1 = this.getSqlWithoutPadding();
        String sql2 = helper.getSqlWithoutPadding();
        return union(isUpper ? KW_UNION : KW_UNION.toLowerCase() , sql1 , sql2);
    }

    public String union(QueryHelper other){
        QueryHelper helper = Objects.requireNonNull(other);
        String sql1 = this.getSql();
        String sql2 = helper.getSql();
        return union(isUpper ? KW_UNION : KW_UNION.toLowerCase() , sql1 , sql2);
    }
    public String unionAllWithoutPadding(QueryHelper other){
        QueryHelper helper = Objects.requireNonNull(other);
        String sql1 = this.getSqlWithoutPadding();
        String sql2 = helper.getSqlWithoutPadding();
        return union(isUpper ? KW_UNION_ALL : KW_UNION_ALL.toLowerCase() , sql1 , sql2);
    }
    public String unionAll(QueryHelper other){
        QueryHelper helper = Objects.requireNonNull(other);
        String sql1 = this.getSql();
        String sql2 = helper.getSql();
        return union(isUpper ? KW_UNION_ALL : KW_UNION_ALL.toLowerCase() , sql1 , sql2);
    }

    /**
     *
     * @param unionType UNION / UNION ALL
     * @param oneSQL sql
     * @param twoSQL sql
     * @param otherSQLs sqls
     * @return sql with union
     */
    public String union(String unionType , String oneSQL , String twoSQL , String... otherSQLs){
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


    ///////////////////////////////////12.get相关方法,获取到组装的SQL语句，可以处理和不处理参数//////////////////////////////////

    /**
     * 获取 select
     */
    public String getSelect(){
        return this.select;
    }

    /**
     * From后面的所有语句 , 处理了 ? 参数的
     * @see QueryHelper#getSqlExceptSelectWithoutPadding()
     */
    public String getSqlExceptSelect(){
        return paddingParam(getSqlExceptSelectWithoutPadding());
    }

    /**
     * From后面的所有语句 , 没有处理 ?
     * @see QueryHelper#getSqlExceptSelect()
     */
    public String getSqlExceptSelectWithoutPadding(){
        StringBuilder builder = new StringBuilder(fromClause).append(whereClause);
        if(null != groupByClause){
            builder.append(groupByClause);
        }
        if(null != havingClause){
            builder.append(havingClause);
        }
        if(null != orderByClause){
            builder.append(orderByClause);
        }
        return builder.append(limitClause).toString();
    }

    /**
     * 获取最终拼装的SQL , 并且处理了 ? 参数的
     * @see QueryHelper#getSqlWithoutPadding
     */
    public String getSql(){
        return paddingParam(getSqlWithoutPadding());
    }

    /**
     * 获取最终拼装的SQL , 没有处理 ?
     * @see QueryHelper#getSql()
     */
    public String getSqlWithoutPadding(){
        StringBuilder builder = new StringBuilder(select).append(fromClause).append(whereClause);
        if(null != groupByClause){
            builder.append(groupByClause);
        }
        if(null != havingClause){
            builder.append(havingClause);
        }
        if(null != orderByClause){
            builder.append(orderByClause);
        }
        return builder.append(limitClause).toString();
    }

    private String paddingParam(String sql) {
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


    /**
     * 获取生成的用于查询总记录数的SQL语句 , 并且处理了 ? 参数的
     * @see QueryHelper#getCountQuerySqlWithoutPadding()
     */
    public String getCountQuerySql(){
        return paddingParam(getCountQuerySqlWithoutPadding());
    }

    /**
     * 获取生成的用于查询总记录数的SQL语句 , 没有处理 ?
     * @see QueryHelper#getCountQuerySql()
     * @see QueryHelper#getSqlWithoutPadding()
     */
    public String getCountQuerySqlWithoutPadding(){
        StringBuilder builder = new StringBuilder(KW_SELECT).append(" COUNT(*) AS totalRow ").append(fromClause).append(whereClause);
        if(null != groupByClause){
            builder.append(groupByClause);
        }
        if(null != havingClause){
            builder.append(havingClause);
        }
        return builder.toString();
    }
    /**
     * 获取SQL中的参数值列表，List返回
     * @see QueryHelper#addCondition(String, Object...)
     * @see QueryHelper#and(String, Object...)
     * @see QueryHelper#addMapHaving(String, Object...)
     */
    public List<Object> getListParameters(){
        if(null == parameters){
            return new LinkedList<>();
        }
        return parameters;
    }

    /**
     * 获取SQL中的参数值列表，Array返回
     * @see QueryHelper#addCondition(String, Object...)
     * @see QueryHelper#and(String, Object...)
     */
    public Object[] getArrayParameters(){
        if(null == parameters){
            return new Object[0];
        }
        return parameters.toArray();
    }

    /**
     * 获取SQL中的参数值列表，Map返回
     * @see QueryHelper#addMapCondition(String, Object...)
     * @see QueryHelper#addMapHaving(String, Object...)
     */
    public Map<String, Object> getMapParameters() {
        if(null == mapParameters){
            return new LinkedHashMap<>();
        }
        return mapParameters;
    }

    /**
     * 工具方法 ： s, p ,[1,2,3] -> "p1s2s3"
     * @param separator 分隔符
     * @param prefix 前缀
     * @param parts 多个
     */
    private static String join(String separator , String prefix , String... parts){
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


    public StringBuilder getOrderByClause() {
        if(null == orderByClause){
            orderByClause = new StringBuilder();
        }
        return orderByClause;
    }

    public StringBuilder getGroupByClause() {
        if(null == groupByClause){
            groupByClause = new StringBuilder();
        }
        return groupByClause;
    }

    public StringBuilder getHavingClause() {
        if(null == havingClause){
            havingClause = new StringBuilder();
        }
        return havingClause;
    }

    @Override
    public String toString() {
        return "QueryHelper{" +
                "sql=" + getSql() +
                ", parameters=" + getListParameters() +
                ", mapParameters=" + getMapParameters() +
                '}';
    }
}
