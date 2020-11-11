package top.jfunc.common.db.query;

import top.jfunc.common.db.condition.Criterion;
import java.util.*;

/**
 * 对具名参数的支持
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class NamedQueryBuilder extends CommonQueryBuilder<NamedQueryBuilder> implements QueryBuilder{
    /**
     * map类型的参数
     */
    protected Map<String , Object> mapParameters;

    public NamedQueryBuilder(){}

    public NamedQueryBuilder(String select, String tableName, String alias){
        super(select, tableName, alias);
    }
    public NamedQueryBuilder(String select, String... froms){
        super(select, froms);
    }

    @Override
    protected void addParams(Object... keyValue) {
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

    @Override
    public Object getParameters() {
        if(null == mapParameters){
            return new LinkedHashMap<>();
        }
        return mapParameters;
    }

    public Map<String, Object> getMapParameters() {
        if(null == mapParameters){
            return new LinkedHashMap<>();
        }
        return mapParameters;
    }

    @Override
    public Object[] getArrayParameters() {
        throw new UnsupportedOperationException("具名参数builder不允许获取此类参数");
    }

    @Override
    public List<Object> getListParameters() {
        throw new UnsupportedOperationException("具名参数builder不允许获取此类参数");
    }

    @Override
    public String paddingParam(String sql) {
        return SqlUtil.paddingParam(sql , getMapParameters());
    }
}
