package top.jfunc.common.db.condition;

/**
 * @author chenzhaoju
 */
public class BetweenExpression extends AbstractCriterion implements Criterion {

    private final String propertyName;
    private final Object lo;
    private final Object hi;

    protected BetweenExpression(String propertyName, Object lo, Object hi) {
        this.propertyName = propertyName;
        this.lo = lo;
        this.hi = hi;
        addParameter(propertyName+"_lo" ,this.lo);
        addParameter(propertyName+"_hi" ,this.hi);
    }

    @Override
    public String toMybatisSql() {
        return this.propertyName + " BETWEEN #{" + getParameterName(propertyName + "_lo") + "} " + " AND #{" + getParameterName(propertyName + "_hi") + "} ";
    }

    @Override
    public String toJdbcSql() {
        return this.propertyName + " BETWEEN ? AND ?";
    }

    @Override
    public String toString() {
        return propertyName + " BETWEEN " + lo + " AND " + hi;
    }
}
