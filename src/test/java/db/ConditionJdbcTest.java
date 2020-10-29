package db;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.db.condition.*;

/**
 * @author xiongshiyan at 2020/10/29 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ConditionJdbcTest {
    @Test
    public void testSimpleExpression1() {
        SimpleExpression eq = Restrictions.eq("count", 2);
        Assert.assertEquals("count = ?", eq.toJdbcSql());
        System.out.println(eq.getParameters());
    }

    @Test
    public void testSimpleExpression() {
        Conditions conditions = new Conditions();
        conditions.add(Restrictions.eq("count", 2));
        conditions.add(Restrictions.eq("name", "名字"));
        conditions.add(Restrictions.ge("age", 18));
        conditions.add(Restrictions.lt("age", 30));
        conditions.add(Restrictions.ge("num",8));
        conditions.add(Restrictions.le("num", 130));
        conditions.add(Restrictions.ne("address", "这个地址"));
        Assert.assertEquals("(count = ? AND name = ? AND age >= ? AND age < ? AND num >= ? AND num <= ? AND address <> ?)", conditions.toJdbcSql());
        System.out.println(conditions.getParameters());

        Conditions conditions1 = new Conditions();
        conditions1.eq("name", "名字")
                .ge("age", 18)
                .lt("age", 30)
                .ne("address", "这个地址");
        Assert.assertEquals("(name = ? AND age >= ? AND age < ? AND address <> ?)", conditions1.toJdbcSql());
        System.out.println(conditions1.getParameters());
    }
    @Test
    public void testBetweenExpression() {
        Conditions conditions = new Conditions();
        conditions.add(Restrictions.eq("count", 2));
        conditions.add(Restrictions.between("age",20,30));
        Assert.assertEquals("(count = ? AND age BETWEEN ? AND ?)", conditions.toJdbcSql());
        System.out.println(conditions.getParameters());
    }

    @Test
    public void testConjunction() {
        Conditions conditions = new Conditions();
        conditions.add(Restrictions.and(Restrictions.eq("age",18),Restrictions.eq("name","李四")));
        conditions.add(Restrictions.or(Restrictions.eq("count",18),Restrictions.eq("count",29)));
        Assert.assertEquals("((age = ? AND name = ?) AND (count = ? OR count = ?))", conditions.toJdbcSql());
        System.out.println(conditions.getParameters());
    }
    @Test
    public void testDisjunction() {
        Conditions c1 = new Conditions();
        c1.add(Restrictions.and(Restrictions.eq("age",18),Restrictions.eq("name","李四")));

        Conditions c2 = new Conditions();
        c2.add(Restrictions.or(Restrictions.eq("count",18),Restrictions.eq("count",29)));

        Junction conjunction = Restrictions.conjunction(c1, c2);
        Junction disjunction = Restrictions.disjunction(c1, c2);

        Assert.assertEquals("((age = ? AND name = ?) AND (count = ? OR count = ?))", conjunction.toJdbcSql());
        Assert.assertEquals("((age = ? AND name = ?) OR (count = ? OR count = ?))", disjunction.toJdbcSql());
    }
    @Test
    public void testLogic() {
        Conditions c1 = new Conditions();
        c1.add(Restrictions.and(Restrictions.eq("age",18),Restrictions.eq("name","李四")));

        Conditions c2 = new Conditions();
        c2.add(Restrictions.or(Restrictions.eq("count",18),Restrictions.eq("count",29)));

        LogicalExpression and = new LogicalExpression(c1, c2, "AND");
        LogicalExpression or = new LogicalExpression(c1, c2, "OR");

        Assert.assertEquals("((age = ? AND name = ?) AND (count = ? OR count = ?))", and.toJdbcSql());
        Assert.assertEquals("((age = ? AND name = ?) OR (count = ? OR count = ?))", or.toJdbcSql());
    }
    @Test
    public void testOneConjunction() {
        Conditions conditions = new Conditions();
        conditions.add(Restrictions.or(Restrictions.eq("count", 18), Restrictions.eq("count", 29)));
        Assert.assertEquals("(count = ? OR count = ?)", conditions.toJdbcSql());
        System.out.println(conditions.getParameters());
    }

    @Test
    public void testSqlRestriction() {
        Conditions conditions = new Conditions();

        conditions.add(Restrictions.conjunction(Restrictions.eq("xxx",1), Restrictions.eq("yyy", 2)));
        Assert.assertEquals("(xxx = ? AND yyy = ?)", conditions.toJdbcSql());
        System.out.println(conditions.getParameters());
    }
    @Test
    public void testMappedParameter() {
        Conditions conditions = new Conditions();

        conditions.add(Restrictions.mapped("t.name", Op.EQ, "name","熊诗言"));
        conditions.add(Restrictions.mapped("t.age", Op.GE, "age",12));

        Assert.assertEquals("(t.name = :name AND t.age >= :age)", conditions.toJdbcSql());
        System.out.println(conditions.getParameterMap());
        //System.out.println(conditions.toMybatisSql());
    }
}
