package top.jfunc.common.db.condition;

/**
 * @author chenzhaoju
 */
public class NotNullExpression  extends AbstractCriterion  implements Criterion {

    private final String propertyName;

    protected NotNullExpression(String propertyName) {
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
        return propertyName + " is not null";
    }
}
