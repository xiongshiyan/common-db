package top.jfunc.common.db.condition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenzhaoju
 * @author yangjian
 */
public abstract class AbstractCriterion implements Criterion {

    protected Map<String,Object> parameterMap = new HashMap<>();
    protected List<Object> parameters = new ArrayList<>();

    @Override
    public Map<String, Object> getParameterMap() {
        return this.parameterMap;
    }

    @Override
    public List<Object> getParameters() {
        return parameters;
    }


    protected AbstractCriterion addParameter(String parameterName, Object value){
        this.parameterMap.put(getParameterName(parameterName), value);
        this.parameters.add(value);
        return this;
    }

    protected String getParameterName(String parameterName){
        return this.getClass().getSimpleName() + "_" + parameterName + "_" + hashCode();
    }
}
