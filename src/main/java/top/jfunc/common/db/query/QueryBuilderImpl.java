package top.jfunc.common.db.query;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author xiongshiyan
 */
public class QueryBuilderImpl implements QueryMapBuilder {
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
    private StringBuilder groupByClause = new StringBuilder();
    /**
     * having子句
     */
    private StringBuilder havingClause  = new StringBuilder();
    /**
     * order by子句
     */
    private StringBuilder orderByClause = new StringBuilder();
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
    public QueryBuilderImpl(String select, String tableName, String alias){
        this.select = addSelectIfNecessary(select);
        fromClause.append(KW_FROM).append(tableName).append(BLANK).append(alias);
    }

    /**
     * 用于两张表联合的情况，生成From子句，类似from table1 a,table2 b 然后添加where条件
     * 另外像left join 这种可以写在一个from字句中或者使用 leftJoin rightJoin innerJoin方法
     */
    public QueryBuilderImpl(String select, String... froms){
        this.select = addSelectIfNecessary(select);
        String prefix = KW_FROM ;
        if(INCLUDE_FROM.matcher(froms[0]).matches()){
            prefix = BLANK ;
        }
        fromClause.append(QueryBuilder.join(COMMA, prefix, froms));
    }
    private String addSelectIfNecessary(String select) {
        //if(INCLUDE_SELECT.matcher(select).matches()){
        //去除空格取前6个[select]
        if(startsWith(select , SELECT)){
            //包含了select
            return select;
        }else {
            //没有包含select
            return KW_SELECT + select;
        }
    }

    /**
     * 判断一个字符串是否以某个关键词开头，不区分大小写
     * @param src 原字符串
     * @param keyWord 关键词
     * @return 是否以之开头
     */
    private boolean startsWith(String src , String keyWord){
        String trim = src.trim();
        int len = keyWord.length();
        if(trim.length() < SELECT.length()){
            return false;
        }
        return trim.substring(0 , len).toUpperCase().startsWith(keyWord.toUpperCase());
    }

    @Override
    public QueryBuilderImpl keyWordUpper() {
        isUpper = true;
        return this;
    }
    @Override
    public QueryBuilderImpl keyWordLower() {
        isUpper = false;
        return this;
    }

    @Override
    public boolean isKeyWordUpper() {
        return isUpper;
    }

    //////////////////////////////////////2.1.leftJoin方法,添加LEFT JOIN子句/////////////////////////////////////
    /**
     * 添加left join子句
     * @param joinClause LEFT JOIN 子句
     * @param on on条件 有一个添加在后面 , 不要带 ON 了 , 没有必须使用on方法添加
     */
    @Override
    public QueryBuilderImpl leftJoin(String joinClause , String on){
        fromClause.append(isUpper ? KW_LEFT_JOIN : KW_LEFT_JOIN.toLowerCase()).append(joinClause);
        fromClause.append(QueryBuilder.join(BLANK, isUpper ? KW_ON : KW_ON.toLowerCase(), on));
        return this;
    }
    @Override
    public QueryBuilderImpl leftJoin(String joinClause){
        fromClause.append(isUpper ? KW_LEFT_JOIN : KW_LEFT_JOIN.toLowerCase()).append(joinClause);
        return this;
    }
    //////////////////////////////////////2.2.rightJoin方法,添加RIGHT JOIN子句/////////////////////////////////////
    /**
     * 添加right join子句
     * @param joinClause RIGHT JOIN 子句
     */
    @Override
    public QueryBuilderImpl rightJoin(String joinClause , String on){
        fromClause.append(isUpper ? KW_RIGHT_JOIN : KW_RIGHT_JOIN.toLowerCase()).append(joinClause);
        fromClause.append(QueryBuilder.join(BLANK, isUpper ? KW_ON : KW_ON.toLowerCase() , on));
        return this;
    }
    @Override
    public QueryBuilderImpl rightJoin(String joinClause){
        fromClause.append(isUpper ? KW_RIGHT_JOIN : KW_RIGHT_JOIN.toLowerCase()).append(joinClause);
        return this;
    }
    //////////////////////////////////////2.3.innerJoin方法,添加INNER JOIN子句/////////////////////////////////////
    /**
     * 添加inner join子句
     * @param joinClause INNER JOIN 子句
     */
    @Override
    public QueryBuilderImpl innerJoin(String joinClause , String on){
        fromClause.append(isUpper ? KW_INNER_JOIN : KW_INNER_JOIN.toLowerCase()).append(joinClause);
        fromClause.append(QueryBuilder.join(BLANK, isUpper ? KW_ON : KW_ON.toLowerCase() , on));
        return this;
    }
    @Override
    public QueryBuilderImpl innerJoin(String joinClause){
        fromClause.append(isUpper ? KW_INNER_JOIN : KW_INNER_JOIN.toLowerCase()).append(joinClause);
        return this;
    }
    //////////////////////////////////////2.4.on方法,join子句添加on条件/////////////////////////////////////

    /**
     * 添加on子句 , 不要带ON 了 , 可以被 left、right、inner join子句使用  , 但是必须紧跟在JOIN 子句后面
     * @param onClause ON 子句
     */
    @Override
    public QueryBuilderImpl on(String onClause){
        fromClause.append(isUpper ? KW_ON : KW_ON.toLowerCase()).append(onClause);
        return this;
    }

    ////////////////////////////3.addCondition/and/andIf方法,添加条件,多个用 AND 连接////////////////////////////
    /**
     * 拼接where子句 d.id between ? and ?   d.parent=?    d.parent is null
     * 跟 and(String, Object...) 的意义完全一致
     * @see QueryBuilderImpl#and(String, Object...)
     * @param condition 具体条件
     * @param params 参数,QueryHelper只支持？参数，如果你想用Query的具名参数，就不要设置参数，产生{Query}后再调用setParameter设置
     */
    @Override
    public QueryBuilderImpl addCondition(String condition, Object... params){
        // 拼接条件
        addWhere(condition);
        // 添加参数
        addParams(params);
        return this;
    }

    /**
     * 跟 addCondition(String, Object...) 的意义完全一致
     * @see QueryBuilderImpl#addCondition(String, Object...)
     */
    @Override
    public QueryBuilderImpl and(String condition, Object... params){
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
    public QueryBuilderImpl or(String condition, Object... params){
        //OR 子句一般来说肯定不会是第一个，所以此时肯定存在了 WHERE
        whereClause.append(isUpper ? KW_OR : KW_OR.toLowerCase()).append(condition);
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
    public QueryBuilderImpl addMapCondition(String condition, Object... keyValue){
        // 拼接参数
        addWhere(condition);

        //添加map类型参数k1,v1,k2,v2...
        addMapParams(keyValue);

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
    @Override
    public <T> QueryBuilderImpl addIn(String what , T... ins){
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
    @Override
    public QueryBuilderImpl addOrderProperty(String propertyName, boolean asc){
        if(orderByClause.length() == 0){
            orderByClause = new StringBuilder(isUpper ? KW_ORDER_BY : KW_ORDER_BY.toLowerCase());
        } else{
            orderByClause.append(COMMA);
        }

        orderByClause.append(propertyName + (asc ? (isUpper ? KW_ASC : KW_ASC.toLowerCase())
                : (isUpper ? KW_DESC : KW_DESC.toLowerCase())));
        return this;
    }
    ///////////////////////////////////8.addGroupProperty方法,添加 GROUP BY 子句//////////////////////////////////

    /**
     * 添加GROUP BY子句
     * @param groupByName group by
     */
    @Override
    public QueryBuilderImpl addGroupProperty(String groupByName){
        if(groupByClause.length() == 0){
            groupByClause = new StringBuilder(isUpper ? KW_GROUP_BY : KW_GROUP_BY.toLowerCase()).append(groupByName);
        } else{
            groupByClause.append(COMMA).append(groupByName);
        }
        return this;
    }

    ///////////////////////////////////9.addHaving方法,添加 HAVING 子句//////////////////////////////////

    /**
     * @param having having子句
     * @param params 参数
     */
    @Override
    public QueryBuilderImpl addHaving(String having , Object... params){
        if(havingClause.length() == 0){
            havingClause = new StringBuilder(isUpper ? KW_HAVING : KW_HAVING.toLowerCase()).append(having);
        } else{
            havingClause.append(isUpper ? KW_AND : KW_AND.toLowerCase()).append(having);
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
    public QueryBuilderImpl addMapHaving(String having, Object... keyValue){
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
    @Override
    public QueryBuilderImpl addLimit(int pageNumber , int pageSize){
        int thisPage = (pageNumber - 1) * pageSize;
        limitClause = (isUpper ? KW_LIMIT : KW_LIMIT.toLowerCase()) + thisPage + COMMA + pageSize;
        return this;
    }


    ///////////////////////////////////11.union相关方法,将两个SQL语句union起来//////////////////////////////////



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
     * @see QueryBuilderImpl#getSqlExceptSelect()
     */
    @Override
    public String getSqlExceptSelectWithoutPadding(){
        return new StringBuilder(fromClause).append(whereClause).append(groupByClause).append(havingClause)
                .append(orderByClause).append(limitClause).toString();
    }

    /**
     * 获取最终拼装的SQL , 没有处理 ?
     * @see QueryBuilderImpl#getSql()
     */
    @Override
    public String getSqlWithoutPadding(){
        return new StringBuilder(select).append(fromClause).append(whereClause).append(groupByClause).append(havingClause)
                .append(orderByClause).append(limitClause).toString();
    }

    /**
     * 获取生成的用于查询总记录数的SQL语句 , 没有处理 ?
     * @see QueryBuilderImpl#getCountQuerySql()
     * @see QueryBuilderImpl#getSqlWithoutPadding()
     */
    @Override
    public String getCountQuerySqlWithoutPadding(){
        return  KW_SELECT + " COUNT(*) AS totalRow " + fromClause + whereClause + groupByClause + havingClause;
    }
    /**
     * 获取SQL中的参数值列表，List返回
     * @see QueryBuilderImpl#addCondition(String, Object...)
     * @see QueryBuilderImpl#and(String, Object...)
     * @see QueryBuilderImpl#addMapHaving(String, Object...)
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
     * @see QueryBuilderImpl#addCondition(String, Object...)
     * @see QueryBuilderImpl#and(String, Object...)
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
     * @see QueryBuilderImpl#addMapCondition(String, Object...)
     * @see QueryBuilderImpl#addMapHaving(String, Object...)
     */
    @Override
    public Map<String, Object> getMapParameters() {
        if(null == mapParameters){
            return new LinkedHashMap<>();
        }
        return mapParameters;
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
