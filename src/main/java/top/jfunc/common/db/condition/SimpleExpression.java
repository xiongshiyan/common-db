package top.jfunc.common.db.condition;

/**
 *
 * @author chenzhaoju
 * @author yangjian
 */
public class SimpleExpression extends AbstractCriterion implements Criterion {
    private final String propertyName;
    private final Object value;
    private final String op;

    protected SimpleExpression(String propertyName, Object value, String op) {
        this.propertyName = propertyName;
        this.value = value;
        this.op = op;
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
        return this.propertyName + getOp() + "#{" + getParameterName(this.propertyName) + "} ";
    }

    @Override
    public String toJdbcSql() {
        return this.propertyName + getOp() + " ?";
    }

    @Override
    public String toString() {
        return propertyName + getOp() + value;
    }
}
