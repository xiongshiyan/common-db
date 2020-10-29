package top.jfunc.common.db.condition;

import java.io.Serializable;
import java.util.*;

/**
 *
 * 查询条件
 *
 * @author chenzhaoju
 * @author yangjian
 */
public class Conditions implements Criterion,Serializable {

    private List<Criterion> criterions = new ArrayList<>();
    private Map<String,Object> parameterMap = new HashMap<>();
    private List<Object> parameters = new ArrayList<>();

    public List<Criterion> getCriterions() {
        return criterions;
    }

    public Conditions add(Criterion criterion) {
        this.criterions.add(criterion);
        this.parameterMap.putAll(criterion.getParameterMap());
        this.parameters.addAll(criterion.getParameters());
        return this;
    }

    @Override
    public String toMybatisSql() {
        return Restrictions.and(this.criterions).toMybatisSql();
    }

    @Override
    public String toJdbcSql() {
        return Restrictions.and(this.criterions).toJdbcSql();
    }

    @Override
    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    @Override
    public List<Object> getParameters() {
        return parameters;
    }

    public Object getParameterValue(String parameterName){
        return this.parameterMap.get(parameterName);
    }

    /* 以下是便捷条件操作 */

    public Conditions eq(String propertyName, Object value)
    {
        return add(Restrictions.eq(propertyName, value));
    }

    public Conditions ne(String propertyName, Object value)
    {
        return add(Restrictions.ne(propertyName, value));
    }

    public Conditions like(String propertyName, String value)
    {
        return add(Restrictions.like(propertyName, value));
    }

    public Conditions like(String propertyName, String value, MatchMode matchMode)
    {
        return add(Restrictions.like(propertyName, value, matchMode));
    }

    public Conditions gt(String propertyName, Object value)
    {
        return add(Restrictions.gt(propertyName, value));
    }


    public Conditions lt(String propertyName, Object value)
    {
        return add(Restrictions.lt(propertyName, value));
    }


    public Conditions le(String propertyName, Object value)
    {
        return add(Restrictions.le(propertyName, value));
    }

    public Conditions ge(String propertyName, Object value)
    {
        return add(Restrictions.ge(propertyName, value));
    }


    public Conditions between(String propertyName, Object lo, Object hi)
    {
        return add(Restrictions.between(propertyName, lo, hi));
    }


    public Conditions in(String propertyName, Object[] values)
    {
        return add(Restrictions.in(propertyName, values));
    }


    public Conditions in(String propertyName, Collection values)
    {
        return add(Restrictions.in(propertyName, values));
    }

    public Conditions isNull(String propertyName)
    {
        return add(Restrictions.isNull(propertyName));
    }


    public Conditions isNotNull(String propertyName)
    {
        return add(Restrictions.isNotNull(propertyName));
    }

    public Conditions sqlRestriction(String sql, Object value) {
        return add(Restrictions.sqlRestriction(sql, value));
    }

    public Conditions conjunction() {
        return add(Restrictions.conjunction());
    }

    public Conditions disjunction() {
        return add(Restrictions.disjunction());
    }
}
