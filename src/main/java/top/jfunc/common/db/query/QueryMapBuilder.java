package top.jfunc.common.db.query;

import java.util.Map;


/**
 * @author xiongshiyan
 */
public interface QueryMapBuilder extends QueryBuilder {
    public QueryMapBuilder addMapCondition(String condition, Object... keyValue);
    default QueryMapBuilder addMapCondition(boolean append, String condition, Object... keyValue){
        if(append){
            addMapCondition(condition, keyValue);
        }
        return this;
    }
    public QueryBuilder addMapHaving(String having, Object... keyValue);
    default QueryBuilder addMapHaving(boolean append, String having, Object... keyValue){
        if(append){
            addMapHaving(having , keyValue);
        }
        return this;
    }
    public Map<String, Object> getMapParameters();
}
