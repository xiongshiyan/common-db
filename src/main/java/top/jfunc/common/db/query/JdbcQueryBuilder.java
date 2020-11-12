package top.jfunc.common.db.query;

import top.jfunc.common.utils.ArrayUtil;
import java.util.*;

/**
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class JdbcQueryBuilder extends AbstractQueryBuilder<JdbcQueryBuilder>{
    /**
     * 参数列表
     */
    protected List<Object> parameters;

    public JdbcQueryBuilder(){
        super();
    }

    public JdbcQueryBuilder(String select, String tableName, String alias){
        super(select, tableName, alias);
    }
    public JdbcQueryBuilder(String select, String... froms){
        super(select, froms);
    }

    @Override
    protected void addParams(Object... params) {
        if(ArrayUtil.isEmpty(params)){
            return;
        }
        if(null == parameters){
            parameters = new LinkedList<>();
        }
        parameters.addAll(Arrays.asList(params));
    }
    /**
     * 获取SQL中的参数值列表，List返回
     */
    @SuppressWarnings("unchecked")
    public List<Object> getListParameters(){
        return (List<Object>) getParameters();
    }

    /**
     * 获取SQL中的参数值列表，Array返回
     */
    public Object[] getArrayParameters(){
        List<Object> listParameters = getListParameters();
        return listParameters.toArray(new Object[listParameters.size()]);
    }


    @SuppressWarnings("unchecked")
    @Override
    public Object getParameters() {
        if(null == parameters){
            return new LinkedList<>();
        }
        return parameters;
    }

    /**
     * 处理参数，处理了?和map类型的参数
     * @param sql sql
     * @return sql
     */
    @Override
    public String paddingParam(String sql) {
        //处理问号
        return SqlUtil.paddingParam(sql, getListParameters());
    }
}
