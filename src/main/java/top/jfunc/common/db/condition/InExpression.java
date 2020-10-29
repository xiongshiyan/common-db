package top.jfunc.common.db.condition;

import top.jfunc.common.utils.ArrayUtil;
import top.jfunc.common.utils.Joiner;
import top.jfunc.common.utils.StrUtil;

import java.util.Arrays;

/**
 * @author chenzhaoju
 */
public class InExpression  extends AbstractCriterion  implements Criterion {

    private final String propertyName;
    private final Object[] values;

    protected InExpression(String propertyName, Object[] values) {
        if(ArrayUtil.isEmpty(values)){
            throw new RuntimeException("条件为空");
        }
        this.propertyName = propertyName;
        this.values = values;
        for (int i = 0; i < this.values.length; i++) {
            Object value = this.values[i];
            addParameter(this.propertyName + "_" + i,value);
        }
    }

    @Override
    public String toMybatisSql() {
        StringBuilder fragment = new StringBuilder();
        fragment.append( this.propertyName);
        fragment.append( " IN (" );
        for (int i = 0; i < this.values.length; i++) {
            if(0 < i){
                fragment.append(" , ");
            }
            fragment.append(" #{");
            fragment.append(getParameterName(this.propertyName + "_" + i));
            fragment.append(" }");
        }
        fragment.append(" ) ");
        return fragment.toString();
    }


    @Override
    public String toJdbcSql() {
        String[] questions = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            questions[i] = StrUtil.QUESTION_MARK;
        }
        String s = Joiner.on(StrUtil.COMMA).join(questions);
        return this.propertyName + " IN (" + s +  ")";
    }

    @Override
    public String toString() {
        return propertyName + " IN (" + Arrays.asList(values) + ')';
    }
}
