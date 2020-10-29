package top.jfunc.common.db.condition;

import top.jfunc.common.db.query.SqlUtil;

/**
 * @author xiongshiyan
 * @author chenzhaoju
 * @author yangjian
 */
public class SimpleExpression extends AbstractCriterion implements Criterion {
    private final String propertyName;
    private final Object value;
    private final String op;

    protected SimpleExpression(String propertyName, Object value, Op op) {
        this.propertyName = propertyName;
        this.value = value;
        this.op = op.getSeperator();
        addParameter(propertyName,value);
    }

    protected final String getOp() {
        return op;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toMybatisSql(){
        return this.propertyName + SqlUtil.leftRightBlank(getOp()) + "#{" + getParameterName(this.propertyName) + "} ";
    }

    @Override
    public String toJdbcSql() {
        return this.propertyName + SqlUtil.leftRightBlank(getOp()) + "?";
    }

    @Override
    public String toString() {
        return propertyName + SqlUtil.leftRightBlank(getOp()) + value;
    }
}
