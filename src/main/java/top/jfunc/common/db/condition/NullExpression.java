package top.jfunc.common.db.condition;

/**
 * @author chenzhaoju
 */
public class NullExpression extends AbstractCriterion implements Criterion {

    private final String propertyName;

    protected NullExpression(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toMybatisSql() {
        return toString();
    }

    @Override
    public String toJdbcSql() {
        return toString();
    }

    @Override
    public String toString() {
        return propertyName + " is null";
    }
}
