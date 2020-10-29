package top.jfunc.common.db.condition;

import java.util.*;
import java.util.function.Function;

/**
 * @author chenzhaoju
 */
public class Junction extends AbstractCriterion implements Criterion {
    private final Nature nature;
    private final List<Criterion> conditions = new ArrayList<Criterion>();

    protected Junction(Nature nature) {
        this.nature = nature;
    }

    protected Junction(Nature nature, Criterion... criterion) {
        this(nature);
        Collections.addAll(conditions, criterion);
        for (Criterion c : criterion) {
            super.parameterMap.putAll(c.getParameterMap());
            super.parameters.addAll(c.getParameters());
        }
    }

    protected Junction(Nature nature, Collection<Criterion> criterions) {
        this(nature);
        this.conditions.addAll(criterions);
        for (Criterion c : criterions) {
            super.parameterMap.putAll(c.getParameterMap());
            super.parameters.addAll(c.getParameters());
        }
    }

    public Junction add(Criterion criterion) {
        conditions.add(criterion);
        return this;
    }

    public Nature getNature() {
        return nature;
    }

    public List<Criterion> conditions() {
        return conditions;
    }

    @Override
    public String toMybatisSql() {
        return toSQL(Criterion::toMybatisSql);
    }

    @Override
    public String toJdbcSql() {
        return toSQL(Criterion::toJdbcSql);
    }

    private String toSQL(Function<Criterion, String> function){
        if (conditions.size() == 0) {
            return "1=1";
        }

        final StringBuilder buffer = new StringBuilder();
        if(1 < conditions.size()){
            buffer.append('(');
        }
        final Iterator<Criterion> itr = conditions.iterator();
        while (itr.hasNext()) {
            buffer.append(function.apply(itr.next()));
            if (itr.hasNext()) {
                buffer.append(' ')
                        .append(nature.getOperator())
                        .append(' ');
            }
        }
        if(1 < conditions.size()){
            buffer.append(')');
        }
        return buffer.toString();
    }

    /**
     * The type of junction
     */
    public enum Nature {
        /**
         * An AND
         */
        AND,
        /**
         * An OR
         */
        OR;

        /**
         * The corresponding SQL operator
         *
         * @return SQL operator
         */
        public String getOperator() {
            return name().toUpperCase();
        }
    }

}
