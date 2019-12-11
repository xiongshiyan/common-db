package top.jfunc.common.db;

import top.jfunc.common.db.query.QueryMapBuilder;
import top.jfunc.common.db.query.SqlKeyword;
import top.jfunc.common.utils.CollectionUtil;
import top.jfunc.common.utils.Joiner;

import java.util.*;


/**
 * SQL的模式
 *   SELECT .. FROM .. (LEFT|RIGHT|INNER) JOIN .. ON .. WHERE .... GROUP BY .. HAVING .. ORDER BY .. LIMIT ..
 * @author xiongshiyan
 */
public class QueryHelper implements QueryMapBuilder {
    /**
     *  SQL语句的关键字
     */
    private static final String BLANK           = " ";
    private static final String COMMA           = leftRightBlank(",");
    private static final String QUOTE           = "'";
    private static final String LEFT_BRAKET     = leftRightBlank("(");
    private static final String RIGHT_BRAKET    = leftRightBlank(")");

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
        fromClause.append(leftRightBlankWithCase(SqlKeyword.FROM.getKeyword())).append(tableName).append(BLANK).append(alias);
    }

    /**
     * 用于两张表联合的情况，生成From子句，类似from table1 a,table2 b 然后添加where条件
     * 另外像left join 这种可以写在一个from字句中或者使用 leftJoin rightJoin innerJoin方法
     */
    public QueryHelper(String select, String... froms){
        this.select = addSelectIfNecessary(select);
        String prefix = leftRightBlankWithCase(SqlKeyword.FROM.getKeyword());
        //if(INCLUDE_FROM.matcher(froms[0]).matches()){
        //去除空格取前5个[from ]
        if(startsWith(froms[0] , rightBlankWithCase(SqlKeyword.FROM.getKeyword()))){
            prefix = BLANK ;
        }
        fromClause.append(prefix).append(Joiner.on(COMMA).join(froms));
    }
    private String addSelectIfNecessary(String select) {
        //if(INCLUDE_SELECT.matcher(select).matches()){
        //去除空格取前6个[select ]
        String selectRightBlank = rightBlankWithCase(SqlKeyword.SELECT.getKeyword());
        if(startsWith(select , selectRightBlank)){
            //包含了select
            return select;
        }else {
            //没有包含select
            return selectRightBlank + select;
        }
    }

    /**
     * 判断一个字符串是否以某个关键词开头，不区分大小写
     * @param src 原字符串
     * @param keyWord 关键词
     * @return 是否以之开头
     */
    private static boolean startsWith(String src , String keyWord){
        String trim = src.trim();
        int len = keyWord.length();
        if(trim.length() < len){
            return false;
        }
        return trim.substring(0 , len).toUpperCase().startsWith(keyWord.toUpperCase());
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
     * @param onClause on条件 有一个添加在后面 , 不要带 ON 了 , 没有必须使用on方法添加
     */
    @Override
    public QueryHelper leftJoin(String joinClause , String onClause){
        leftJoin(joinClause);
        on(onClause);
        return this;
    }
    @Override
    public QueryHelper leftJoin(String joinClause){
        String leftJoin = leftRightBlankWithCase(SqlKeyword.LEFT_JOIN.getKeyword());
        fromClause.append(leftJoin).append(joinClause);
        return this;
    }
    //////////////////////////////////////2.2.rightJoin方法,添加RIGHT JOIN子句/////////////////////////////////////
    /**
     * 添加right join子句
     * @param joinClause RIGHT JOIN 子句
     */
    @Override
    public QueryHelper rightJoin(String joinClause , String onClause){
        rightJoin(joinClause);
        on(onClause);
        return this;
    }
    @Override
    public QueryHelper rightJoin(String joinClause){
        String rightJoin = leftRightBlankWithCase(SqlKeyword.RIGHT_JOIN.getKeyword());
        fromClause.append(rightJoin).append(joinClause);
        return this;
    }
    //////////////////////////////////////2.3.innerJoin方法,添加INNER JOIN子句/////////////////////////////////////
    /**
     * 添加inner join子句
     * @param joinClause INNER JOIN 子句
     */
    @Override
    public QueryHelper innerJoin(String joinClause , String onClause){
        innerJoin(joinClause);
        on(onClause);
        return this;
    }
    @Override
    public QueryHelper innerJoin(String joinClause){
        String innerJoin = leftRightBlankWithCase(SqlKeyword.INNER_JOIN.getKeyword());
        fromClause.append(innerJoin).append(joinClause);
        return this;
    }
    //////////////////////////////////////2.4.on方法,join子句添加on条件/////////////////////////////////////

    /**
     * 添加on子句 , 不要带ON 了 , 可以被 left、right、inner join子句使用  , 但是必须紧跟在JOIN 子句后面
     * @param onClause ON 子句
     */
    @Override
    public QueryHelper on(String onClause){
        String on = leftRightBlankWithCase(SqlKeyword.ON.getKeyword());
        fromClause.append(on).append(onClause);
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
    @Override
    public QueryHelper addCondition(String condition, Object... params){
        // 拼接条件
        addWhere(condition);
        // 添加参数
        addParams(params);
        return this;
    }

    ////////////////////////////4.or/orIf方法,添加条件,多个用 OR 连接////////////////////////////
    /**
     * 添加 OR 子句
     */
    @Override
    public QueryHelper or(String condition, Object... params){
        //OR 子句一般来说肯定不会是第一个，所以此时肯定存在了 WHERE
        String or = leftRightBlankWithCase(SqlKeyword.OR.getKeyword());
        whereClause.append(or).append(condition);
        // 添加参数
        addParams(params);
        return this;
    }
    ////////////////////////////5.addMapCondition方法,添加 Map 条件,多个用 AND 连接////////////////////////////
    /**
     * 主要是为了支持某些框架中的具名参数
     * @param condition 具体条件
     * @param keyValue 模式k1,v1,k2,v2...(k1,k2必须是String)
     */
    @Override
    public QueryHelper addMapCondition(String condition, Object... keyValue){
        // 拼接参数
        addWhere(condition);

        //添加map类型参数k1,v1,k2,v2...
        addMapParams(keyValue);

        return this;
    }

    private void addWhere(String condition) {
        // 拼接
        if(whereClause.length() == 0){
            String where = leftRightBlankWithCase(SqlKeyword.WHERE.getKeyword());
            whereClause = new StringBuilder(where).append(condition);
        } else{
            String and = leftRightBlankWithCase(SqlKeyword.AND.getKeyword());
            whereClause.append(and).append(condition);
        }
    }

    ///////////////////////////////////6.addIn方法,添加 IN 条件/////////////////////////////////////////////

    @Override
    public <T> QueryHelper addIn(String what , List<T> ins){
        if(CollectionUtil.isEmpty(ins)){
            return this;
        }
        // 拼接
        if(whereClause.length() == 0){
            String where = leftRightBlankWithCase(SqlKeyword.WHERE.getKeyword());
            whereClause = new StringBuilder(where);
        } else{
            String and = leftRightBlankWithCase(SqlKeyword.AND.getKeyword());
            whereClause.append(and);
        }
        // 添加左括号
        String in = leftRightBlankWithCase(SqlKeyword.IN.getKeyword());
        whereClause.append(what).append(in).append(LEFT_BRAKET);
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
    @Override
    public QueryHelper addOrderProperty(String propertyName, boolean asc){
        if(getOrderByClause().length() == 0){
            String orderBy = leftRightBlankWithCase(SqlKeyword.ORDER_BY.getKeyword());
            getOrderByClause().append(orderBy);
        } else{
            getOrderByClause().append(COMMA);
        }
        String ascStr = leftRightBlankWithCase(SqlKeyword.ASC.getKeyword());
        String descStr = leftRightBlankWithCase(SqlKeyword.DESC.getKeyword());
        getOrderByClause().append(propertyName).append(asc ? ascStr : descStr);
        return this;
    }
    ///////////////////////////////////8.addGroupProperty方法,添加 GROUP BY 子句//////////////////////////////////

    /**
     * 添加GROUP BY子句
     * @param groupByName group by
     */
    @Override
    public QueryHelper addGroupProperty(String groupByName){
        if(getGroupByClause().length() == 0){
            String groupBy = leftRightBlankWithCase(SqlKeyword.GROUP_BY.getKeyword());
            getGroupByClause().append(groupBy).append(groupByName);
        } else{
            getGroupByClause().append(COMMA).append(groupByName);
        }
        return this;
    }

    ///////////////////////////////////9.addHaving方法,添加 HAVING 子句//////////////////////////////////

    /**
     * @param having having子句
     * @param params 参数
     */
    @Override
    public QueryHelper addHaving(String having , Object... params){
        if(getHavingClause().length() == 0){
            String hav = leftRightBlankWithCase(SqlKeyword.HAVING.getKeyword());
            getHavingClause().append(hav).append(having);
        } else{
            String and = leftRightBlankWithCase(SqlKeyword.AND.getKeyword());
            getHavingClause().append(and).append(having);
        }

        addParams(params);

        return this;
    }

    /**
     * 主要是为了支持某些框架中的具名参数
     * @param having having子句
     * @param keyValue 模式k1,v1,k2,v2...
     */
    @Override
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
        return page(pageNumber, pageSize);
    }

    @Override
    public QueryHelper page(int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        String limit = leftRightBlankWithCase(SqlKeyword.LIMIT.getKeyword());
        limitClause = limit + offset + COMMA + pageSize;
        return this;
    }

    ///////////////////////////////////12.get相关方法,获取到组装的SQL语句，可以处理和不处理参数//////////////////////////////////

    /**
     * 获取 select
     */
    @Override
    public String getSelect(){
        return this.select;
    }

    /**
     * From后面的所有语句 , 没有处理 ?
     * @see QueryHelper#getSqlExceptSelect()
     */
    @Override
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
     * 获取最终拼装的SQL , 没有处理 ?
     * @see QueryHelper#getSql()
     */
    @Override
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

    /**
     * 获取生成的用于查询总记录数的SQL语句 , 没有处理 ?
     * @see QueryHelper#getCountQuerySql()
     * @see QueryHelper#getSqlWithoutPadding()
     */
    @Override
    public String getCountQuerySqlWithoutPadding(){
        String selectRightBlank = rightBlankWithCase(SqlKeyword.SELECT.getKeyword());
        StringBuilder builder = new StringBuilder(selectRightBlank).append(" COUNT(*) AS totalRow ").append(fromClause).append(whereClause);
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
    @Override
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
    @Override
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
    @Override
    public Map<String, Object> getMapParameters() {
        if(null == mapParameters){
            return new LinkedHashMap<>();
        }
        return mapParameters;
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

    private String leftBlankWithCase(String word){
        String leftBlank = leftBlank(word);
        return isUpper ? leftBlank.toUpperCase() : leftBlank.toLowerCase();
    }
    private String rightBlankWithCase(String word){
        String leftBlank = rightBlank(word);
        return isUpper ? leftBlank.toUpperCase() : leftBlank.toLowerCase();
    }
    private String leftRightBlankWithCase(String word){
        String leftBlank = leftRightBlank(word);
        return isUpper ? leftBlank.toUpperCase() : leftBlank.toLowerCase();
    }



    /**
     * 在左边添加空格
     */
    public static String leftBlank(String word){
        return BLANK + word;
    }
    /**
     * 在右边添加空格
     */
    public static String rightBlank(String word){
        return word + BLANK;
    }
    /**
     * 在左右量边添加空格
     */
    public static String leftRightBlank(String word){
        return BLANK + word + BLANK;
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
