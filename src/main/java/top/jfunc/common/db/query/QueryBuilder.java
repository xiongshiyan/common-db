package top.jfunc.common.db.query;

import top.jfunc.common.ChainCall;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * SQL的模式
 *   SELECT .. FROM .. (LEFT|RIGHT|INNER) JOIN .. ON .. WHERE .... GROUP BY .. HAVING .. ORDER BY .. LIMIT ..
 * @author xiongshiyan
 */
public interface QueryBuilder<THIS extends QueryBuilder> extends ChainCall<THIS>{
    boolean ASC = true;
    boolean DESC = false;

    /**
     * left join 子句
     * @param joinClause like tableName t
     * @param onClause like t1.id=t2.id
     * @return this
     */
    THIS leftJoin(String joinClause, String onClause);
    /**
     * left join 子句，配合{@link QueryBuilder#on(String)}子句使用
     * @param joinClause like tableName t
     * @return this
     */
    THIS leftJoin(String joinClause);
    /**
     * right join 子句
     * @param joinClause like tableName t
     * @param onClause like t1.id=t2.id
     * @return this
     */
    THIS rightJoin(String joinClause, String onClause);
    /**
     * right join 子句，配合{@link QueryBuilder#on(String)}子句使用
     * @param joinClause like tableName t
     * @return this
     */
    THIS rightJoin(String joinClause);
    /**
     * inner join 子句
     * @param joinClause like tableName t
     * @param onClause like t1.id=t2.id
     * @return this
     */
    THIS innerJoin(String joinClause, String onClause);
    /**
     * inner join 子句，配合{@link QueryBuilder#on(String)}子句使用
     * @param joinClause like tableName t
     * @return this
     */
    THIS innerJoin(String joinClause);
    /**
     * on子句，调用之前必须先调用
     * {@link QueryBuilder#leftJoin(String)}
     * 或者{@link QueryBuilder#rightJoin(String)}
     * 或者{@link QueryBuilder#innerJoin(String)}
     * @param onClause like t1.id=t2.id
     * @return this
     */
    THIS on(String onClause);
    /**
     * 增加条件add子句
     * @param condition like t1.id=?
     * @param params 参数
     * @return this
     */
    THIS addCondition(String condition, Object... params);
    /**
     * 根据条件决定是否添加条件
     * @see QueryBuilder#and(boolean, String, Object...)
     */
    default THIS addCondition(boolean append, String condition, Object... params){
        if(append){
            addCondition(condition, params);
        }
        return myself();
    }
    /**
     * @see QueryBuilder#addCondition(String, Object...)
     */
    default THIS and(String condition, Object... params){
        return addCondition(condition , params);
    }
    /**
     * 根据条件决定是否添加条件
     * @see QueryBuilder#addCondition(boolean, String, Object...)
     */
    default THIS and(boolean append, String condition, Object... params){
        if(append){
            and(condition, params);
        }
        return myself();
    }
    /**
     * 增加条件or子句
     * @param condition like t1.id=?
     * @param params 参数
     * @return this
     */
    THIS or(String condition, Object... params);
    /**
     * 根据条件决定是否添加条件
     * @see QueryBuilder#or(String, Object...)
     */
    default THIS or(boolean append, String condition, Object... params){
        if(append){
            or(condition, params);
        }
        return myself();
    }
    /**
     * addIn("d.id" , 1,2,3) - > d.id IN (1,2,3)
     * addIn("d.phone" , "1","2","3") - > d.id IN ('1','2','3')
     * @param what 添加 IN 语句
     * @param ins In条件
     * @return this
     */
    <T> THIS addIn(String what, List<T> ins);

    /**
     * @see QueryBuilder#addIn(String, List)
     */
    default <T> THIS addIn(String what, T... ins){
        return addIn(what , Arrays.asList(ins));
    }



    /**
     * @param condition 具体条件
     * @param keyValue 参数
     */
    THIS addMapCondition(String condition, Object... keyValue);
    default THIS addMapCondition(boolean append, String condition, Object... keyValue){
        if(append){
            addMapCondition(condition, keyValue);
        }
        return myself();
    }

    /**
     * 增加map类型的having子句
     */
    THIS addMapHaving(String having, Object... keyValue);
    default THIS addMapHaving(boolean append, String having, Object... keyValue){
        if(append){
            addMapHaving(having , keyValue);
        }
        return myself();
    }
    Map<String, Object> getMapParameters();



    /**
     * 增加order by子句
     * @param propertyName like t1.time
     * @param asc 升序还是降序
     * @return this
     */
    THIS addOrderProperty(String propertyName, boolean asc);

    default THIS addAscOrderProperty(String propertyName){
        return addOrderProperty(propertyName , ASC);
    }
    default THIS addDescOrderProperty(String propertyName){
        return addOrderProperty(propertyName , DESC);
    }

    /**
     * @param append 是否拼装这个排序
     * @param propertyName 排序属性
     * @param asc true表示升序，false表示降序
     */
    default THIS addOrderProperty(boolean append, String propertyName, boolean asc){
        if(append){
            addOrderProperty(propertyName, asc);
        }
        return myself();
    }
    default THIS addAscOrderProperty(boolean append, String propertyName){
        if(append){
            addOrderProperty(propertyName, ASC);
        }
        return myself();
    }
    default THIS addDescOrderProperty(boolean append, String propertyName){
        if(append){
            addOrderProperty(propertyName, DESC);
        }
        return myself();
    }
    /**
     * 增加group by子句
     * @param groupByName like t1.dept_id
     * @return this
     */
    THIS addGroupProperty(String groupByName);
    /**
     * 增加having子句
     * @param having like t1.ff=?
     * @param params 参数
     * @return this
     */
    THIS addHaving(String having, Object... params);
    /**
     * 是否添加此having子句
     * @see QueryBuilder#addHaving(String, Object...)
     */
    default THIS addHaving(boolean append, String having, Object... params){
        if(!append){
            return myself();
        }
        return addHaving(having , params);
    }
    /**
     * 增加分页条件
     * @param pageNumber pageNumber based on 1
     * @param pageSize pageSize
     * @return this
     */
    THIS page(int pageNumber, int pageSize);

    /**
     * 获取select子句
     */
    String getSelect();

    /**
     * 获取from之后的所有子句，可能包含 ?
     */
    String getSqlExceptSelectWithoutPadding();

    /**
     * 获取整个SQL语句，可能包含 ?
     */
    String getSqlWithoutPadding();

    /**
     * 获取计算count的SQL语句，可能包含 ?
     */
    String getCountQuerySqlWithoutPadding();

    /**
     * 获取list类型参数
     */
    List<Object> getListParameters();

    /**
     * 获取array类型参数
     */
    Object[] getArrayParameters();
    /**
     * From后面的所有语句 , 处理了 ? 参数的
     * @see QueryBuilder#getSqlExceptSelectWithoutPadding()
     */
    default String getSqlExceptSelect(){
        return paddingParam(getSqlExceptSelectWithoutPadding());
    }
    /**
     * 获取最终拼装的SQL , 并且处理了 ? 参数的
     * @see QueryBuilder#getSqlWithoutPadding
     */
    default String getSql(){
        return paddingParam(getSqlWithoutPadding());
    }
    /**
     * 获取生成的用于查询总记录数的SQL语句 , 并且处理了 ? 参数的
     * @see QueryBuilder#getCountQuerySqlWithoutPadding()
     */
    default String getCountQuerySql(){
        return paddingParam(getCountQuerySqlWithoutPadding());
    }
    /**
     * 处理参数，处理了?和map类型的参数
     * @param sql sql
     * @return sql
     */
    default String paddingParam(String sql) {
        //处理问号
        String handleQuote = SqlUtil.paddingParam(sql, getListParameters());
        //处理map参数
        return SqlUtil.paddingParam(handleQuote , getMapParameters());
    }
}
