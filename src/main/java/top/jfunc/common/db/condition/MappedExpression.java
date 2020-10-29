package top.jfunc.common.db.condition;

import top.jfunc.common.db.query.SqlUtil;
import top.jfunc.common.utils.StrUtil;

/**
 * 对具名参数的支持，形如 t.name=:name
 * @author xiongshiyan
 */
public class MappedExpression extends AbstractCriterion implements Criterion {

    private final String propertyName;
    private final String op;
    private final String key;

    public MappedExpression(String propertyName, Op op, String key, Object value) {
        this.propertyName = propertyName;
        this.op = op.getSeperator();
        this.key = key;
        this.parameterMap.put(key, value);
    }

    @Override
    public String toMybatisSql() {
        throw new UnsupportedOperationException("不支持具名参数");
    }

    public String getOp() {
        return op;
    }

    @Override
    public String toJdbcSql() {
        return toString();
    }

    @Override
    public String toString() {
        return propertyName + SqlUtil.leftRightBlank(getOp()) + StrUtil.COLON + key;
    }
}
