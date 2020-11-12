package top.jfunc.common.db.query;

import top.jfunc.common.ChainCall;
import top.jfunc.common.db.condition.Criterion;
import top.jfunc.common.db.page.MySqlPageBuilder;
import top.jfunc.common.db.page.PageBuilder;
import top.jfunc.common.utils.CollectionUtil;
import top.jfunc.common.utils.Joiner;
import top.jfunc.common.utils.StrUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static top.jfunc.common.db.query.SqlUtil.*;

/**
 * SQL的模式
 *   SELECT .. FROM .. (LEFT|RIGHT|INNER) JOIN .. ON .. WHERE .... GROUP BY .. HAVING .. ORDER BY
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractQueryBuilder<THIS extends AbstractQueryBuilder> implements QueryBuilder, ChainCall<THIS> {
    /**
     * select子句
     */
    protected String       selectClause;
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
     * 默认-1表示没有分页参数
     */
    protected int pageNumber = -1;
    protected int pageSize = 10;

    private static PageBuilder DEFAULT_PAGE_BUILDER = MySqlPageBuilder.getInstance();
    /**
     * 全局设置pageBuilder
     * @param pageBuilder pageBuilder
     */
    public static void initDefaultPageBuilder(PageBuilder pageBuilder){
        DEFAULT_PAGE_BUILDER = pageBuilder;
    }
    /**
     * 分页参数处理器，默认是mysql的
     */
    protected PageBuilder pageBuilder = DEFAULT_PAGE_BUILDER;
    /**
     * 设置此pageBuilder
     * @param pageBuilder pageBuilder
     * @return this
     */
    public THIS setPageBuilder(PageBuilder pageBuilder) {
        this.pageBuilder = pageBuilder;
        return myself();
    }

    public PageBuilder getPageBuilder() {
        return pageBuilder;
    }

    //////////////////////////////////////1.构造方法,确定基本的表和查询字段/////////////////////////////////////

    public AbstractQueryBuilder(){}

    /**
     * 用于一张表的情况，生成From子句
     * from topic t
     */
    public AbstractQueryBuilder(String select, String tableName, String alias){
        this.selectClause = addSelectIfNecessary(select);
        fromClause.append(rightBlank(SqlKeyword.FROM.getKeyword())).append(tableName).append(BLANK).append(alias);
    }

    /**
     * 用于两张表联合的情况，生成From子句，类似from table1 a,table2 b 然后添加where条件
     * 另外像left join 这种可以写在一个from字句中或者使用 leftJoin rightJoin innerJoin方法
     */
    public AbstractQueryBuilder(String select, String... froms){
        this.selectClause = addSelectIfNecessary(select);
        String prefix = rightBlank(SqlKeyword.FROM.getKeyword());
        //if(INCLUDE_FROM.matcher(froms[0]).matches()){
        //去除空格取前5个[from ]
        if(startsWith(froms[0] , prefix)){
            prefix = BLANK ;
        }
        fromClause.append(prefix).append(Joiner.on(COMMA).join(froms));
    }
    private String addSelectIfNecessary(String select) {
        //if(INCLUDE_SELECT.matcher(select).matches()){
        //去除空格取前6个[select ]
        String selectRightBlank = rightBlank(SqlKeyword.SELECT.getKeyword());
        if(startsWith(select , selectRightBlank)){
            //包含了select
            return select;
        }else {
            //没有包含select
            return selectRightBlank + select;
        }
    }

    public THIS setSelectClause(CharSequence selectClause) {
        this.selectClause = selectClause.toString();
        return myself();
    }

    public THIS setFromClause(CharSequence fromClause) {
        this.fromClause = new StringBuilder(fromClause);
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
        String leftJoin = leftRightBlank(SqlKeyword.LEFT_JOIN.getKeyword());
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
        String rightJoin = leftRightBlank(SqlKeyword.RIGHT_JOIN.getKeyword());
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
        String innerJoin = leftRightBlank(SqlKeyword.INNER_JOIN.getKeyword());
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
        String on = leftRightBlank(SqlKeyword.ON.getKeyword());
        fromClause.append(on).append(onClause);
        return myself();
    }

    ////////////////////////////3.addCondition/and/andIf 方法,添加条件,多个用 AND 连接////////////////////////////

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
        addWhereAndCondition(condition);
        // 添加参数
        addParams(params);
        return myself();
    }

    @Override
    public THIS addCondition(boolean append, String condition, Object... params) {
        if(append){
            addCondition(condition, params);
        }
        return myself();
    }

    @Override
    public THIS addCondition(Criterion criterion) {
        addCondition(criterion.toJdbcSql(), criterion.getParameters().toArray());
        return myself();
    }

    @Override
    public THIS and(String condition, Object... params) {
        return addCondition(condition , params);
    }

    @Override
    public THIS and(boolean append, String condition, Object... params) {
        if(append){
            addCondition(condition, params);
        }
        return myself();
    }

    ////////////////////////////4.or/orNew/orIf/orNewIf方法,添加条件,多个用 OR 连接////////////////////////////

    /**
     * 添加 OR 子句
     */
    @Override
    public THIS or(String condition, Object... params){
        // 添加OR子句
        addOrCondition(condition);
        // 添加参数
        addParams(params);
        return myself();
    }

    @Override
    public THIS or(boolean append, String condition, Object... params) {
        if(append){
            or(condition, params);
        }
        return myself();
    }

    protected void addOrCondition(String condition) {
        //OR 子句一般来说不是第一个，所以此时肯定存在了 WHERE
        int index = whereClause.toString().toUpperCase().indexOf(SqlKeyword.WHERE.getKeyword());
        if(-1 == index){
            throw new RuntimeException("还没有条件，不能使用or方法");
        }

        String or = leftRightBlank(SqlKeyword.OR.getKeyword());
        whereClause.append(or).append(condition);
    }

    @Override
    public THIS orNew(String condition, Object... params){
        addOrNewCondition(condition);
        addParams(params);
        return myself();
    }

    @Override
    public THIS orNew(boolean append, String condition, Object... params) {
        if(append){
            orNew(condition, params);
        }
        return myself();
    }

    protected void addOrNewCondition(String condition) {
        //找到where
        int index = whereClause.toString().toUpperCase().indexOf(SqlKeyword.WHERE.getKeyword());
        if(-1 == index){
            throw new RuntimeException("还没有条件，不能使用orNew方法");
        }
        //以前的条件使用()包裹起来
        whereClause.insert(index + 6, LEFT_BRAKET).append(RIGHT_BRAKET);
        whereClause.append(leftRightBlank(SqlKeyword.OR.getKeyword()))
                .append(LEFT_BRAKET).append(condition).append(RIGHT_BRAKET);
    }

    /**
     * 增加 where ，如果存在就添加 and
     */
    protected void addWhere() {
        if(whereClause.length() == 0){
            String where = rightBlank(SqlKeyword.WHERE.getKeyword());
            whereClause.append(where);
        } else{
            String and = leftRightBlank(SqlKeyword.AND.getKeyword());
            whereClause.append(and);
        }
    }
    protected void addWhereAndCondition(String condition) {
        addWhere();
        whereClause.append(condition);
    }

    ///////////////////////////////////6.addIn/addInIf/addNotIn方法,添加 IN 条件/////////////////////////////////////////////

    @Override
    public <T> THIS addIn(String what , List<T> ins){
        if(CollectionUtil.isEmpty(ins)){
            return myself();
        }

        inNotInClause(what, SqlKeyword.IN , ins);

        return myself();
    }

    @Override
    public <T> THIS addIn(String what, T... ins) {
        return addIn(what , Arrays.asList(ins));
    }

    @Override
    public <T> THIS addNotIn(String what, List<T> ins) {
        if(CollectionUtil.isEmpty(ins)){
            return myself();
        }

        inNotInClause(what, SqlKeyword.NOT_IN , ins);

        return myself();
    }

    @Override
    public <T> THIS addNotIn(String what, T... ins) {
        return addNotIn(what , Arrays.asList(ins));
    }

    /**
     * in (...)  或者 not in (...)
     * @param sqlKeyword IN | NOT IN
     */
    private <T> void inNotInClause(String what, SqlKeyword sqlKeyword , List<T> ins) {
        // 拼接where
        addWhere();
        // 添加左括号
        String in = leftRightBlank(sqlKeyword.getKeyword());
        whereClause.append(what).append(in).append(LEFT_BRAKET);

        ///
        /*for(Object part : ins){
            //数字不需要'' , 其他就转化为字符串并加上''
            String x = part instanceof Number ? part.toString() : QUOTE + part + QUOTE;
            whereClause.append(x).append(COMMA);
        }
        // 去掉最后的 ,
        whereClause = new StringBuilder(whereClause.substring(0 , whereClause.lastIndexOf(COMMA)));*/

        List<String> parts = new ArrayList<>(ins.size());
        ins.forEach(ele->parts.add(StrUtil.QUESTION_MARK));
        String questions = Joiner.on(StrUtil.COMMA).join(parts);
        whereClause.append(questions);
        //添加右括号
        whereClause.append(RIGHT_BRAKET);

        addParams(ins.toArray());
    }

    @Override
    public THIS in(String what, String inSubQuery) {
        addWhere();
        whereClause.append(what).append(leftRightBlank(SqlKeyword.IN.getKeyword()))
                .append(LEFT_BRAKET).append(inSubQuery).append(RIGHT_BRAKET);
        return myself();
    }

    @Override
    public THIS notIn(String what, String inSubQuery) {
        addWhere();
        whereClause.append(what).append(leftRightBlank(SqlKeyword.NOT_IN.getKeyword()))
                .append(LEFT_BRAKET).append(inSubQuery).append(RIGHT_BRAKET);
        return myself();
    }

    @Override
    public THIS exists(String sql) {
        addWhereAndCondition(middleBlank(SqlKeyword.EXISTS.getKeyword() , LEFT_BRAKET + sql + RIGHT_BRAKET));
        return myself();
    }
    @Override
    public THIS notExists(String sql) {
        addWhereAndCondition(middleBlank(SqlKeyword.NOT_EXISTS.getKeyword() , LEFT_BRAKET + sql + RIGHT_BRAKET));
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
            String orderBy = leftRightBlank(SqlKeyword.ORDER_BY.getKeyword());
            getOrderByClause().append(orderBy);
        } else{
            getOrderByClause().append(COMMA);
        }
        String ascStr = leftRightBlank(SqlKeyword.ASC.getKeyword());
        String descStr = leftRightBlank(SqlKeyword.DESC.getKeyword());
        getOrderByClause().append(propertyName).append(asc ? ascStr : descStr);
        return myself();
    }

    @Override
    public THIS addAscOrderProperty(String propertyName) {
        return addOrderProperty(propertyName , ASC);
    }

    @Override
    public THIS addAscOrderProperty(String... propertyNames) {
        for (String propertyName : propertyNames) {
            addAscOrderProperty(propertyName);
        }
        return myself();
    }

    @Override
    public THIS addDescOrderProperty(String propertyName) {
        return addOrderProperty(propertyName , DESC);
    }

    @Override
    public THIS addDescOrderProperty(String... propertyNames) {
        for (String propertyName : propertyNames) {
            addDescOrderProperty(propertyName);
        }
        return myself();
    }

    @Override
    public THIS addOrderProperty(boolean append, String propertyName, boolean asc) {
        if(append){
            addOrderProperty(propertyName, asc);
        }
        return myself();
    }

    @Override
    public THIS addAscOrderProperty(boolean append, String propertyName) {
        if(append){
            addOrderProperty(propertyName, ASC);
        }
        return myself();
    }

    @Override
    public THIS addDescOrderProperty(boolean append, String propertyName) {
        if(append){
            addOrderProperty(propertyName , DESC);
        }
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
            String groupBy = rightBlank(SqlKeyword.GROUP_BY.getKeyword());
            getGroupByClause().append(groupBy).append(groupByName);
        } else{
            getGroupByClause().append(COMMA).append(groupByName);
        }
        return myself();
    }

    @Override
    public THIS addGroupProperty(String... groupByNames) {
        for (String groupByName : groupByNames) {
            addGroupProperty(groupByName);
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
        addHavingClause(having);
        addParams(params);
        return myself();
    }

    @Override
    public THIS addHaving(boolean append, String having, Object... params) {
        if(append){
            addHaving(having , params);
        }
        return myself();
    }

    protected void addHavingClause(String having) {
        if(getHavingClause().length() == 0){
            String hav = rightBlank(SqlKeyword.HAVING.getKeyword());
            getHavingClause().append(hav).append(having);
        } else{
            String and = leftRightBlank(SqlKeyword.AND.getKeyword());
            getHavingClause().append(and).append(having);
        }
    }

    /**
     * 添加参数
     * @param params 参数
     */
    protected abstract void addParams(Object... params);

    @Override
    public THIS page(int pageNumber, int pageSize) {
        if(pageNumber<=0){
            throw new IllegalArgumentException("pageNumber must >= 1");
        }
        if(pageSize<=0){
            throw new IllegalArgumentException("pageSize must >= 1");
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
        return selectClause;
    }

    /**
     * From后面的所有语句 , 没有处理 ?
     * @see THIS#getSqlExceptSelect()
     */
    @Override
    public String getSqlExceptSelectWithoutPadding(){
        StringBuilder builder = new StringBuilder(middleBlank(fromClause.toString() , whereClause.toString()).trim());
        if(null != groupByClause){
            builder.append(leftBlank(groupByClause.toString()));
        }
        if(null != havingClause){
            builder.append(leftBlank(havingClause.toString()));
        }
        if(null != orderByClause){
            builder.append(leftBlank(orderByClause.toString()));
        }
        return leftBlank(builder.toString());
    }
    /**
     * 获取最终拼装的SQL , 没有处理 ?
     * @see THIS#getSql()
     */
    @Override
    public String getSqlWithoutPadding(){
        //没有设置分页参数的情况下
        if(-1 == pageNumber){
            String sql = middleBlank(selectClause , getSqlExceptSelectWithoutPadding());
            return sql.trim();
        }

        return pageBuilder.sqlWithPage(selectClause , getSqlExceptSelectWithoutPadding() , pageNumber , pageSize);
    }

    /**
     * 获取生成的用于查询总记录数的SQL语句 , 没有处理 ?
     * @see THIS#getCountQuerySql()
     * @see THIS#getSqlWithoutPadding()
     */
    @Override
    public String getCountQuerySqlWithoutPadding(){
        StringBuilder builder = new StringBuilder(SqlKeyword.SELECT.getKeyword()).append(" COUNT(*) AS totalRow ").append(middleBlank(fromClause.toString() , whereClause.toString()).trim());
        if(null != groupByClause){
            builder.append(leftBlank(groupByClause.toString()));
        }
        if(null != havingClause){
            builder.append(leftBlank(havingClause.toString()));
        }
        return builder.toString();
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

    protected String leftBlank(String word){
        return SqlUtil.leftBlank(word);
    }
    protected String rightBlank(String word){
        return SqlUtil.rightBlank(word);
    }
    protected String middleBlank(String word1, String word2){
        return SqlUtil.middleBlank(word1, word2);
    }
    protected String leftRightBlank(String word){
        return SqlUtil.leftRightBlank(word);
    }

    @Override
    public String toString() {
        return getSqlWithoutPadding();
    }
}
