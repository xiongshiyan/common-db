package top.jfunc.common.db.query;

import java.util.Map;


/**
 * 新增key-value类型的参数
 * @author xiongshiyan
 */
public interface QueryMapBuilder extends QueryBuilder {
    /**
     * @param condition 具体条件
     * @param keyValue 参数
     */
    QueryMapBuilder addMapCondition(String condition, Object... keyValue);
    default QueryMapBuilder addMapCondition(boolean append, String condition, Object... keyValue){
        if(append){
            addMapCondition(condition, keyValue);
        }
        return this;
    }

    /**
     * 增加map类型的having子句
     */
    QueryBuilder addMapHaving(String having, Object... keyValue);
    default QueryBuilder addMapHaving(boolean append, String having, Object... keyValue){
        if(append){
            addMapHaving(having , keyValue);
        }
        return this;
    }
    Map<String, Object> getMapParameters();
}
