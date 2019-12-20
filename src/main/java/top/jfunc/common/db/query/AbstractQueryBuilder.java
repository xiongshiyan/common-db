package top.jfunc.common.db.query;

import top.jfunc.common.ChainCall;
import top.jfunc.common.utils.CollectionUtil;
import top.jfunc.common.utils.Joiner;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static top.jfunc.common.db.query.SqlUtil.*;
import static top.jfunc.common.db.query.SqlUtil.leftRightBlank;

/**
 * SQL的模式
 *   SELECT .. FROM .. (LEFT|RIGHT|INNER) JOIN .. ON .. WHERE .... GROUP BY .. HAVING .. ORDER BY
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractQueryBuilder<THIS extends AbstractQueryBuilder> implements QueryBuilder , ChainCall<THIS>{
    /**
     * 关键字连接是否是大写 , 但是由于select 和 from 子句是在构造器中 , 需自行指定
     */
    protected boolean isUpper = true;
    /**
     * select子句
     */
    protected String       select;
    /**
     * from子句
     */
    protected StringBuilder fromClause    = new StringBuilder();
    /**
     * where子句
     */
    protected StringBuilder whereClause   = new StringBuilder();
    /**
     * group by子句
     */
    protected StringBuilder groupByClause = null;
    /**
     * having子句
     */
    protected StringBuilder havingClause  = null;
    /**
     * order by子句
     */
    protected StringBuilder orderByClause = null;
    /**
     * 参数列表
     */
    protected List<Object> parameters;
    /**
     * map类型的参数
     */
    protected Map<String , Object> mapParameters;

    /**
     * 默认-1表示没有分页数据，based on 1
     */
    protected int pageNumber = -1;
    protected int pageSize = 10;

    //////////////////////////////////////1.构造方法,确定基本的表和查询字段/////////////////////////////////////

    public AbstractQueryBuilder() {
    }

    /**
     * 用于一张表的情况，生成From子句
     * from topic t
     */
    public AbstractQueryBuilder(String select, String tableName, String alias){
        this.select = addSelectIfNecessary(select);
        fromClause.append(leftRightBlankWithCase(SqlKeyword.FROM.getKeyword())).append(tableName).append(BLANK).append(alias);
    }

    /**
     * 用于两张表联合的情况，生成From子句，类似from table1 a,table2 b 然后添加where条件
     * 另外像left join 这种可以写在一个from字句中或者使用 leftJoin rightJoin innerJoin方法
     */
    public AbstractQueryBuilder(String select, String... froms){
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

    public THIS keyWordUpper() {
        isUpper = true;
        return myself();
    }
    public THIS keyWordLower() {
        isUpper = false;
        return myself();
    }

    //////////////////////////////////////2.1.leftJoin方法,添加LEFT JOIN子句/////////////////////////////////////

    /**
     * 添加left join子句
     * @param joinClause LEFT JOIN 子句
     * @param onClause on条件 有一个添加在后面 , 不要带 ON 了 , 没有必须使用on方法添加
     */
    @Override
    public THIS leftJoin(String joinClause , String onClause){
        leftJoin(joinClause);
        on(onClause);
        return myself();
    }
    @Override
    public THIS leftJoin(String joinClause){
        String leftJoin = leftRightBlankWithCase(SqlKeyword.LEFT_JOIN.getKeyword());
        fromClause.append(leftJoin).append(joinClause);
        return myself();
    }

    //////////////////////////////////////2.2.rightJoin方法,添加RIGHT JOIN子句/////////////////////////////////////

    /**
     * 添加right join子句
     * @param joinClause RIGHT JOIN 子句
     */
    @Override
    public THIS rightJoin(String joinClause , String onClause){
        rightJoin(joinClause);
        on(onClause);
        return myself();
    }
    @Override
    public THIS rightJoin(String joinClause){
        String rightJoin = leftRightBlankWithCase(SqlKeyword.RIGHT_JOIN.getKeyword());
        fromClause.append(rightJoin).append(joinClause);
        return myself();
    }

    //////////////////////////////////////2.3.innerJoin方法,添加INNER JOIN子句/////////////////////////////////////

    /**
     * 添加inner join子句
     * @param joinClause INNER JOIN 子句
     */
    @Override
    public THIS innerJoin(String joinClause , String onClause){
        innerJoin(joinClause);
        on(onClause);
        return myself();
    }
    @Override
    public THIS innerJoin(String joinClause){
        String innerJoin = leftRightBlankWithCase(SqlKeyword.INNER_JOIN.getKeyword());
        fromClause.append(innerJoin).append(joinClause);
        return myself();
    }

    //////////////////////////////////////2.4.on方法,join子句添加on条件/////////////////////////////////////

    /**
     * 添加on子句 , 不要带ON 了 , 可以被 left、right、inner join子句使用  , 但是必须紧跟在JOIN 子句后面
     * @param onClause ON 子句
     */
    @Override
    public THIS on(String onClause){
        String on = leftRightBlankWithCase(SqlKeyword.ON.getKeyword());
        fromClause.append(on).append(onClause);
        return myself();
    }

    ////////////////////////////3.addCondition/and/andIf方法,添加条件,多个用 AND 连接////////////////////////////

    /**
     * 拼接where子句 d.id between ? and ?   d.parent=?    d.parent is null
     * 跟 and(String, Object...) 的意义完全一致
     * @see THIS#and(String, Object...)
     * @param condition 具体条件
     * @param params 参数,THIS只支持？参数，如果你想用Query的具名参数，就不要设置参数，产生{Query}后再调用setParameter设置
     */
    @Override
    public THIS addCondition(String condition, Object... params){
        // 拼接条件
        addWhere(condition);
        // 添加参数
        addParams(params);
        return myself();
    }

    ////////////////////////////4.or/orIf方法,添加条件,多个用 OR 连接////////////////////////////

    /**
     * 添加 OR 子句
     */
    @Override
    public THIS or(String condition, Object... params){
        //OR 子句一般来说肯定不会是第一个，所以此时肯定存在了 WHERE
        String or = leftRightBlankWithCase(SqlKeyword.OR.getKeyword());
        whereClause.append(or).append(condition);
        // 添加参数
        addParams(params);
        return myself();
    }

    ////////////////////////////5.addMapCondition方法,添加 Map 条件,多个用 AND 连接////////////////////////////

    /**
     * 主要是为了支持某些框架中的具名参数
     * @param condition 具体条件
     * @param keyValue 模式k1,v1,k2,v2...(k1,k2必须是String)
     */
    @Override
    public THIS addMapCondition(String condition, Object... keyValue){
        // 拼接参数
        addWhere(condition);

        //添加map类型参数k1,v1,k2,v2...
        addMapParams(keyValue);

        return myself();
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
    public <T> THIS addIn(String what , List<T> ins){
        if(CollectionUtil.isEmpty(ins)){
            return myself();
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

        return myself();
    }

    ///////////////////////////////////7.addOrderProperty方法,添加 ORDER BY 子句//////////////////////////////////

    /**
     * 拼接order by子句
     * @param propertyName 参与排序的属性名
     * @param asc true表示升序，false表示降序
     */
    @Override
    public THIS addOrderProperty(String propertyName, boolean asc){
        if(getOrderByClause().length() == 0){
            String orderBy = leftRightBlankWithCase(SqlKeyword.ORDER_BY.getKeyword());
            getOrderByClause().append(orderBy);
        } else{
            getOrderByClause().append(COMMA);
        }
        String ascStr = leftRightBlankWithCase(SqlKeyword.ASC.getKeyword());
        String descStr = leftRightBlankWithCase(SqlKeyword.DESC.getKeyword());
        getOrderByClause().append(propertyName).append(asc ? ascStr : descStr);
        return myself();
    }

    ///////////////////////////////////8.addGroupProperty方法,添加 GROUP BY 子句//////////////////////////////////

    /**
     * 添加GROUP BY子句
     * @param groupByName group by
     */
    @Override
    public THIS addGroupProperty(String groupByName){
        if(getGroupByClause().length() == 0){
            String groupBy = leftRightBlankWithCase(SqlKeyword.GROUP_BY.getKeyword());
            getGroupByClause().append(groupBy).append(groupByName);
        } else{
            getGroupByClause().append(COMMA).append(groupByName);
        }
        return myself();
    }

    ///////////////////////////////////9.addHaving方法,添加 HAVING 子句//////////////////////////////////

    /**
     * @param having having子句
     * @param params 参数
     */
    @Override
    public THIS addHaving(String having , Object... params){
        if(getHavingClause().length() == 0){
            String hav = leftRightBlankWithCase(SqlKeyword.HAVING.getKeyword());
            getHavingClause().append(hav).append(having);
        } else{
            String and = leftRightBlankWithCase(SqlKeyword.AND.getKeyword());
            getHavingClause().append(and).append(having);
        }

        addParams(params);

        return myself();
    }

    /**
     * 主要是为了支持某些框架中的具名参数
     * @param having having子句
     * @param keyValue 模式k1,v1,k2,v2...
     */
    @Override
    public THIS addMapHaving(String having, Object... keyValue){
        // 拼接having
        addHaving(having);

        //增加map参数
        addMapParams(keyValue);

        return myself();
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
    @Override
    public THIS paging(int pageNumber, int pageSize) {
        if(pageNumber <= 0){
            throw new IllegalArgumentException("pageNumber must be greater than 0 , but " + pageNumber);
        }
        if(pageSize <= 0){
            throw new IllegalArgumentException("pageSize must be greater than 0 , but " + pageSize);
        }

        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        return myself();
    }

    ///////////////////////////////////10.get相关方法,获取到组装的SQL语句，可以处理和不处理参数//////////////////////////////////

    /**
     * 获取 select
     */
    @Override
    public String getSelect(){
        return this.select;
    }
    /**
     * From后面的所有语句 , 没有处理 ?
     * @see AbstractQueryBuilder#getSqlExceptSelect()
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
        return builder.toString();
    }
    /**
     * 获取最终拼装的SQL , 没有处理 ?
     * @see AbstractQueryBuilder#getSql()
     */
    @Override
    public String getSqlWithoutPadding(){
        if(-1 == pageNumber){
            return select + getSqlExceptSelectWithoutPadding();
        }

        return sqlWithPage(select , getSqlExceptSelectWithoutPadding() , pageNumber , pageSize);
    }

    /**
     * 专门处理分页参数，返回处理完的SQL
     */
    protected abstract String sqlWithPage(String select , String sqlExceptSelectWithoutPadding , int pageNumber , int pageSize);

    /**
     * 获取生成的用于查询总记录数的SQL语句 , 没有处理 ?
     * @see AbstractQueryBuilder#getCountQuerySql()
     * @see AbstractQueryBuilder#getSqlWithoutPadding()
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
     * @see AbstractQueryBuilder#addCondition(String, Object...)
     * @see AbstractQueryBuilder#and(String, Object...)
     * @see AbstractQueryBuilder#addMapHaving(String, Object...)
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
     * @see AbstractQueryBuilder#addCondition(String, Object...)
     * @see AbstractQueryBuilder#and(String, Object...)
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
     * @see AbstractQueryBuilder#addMapCondition(String, Object...)
     * @see AbstractQueryBuilder#addMapHaving(String, Object...)
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

    protected String leftBlankWithCase(String word){
        String leftBlank = leftBlank(word);
        return isUpper ? leftBlank.toUpperCase() : leftBlank.toLowerCase();
    }
    protected String rightBlankWithCase(String word){
        String rightBlank = rightBlank(word);
        return isUpper ? rightBlank.toUpperCase() : rightBlank.toLowerCase();
    }
    protected String leftRightBlankWithCase(String word){
        String leftRightBlank = leftRightBlank(word);
        return isUpper ? leftRightBlank.toUpperCase() : leftRightBlank.toLowerCase();
    }

    public THIS setSelectClause(String select) {
        this.select = select;
        return myself();
    }

    public THIS setFromClause(String fromClause) {
        this.fromClause = new StringBuilder(fromClause);
        return myself();
    }

    @Override
    public String toString() {
        return getSqlWithoutPadding();
    }
}
