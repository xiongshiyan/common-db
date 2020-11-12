package top.jfunc.common.db.query;

import top.jfunc.common.db.condition.Criterion;
import java.util.*;

/**
 * 对具名参数的支持
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class NamedQueryBuilder extends AbstractQueryBuilder<NamedQueryBuilder>{
    /**
     * map类型的参数
     */
    protected Map<String , Object> mapParameters;

    public NamedQueryBuilder(){super();}

    public NamedQueryBuilder(String select, String tableName, String alias){
        super(select, tableName, alias);
    }
    public NamedQueryBuilder(String select, String... froms){
        super(select, froms);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addParams(Object... keyValue) {
        if(null == mapParameters){
            mapParameters = new LinkedHashMap<>();
        }

        //只有一个参数并且是map
        if(keyValue.length == 1){
            Object o = keyValue[0];
            if(o instanceof Map){
                mapParameters.putAll((Map<String, Object>) o);
                return;
            }
        }


        if(0 != (keyValue.length % 2)){
            throw new IllegalArgumentException("参数必须符合模式k1,v1,k2,v2...");
        }
        int kvLen = keyValue.length / 2;
        for (int i = 0; i < kvLen; i++) {
            mapParameters.put(keyValue[i].toString() , keyValue[i+1]);
        }
    }

    /**
     * @see top.jfunc.common.db.condition.Conditions
     * @see top.jfunc.common.db.condition.MappedExpression
     */
    @Override
    public NamedQueryBuilder addCondition(Criterion criterion){
        addWhereAndCondition(criterion.toJdbcSql());

        if(null == mapParameters){
            mapParameters = new LinkedHashMap<>();
        }
        mapParameters.putAll(criterion.getParameterMap());
        return this;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getMapParameters() {
        return (Map<String, Object>) getParameters();
    }

    @Override
    public Object getParameters() {
        if(null == mapParameters){
            return new LinkedHashMap<>();
        }
        return mapParameters;
    }

    @Override
    public String paddingParam(String sql) {
        return SqlUtil.paddingParam(sql , getMapParameters());
    }
}
