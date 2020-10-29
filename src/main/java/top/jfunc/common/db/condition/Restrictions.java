package top.jfunc.common.db.condition;

import top.jfunc.common.db.query.SqlKeyword;

import java.util.Collection;

/**
 * @author chenzhaoju
 */
public class Restrictions {

    public static SimpleExpression eq(String propertyName, Object value) {
        return new SimpleExpression( propertyName, value, Op.EQ );
    }

    /*public static Criterion eqOrIsNull(String propertyName, Object value) {
        return value == null ? isNull( propertyName ) : eq( propertyName, value );
    }*/

    public static SimpleExpression ne(String propertyName, Object value) {
        return new SimpleExpression( propertyName, value, Op.NE );
    }

    /*public static Criterion neOrIsNotNull(String propertyName, Object value) {
        return value == null ? isNotNull( propertyName ) : ne( propertyName, value );
    }*/

    public static SimpleExpression like(String propertyName, String value) {
        return new SimpleExpression( propertyName, value, Op.LIKE );
    }

    public static SimpleExpression like(String propertyName, String value, MatchMode matchMode) {
        return new SimpleExpression( propertyName, matchMode.toMatchString( value ), Op.LIKE );
    }

    public static SimpleExpression gt(String propertyName, Object value) {
        return new SimpleExpression( propertyName, value, Op.GT);
    }


    public static SimpleExpression lt(String propertyName, Object value) {
        return new SimpleExpression( propertyName, value, Op.LT );
    }


    public static SimpleExpression le(String propertyName, Object value) {
        return new SimpleExpression( propertyName, value, Op.LE );
    }

    public static SimpleExpression ge(String propertyName, Object value) {
        return new SimpleExpression( propertyName, value, Op.GE );
    }


    public static Criterion between(String propertyName, Object lo, Object hi) {
        return new BetweenExpression( propertyName, lo, hi );
    }


    public static Criterion in(String propertyName, Object[] values) {
        return new InExpression( propertyName, values );
    }


    public static Criterion in(String propertyName, Collection values) {
        return new InExpression( propertyName, values.toArray() );
    }

    public static Criterion isNull(String propertyName) {
        return new NullExpression( propertyName );
    }


    public static Criterion isNotNull(String propertyName) {
        return new NotNullExpression( propertyName );
    }


    public static Criterion mapped(String propertyName, Op op, String key, Object value){
        return new MappedExpression(propertyName, op, key, value);
    }

    public static Conjunction and(Criterion... predicates) {
        return conjunction( predicates );
    }

    public static Conjunction and(Collection<Criterion> predicates) {
        return conjunction(predicates);
    }


    public static Disjunction or(Criterion... predicates) {
        return disjunction( predicates );
    }

    public static Criterion logicAnd(Criterion lhs, Criterion rhs) {
        return new LogicalExpression(lhs, rhs, SqlKeyword.AND.getKeyword());
    }
    public static Criterion logicOr(Criterion lhs, Criterion rhs) {
        return new LogicalExpression(lhs, rhs, SqlKeyword.OR.getKeyword());
    }

    /**
     * 自定义 SQL 查询
     * @param sql SQL 字符串
     * @param value 查询绑定值
     * @return 返回 Criterion 对象
     */
    public static Criterion sqlRestriction(String sql, Object value) {
        return new SimpleExpression( sql, value, Op.BLANK );
    }

    public static Conjunction conjunction() {
        return new Conjunction();
    }

    public static Conjunction conjunction(Criterion... conditions) {
        return new Conjunction( conditions );
    }

    public static Conjunction conjunction(Collection<Criterion> conditions) {
        return new Conjunction( conditions );
    }

    public static Disjunction disjunction() {
        return new Disjunction();
    }

    public static Disjunction disjunction(Criterion... conditions) {
        return new Disjunction( conditions );
    }

    protected Restrictions() {}

}
